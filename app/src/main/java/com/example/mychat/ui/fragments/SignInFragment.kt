package com.example.mychat.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mychat.R
import com.example.mychat.firebaseUtils.Resource
import com.example.mychat.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.signin_fragment.*
import java.lang.Exception

class SignInFragment : Fragment(R.layout.signin_fragment) {
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        btnsignin_signinActivity.setOnClickListener {
            val email = et_name_login.text.toString()
            val password = et_password_login.text.toString()
            viewModel.loginUser(email,password)




        }

        viewModel.loginUserResult.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_signInFragment_to_chatsFragment)

                }
                is Resource.Error -> {
                    it.message?.let {
                        Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                    }

                }
            }
        })



        tv_signUp_signinActivity.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }
}