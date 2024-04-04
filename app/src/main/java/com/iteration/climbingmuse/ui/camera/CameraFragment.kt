package com.iteration.climbingmuse.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.*
import com.iteration.climbingmuse.app.PermissionsFragment
import com.iteration.climbingmuse.databinding.FragmentCameraBinding
import com.iteration.climbingmuse.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var backgroundExecutor: ExecutorService
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper

    // Interface for Computer Vision processing
    private val videoProcessor: VideoProcessor = VideoProcessor()

    /// CameraX
    // Camera
    private var camera: Camera? = null
    // Preview for the camera
    private lateinit var preview: Preview
    // CameraX use cases
    private var imageAnalyzer: ImageAnalysis? = null
    private var videoCapturer: VideoCapture<Recorder>? = null

    private val qualitySelector = QualitySelector.fromOrderedList(
        listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD))
    private var recording: Recording? = null
    ///

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init the background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        setFabButtonsListeners()

        videoProcessor.decorators = arrayListOf(
            AngleDecorator(viewModel.showAngles),
            GravityCenterDecorator(viewModel.showCOGMarker, viewModel.showCOGTrail, viewModel.showBalanceMarker),
            JointDecorator(viewModel.showJointMarkers),
            MuscleEngagementDecorator(viewModel.showMuscleMarkers, viewModel.showMuscleEngagement)
        )


    }

    private fun setFabButtonsListeners() {
        binding.fabToggleCv.setOnClickListener {
            val cvEnabled = binding.chipCv.visibility == View.VISIBLE
            if(cvEnabled) {
                // Stop recording
                binding.chipCv.visibility = View.GONE
            } else {
                // Start recording
                binding.chipCv.visibility = View.VISIBLE
                Snackbar.make(binding.root, resources.getString(R.string.enable_cv), Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.fabToggleRecord.setOnClickListener {
            if(recording != null) {
                // Stop recording
                recording!!.stop()
                binding.chipRecording.visibility = View.GONE
            } else {
                // Start recording
                recordVideo()
                binding.chipRecording.visibility = View.VISIBLE
            }
        }

        binding.fabToggleInferenceTime.setOnClickListener {
            val isToggled = binding.chipInference.visibility == View.VISIBLE
            if(isToggled) {
                // Stop recording
                binding.chipInference.visibility = View.GONE
            } else {
                // Start recording
                binding.chipInference.visibility = View.VISIBLE
            }
        }

        binding.fabToggleChips.setOnClickListener {
            val isVisible = binding.chipsContainer.visibility == View.VISIBLE
            if(isVisible) {
                // Stop recording
                binding.chipsContainer.visibility = View.GONE
            } else {
                // Start recording
                binding.chipsContainer.visibility = View.VISIBLE
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            val permissionDenied = requireContext().getSharedPreferences(
                getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
                .getBoolean(getString(R.string.camera_permission_denied), false)
            Timber.w(
                "No permission for camera. Permission has already been denied once: %s",
                permissionDenied
            )

            if (permissionDenied) {
                displayExplanationView()
            } else {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_navigation_camera_to_navigation_permissions)
            }
        } else {
            // Wait for the views to be properly laid out
            binding.liveFeed.post { lifecycleScope.launch { setUpCamera() } }

            // Create the PoseLandmarkerHelper that will handle the inference
            backgroundExecutor.execute {
                poseLandmarkerHelper = PoseLandmarkerHelper(
                    context = requireContext(),
                    runningMode = RunningMode.LIVE_STREAM,
                    minPoseDetectionConfidence = viewModel.currentMinPoseDetectionConfidence,
                    minPoseTrackingConfidence = viewModel.currentMinPoseTrackingConfidence,
                    minPosePresenceConfidence = viewModel.currentMinPosePresenceConfidence,
                    currentDelegate = viewModel.currentDelegate,
                    poseLandmarkerHelperListener = this
                )
            }

            // Start the PoseLandmarkerHelper again when users come back
            // to the foreground.
            backgroundExecutor.execute {
                if (this::poseLandmarkerHelper.isInitialized && poseLandmarkerHelper.isClose()) {
                    poseLandmarkerHelper.setupPoseLandmarker()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(this::poseLandmarkerHelper.isInitialized) {
            viewModel.setMinPoseDetectionConfidence(poseLandmarkerHelper.minPoseDetectionConfidence)
            viewModel.setMinPoseTrackingConfidence(poseLandmarkerHelper.minPoseTrackingConfidence)
            viewModel.setMinPosePresenceConfidence(poseLandmarkerHelper.minPosePresenceConfidence)
            viewModel.setDelegate(poseLandmarkerHelper.currentDelegate)

            // Close the PoseLandmarkerHelper and release resources
            backgroundExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
                // CameraProvider
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.Builder().requireLensFacing(viewModel.cameraSelection.value!!).build()

                // Build and bind the camera use cases
                bindCameraUseCases(cameraProvider, cameraSelector)
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider, cameraSelector: CameraSelector) {
        // # Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.liveFeed.display.rotation)
            .build()
            .also {
                // Attach the viewfinder's surface provider to preview use case
                it.setSurfaceProvider(binding.liveFeed.surfaceProvider)
            }

        // # ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.liveFeed.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        detectPose(image)
                    }
                }

        // # VideoCapture
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        videoCapturer = VideoCapture.withOutput(recorder)
//        videoCapturer = VideoCapture.Builder(recorder)
//            .setMirrorMode(MIRROR_MODE_ON_FRONT_ONLY) // FIXME: This requires CameraX 1.3, which in turn requires SDK 34
//            .build()

        val useCases = arrayOf(preview, imageAnalyzer, videoCapturer)
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        if(camera != null) {
            camera!!.cameraInfo.cameraState.removeObservers(viewLifecycleOwner)
        }

        // camera provides access to CameraControl & CameraInfo
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, *useCases)
    }

    private fun detectPose(imageProxy: ImageProxy) {
        if(this::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = viewModel.cameraSelection.value == CameraSelector.LENS_FACING_FRONT
            )
        }
    }

    private fun recordVideo() {
        // Create MediaStoreOutputOptions for our recorder
        val timestamp = SimpleDateFormat("ddMMMyy_HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        val filename = "ClimbingMuse_$timestamp"
        val appDir = "${Environment.DIRECTORY_MOVIES}/${resources.getString(R.string.app_name)}"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, filename)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, appDir)
            }
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(requireContext().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        // Configure Recorder and Start recording to the mediaStoreOutput.

        val captureListener = Consumer<VideoRecordEvent> { event ->
            when(event) {
                is VideoRecordEvent.Start -> {
                    Snackbar.make(binding.root, resources.getString(R.string.recording_started), Snackbar.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize -> {
                    Snackbar.make(binding.root, resources.getString(R.string.recording_started).format(filename), Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_delete) {
                            requireContext().contentResolver.delete(mediaStoreOutput.collectionUri, null, null)
                            Timber.d("Deleting ${mediaStoreOutput.collectionUri}")
                        }
                        .show()
                }
            }

        }

        Timber.d("Start recording - file is $filename")
        recording = videoCapturer?.output
            ?.prepareRecording(requireContext(), mediaStoreOutput)
            ?.apply {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            ?.start(ContextCompat.getMainExecutor(requireContext()), captureListener)
    }

    private fun displayExplanationView() {
        // FIXME: This view does not disappear if permissions are given when app is paused
        binding.root.addView(TextView(requireContext()).apply {
            text = resources.getText(R.string.camera_permission_justification)
            textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }
            textSize = resources.getDimension(R.dimen.medium_text)
            layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    topToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                    bottomToBottom = ConstraintSet.PARENT_ID
                }
            setPadding(
                resources.getDimension(R.dimen.default_padding).toInt(),
                resources.getDimension(R.dimen.default_padding).toInt(),
                resources.getDimension(R.dimen.default_padding).toInt(),
                resources.getDimension(R.dimen.default_padding).toInt()
            )
        })
    }

    override fun onError(error: String, errorCode: Int) {
        Timber.e("[Error#%s] %s", errorCode, error)
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        // display on overlay
        activity?.runOnUiThread {
            if (_binding != null) {
                Timber.d(viewModel.toString())

                videoProcessor.processData(resultBundle.results.first())

                // Pass necessary information to OverlayView for drawing on the canvas
                binding.overlay.setResults(
                    videoProcessor.decorators,
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                binding.chipInference.text = resources.getString(R.string.chip_inference, resultBundle.inferenceTime.toString().padStart(3, '0'))

                // Force a redraw
                binding.overlay.invalidate()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            binding.liveFeed.display.rotation
    }
}