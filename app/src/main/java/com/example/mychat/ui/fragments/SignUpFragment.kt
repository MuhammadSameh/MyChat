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
import kotlinx.android.synthetic.main.signup_fragment.*
import kotlinx.android.synthetic.main.signup_fragment.et_name_login
import kotlinx.android.synthetic.main.signup_fragment.et_password_login
import java.lang.Exception

class SignUpFragment: Fragment(R.layout.signup_fragment) {

    private val viewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnsignup_signUpActivity.setOnClickListener {
            val email = et_name_login.text.toString()
            val password = et_password_login.text.toString()
            val name = et_name.text.toString()
            viewModel.registerUser(email, password, name)

        }

        viewModel.creatUserResult.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_chatsFragment)

                }
                is Resource.Error -> {
                    it.message?.let {
                        Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                    }

                }
            }
        })

        tv_signin_signupActivity.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }
}