package com.iteration.climbingmuse.ui.file

import android.net.Uri
import android.os.Bundle
import android.system.Os
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.common.net.MediaType
import com.iteration.climbingmuse.PoseLandmarkerHelper
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.FragmentFileExplorerBinding

class FileExplorerFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener  {

    private var _binding: FragmentFileExplorerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm = ViewModelProvider(this).get(FileExplorerViewModel::class.java)

        _binding = FragmentFileExplorerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        vm.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSelectFile.setOnClickListener {
            getContent.launch(arrayOf("image/*", "video/*"))
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let { mediaUri ->
                when (val mediaType = loadMediaType(mediaUri)) {
                    MediaType.ANY_IMAGE_TYPE -> {
                        Toast.makeText(requireContext(), "This is an image!", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.action_navigation_file_explorer_to_navigation_image_analysis)
                    }//runDetectionOnImage(mediaUri)
                    MediaType.ANY_VIDEO_TYPE -> Toast.makeText(requireContext(), "This is a video!", Toast.LENGTH_SHORT).show()//runDetectionOnVideo(mediaUri)
                    MediaType.ANY_TYPE -> {
                        //updateDisplayView(mediaType)
                        Toast.makeText(requireContext(), "Unsupported data type.", Toast.LENGTH_SHORT).show()
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

    private fun updateDisplayView(mediaType: MediaType) {
        // binding.imageResult.visibility = if (mediaType == MediaType.ANY_IMAGE_TYPE) View.VISIBLE else View.GONE
        // binding.videoView.visibility = if (mediaType == MediaType.ANY_VIDEO_TYPE) View.VISIBLE else View.GONE
        // binding.tvPlaceholder.visibility = if (mediaType == MediaType.ANY_TYPE) View.VISIBLE else View.GONE
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