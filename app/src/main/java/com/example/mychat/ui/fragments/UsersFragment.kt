package com.example.mychat.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.R
import com.example.mychat.adapters.UserAdapter
import com.example.mychat.viewmodels.MainViewModel
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.users_fragment.*

class UsersFragment: Fragment(R.layout.users_fragment) {
    val viewModel: MainViewModel by viewModels()
    lateinit var userAdapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        userAdapter.setClickListener { user ->
            val bundle = Bundle().apply {
                putSerializable("userClicked", user )
            }
            findNavController().navigate(R.id.action_usersFragment_to_messageFragment, bundle)
        }
       viewModel.users.observe(viewLifecycleOwner, Observer {
           userAdapter.submitList(it)
       })

        viewModel.getAllUsers()


    }

    private fun setupRecycler() {
        rv_users.apply {
            userAdapter = UserAdapter()
            adapter = userAdapter

            layoutManager = LinearLayoutManager(requireContext())
        }
    }


}