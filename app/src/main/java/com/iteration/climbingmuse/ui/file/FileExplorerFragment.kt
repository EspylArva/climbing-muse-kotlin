package com.iteration.climbingmuse.ui.file


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.system.Os
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.common.net.MediaType
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper
import com.iteration.climbingmuse.databinding.FragmentFileExplorerBinding
import timber.log.Timber
import java.util.concurrent.Executors

class FileExplorerFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener  {

    private var _binding: FragmentFileExplorerBinding? = null
    private val vm: FileExplorerViewModel by activityViewModels()

    private val ONLINE_URI = "https://shorturl.at/wY25e" // FIXME: Random video found on internet
    private val OFFLINE_URI = R.raw.janja

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileExplorerBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val textView: TextView = binding.textDashboard
        vm.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        observe()
        setVideo()
        setFabButtonsListeners()



        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.switchResourceType.setOnClickListener {
            Timber.d("Currently checked: ${binding.switchResourceType.isChecked}")
            vm.useOnline.postValue(binding.switchResourceType.isChecked)
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let { mediaUri ->
                when (val mediaType = loadMediaType(mediaUri)) {
                    MediaType.ANY_IMAGE_TYPE -> {
                        Toast.makeText(requireContext(), "This is an image! URI: $mediaUri", Toast.LENGTH_SHORT).show()
                        // TODO: Do everything here
                    }
                    MediaType.ANY_VIDEO_TYPE -> {
                        Toast.makeText(requireContext(), "This is a video! URI: $mediaUri", Toast.LENGTH_SHORT).show()
                        // TODO: Add detection

                        // Show on VideoView
                        binding.videoDisplay.setVideoURI(mediaUri)
                        binding.videoDisplay.start()
                    }
                    MediaType.ANY_TYPE -> {
                        Toast.makeText(requireContext(), "This is a neither a video nor an image! Please select a video or an image.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun loadMediaType(uri: Uri): MediaType {
        val mimeType = context?.contentResolver?.getType(uri)
        mimeType?.let {
            if (mimeType.startsWith("image")) return MediaType.ANY_IMAGE_TYPE
            if (mimeType.startsWith("video")) return MediaType.ANY_VIDEO_TYPE
        }

        return MediaType.ANY_TYPE
    }

    private fun setFabButtonsListeners() {
        binding.buttonSelectFile.setOnClickListener {
            getContent.launch(arrayOf("image/*", "video/*"))
        }
    }

    private fun setVideo() {
        val video = binding.videoDisplay
        val uri: Uri = if (vm.useOnline.value!!) {
            Uri.parse(ONLINE_URI)
        } else {
            Uri.parse("android.resource://com.iteration.climbingmuse/${OFFLINE_URI}")
        }
        Timber.d("Use online: ${vm.useOnline.value} ==> uri=${uri.path}")

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoDisplay)

        video.setMediaController(mediaController)
        video.setVideoURI(uri)
        video.requestFocus()
        video.start()
    }

    private fun observe() {
        val sp = requireContext().getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)

        vm.useOnline.observe(viewLifecycleOwner) {
            sp.edit().putBoolean(resources.getString(R.string.sp_use_online), it).apply()
            setVideo()
        }
        vm.text.observe(viewLifecycleOwner) { binding.textDashboard.text = it }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onError(error: String, errorCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        TODO("Not yet implemented")
    }
}