package com.capstone.skinenthusiast.ui.main.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.FragmentProfileBinding
import com.capstone.skinenthusiast.ui.editprofile.EditProfileActivity
import com.capstone.skinenthusiast.ui.splash.SplashActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val viewModel by viewModel<ProfileViewModel>()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setProfileContents()
        setListeners()

        return binding.root
    }

    private fun setProfileContents() {
        binding.apply {
            viewModel.getAvatar().asLiveData().observe(viewLifecycleOwner) {
                android.util.Log.e("FTEST", "image -> $it")

                Glide.with(root.context)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivProfile)
            }

            viewModel.getName().asLiveData().observe(viewLifecycleOwner) {
                tvUsername.text = it
            }

            viewModel.getEmail().asLiveData().observe(viewLifecycleOwner) {
                tvEmail.text = it
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnEditProfile.setOnClickListener {
                startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
            }

            btnLogout.setOnClickListener {
                val iSplash = Intent(requireActivity(), SplashActivity::class.java)
                viewModel.logout()
                startActivity(iSplash)
                requireActivity().finishAffinity()
            }
        }
    }
}