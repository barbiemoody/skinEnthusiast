package com.capstone.skinenthusiast.ui.main.ui.skincam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.skinenthusiast.databinding.FragmentSkincamBinding
import com.capstone.skinenthusiast.ui.camera.CameraActivity

class SkinCamFragment : Fragment() {

    private var _binding: FragmentSkincamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSkincamBinding.inflate(inflater, container, false)

        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.btnStart.setOnClickListener {
            if (checkImagePermission()) {
                val iCamera = Intent(requireActivity(), CameraActivity::class.java)
                startActivity(iCamera)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    REQUIRED_CAMERA_PERMISSION,
                    REQUEST_CODE_PERMISSIONS
                )

                if (checkImagePermission()) {
                    val iCamera = Intent(requireActivity(), CameraActivity::class.java)
                    startActivity(iCamera)
                }
            }
        }
    }

    private fun checkImagePermission() = REQUIRED_CAMERA_PERMISSION.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 101
    }
}