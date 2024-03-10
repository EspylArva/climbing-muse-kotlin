package com.iteration.climbingmuse.ui.camera

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.iteration.climbingmuse.PoseLandmarkerHelper
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.*
import com.iteration.climbingmuse.app.PermissionsFragment
import com.iteration.climbingmuse.databinding.FragmentCameraBinding
import com.iteration.climbingmuse.ui.settings.SettingsViewModel
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener {
    private val videoProcessor: VideoProcessor = VideoProcessor()
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    private lateinit var backgroundExecutor: ExecutorService
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper

    private lateinit var preview: Preview
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    private val REQUEST_CODE_CAMERA_PERMISSION = 101


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init the background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()
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
            binding.liveFeed.post { setUpCamera() }

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
                if (this::poseLandmarkerHelper.isInitialized) {
                    if (poseLandmarkerHelper.isClose()) {
                        poseLandmarkerHelper.setupPoseLandmarker()
                    }
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
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases() {
        binding.liveFeed.display ?: return

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.liveFeed.display.rotation) // FIXME Seems to crash here
            .build()

        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build() //TODO allow LENS_FACING_FRONT

        // Attach the viewfinder's surface provider to preview use case
        preview.setSurfaceProvider(binding.liveFeed.surfaceProvider)


        // CameraProvider
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // Use cases
        // ImageAnalysis. Using RGBA 8888 to match how our models work
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

        val useCases = arrayOf(preview, imageAnalyzer) // TODO reintroduce imageAnalyzer (add imageAnalyzer to varargs)
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        // camera provides access to CameraControl & CameraInfo
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, *useCases)
    }

    private fun detectPose(imageProxy: ImageProxy) {
        if(this::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = false // cameraFacing == CameraSelector.LENS_FACING_FRONT
            )
        }
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
                //binding.bottomSheetLayout.inferenceTimeVal.text =
                //    String.format("%d ms", resultBundle.inferenceTime)

                videoProcessor.apply { decorators = arrayListOf(
                    JointDecorator(),
                    AngleDecorator(),
                    MuscleEngagementDecorator(),
                    GravityCenterDecorator()
                )}
                videoProcessor.decorate(resultBundle.results.first())

                // Pass necessary information to OverlayView for drawing on the canvas
                binding.overlay.setResults(
                    videoProcessor.decorators,
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

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