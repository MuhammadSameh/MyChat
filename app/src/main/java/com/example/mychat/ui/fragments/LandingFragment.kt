package com.example.mychat.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mychat.R
import com.example.mychat.databinding.LandingFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LandingFragment: Fragment(R.layout.landing_fragment) {
    lateinit var binding: LandingFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = LandingFragmentBinding.bind(view)
        FirebaseAuth.getInstance().currentUser?.let {
            findNavController().navigate(R.id.action_landingFragment_to_chatsFragment)
        }
        binding.lginBtnLanding.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_signInFragment)
        }

        binding.signupBtnLanding.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_signUpFragment)
        }
    }
}