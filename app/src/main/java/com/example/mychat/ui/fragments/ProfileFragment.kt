package com.example.mychat.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.firebaseUtils.Constants.FLAG_UPDATE
import com.example.mychat.models.User
import com.example.mychat.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private val viewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCurrentUser(viewModel.getCurrentUserId()) {
            loadUser(it)
        }
        val pickPic = registerForActivityResult(ActivityResultContracts.GetContent()){
            it?.let {
                profile_frag_image.setImageURI(it)
                viewModel.uploadToStorageAndSend(FLAG_UPDATE,it)
            }
        }
        button.setOnClickListener {
        val status = status_profile_fragment.text.toString()
            viewModel.updateUser(status = status)
        }
        profile_frag_image.setOnClickListener {
            pickPic.launch("image/*")
        }
        logout_btn_profile.setOnClickListener {
            viewModel.logoutUser()
            findNavController().navigate(R.id.action_profileFragment_to_landingFragment)
        }
    }

    private fun loadUser(user: User){
        if (user.picturePath != "default"){
            Glide.with(requireContext()).load(user.picturePath).into(profile_frag_image)
        }
        status_profile_fragment.setText(user.status)
        username_profile_frag.text = user.name
    }
}