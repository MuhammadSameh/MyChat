package com.example.mychat.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychat.R
import com.example.mychat.adapters.ChatAdapter
import com.example.mychat.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.chats_fragment.*

class ChatsFragment : Fragment(R.layout.chats_fragment) {
    lateinit var chatAdapter: ChatAdapter
    private val viewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatAdapter = ChatAdapter()
        setupRecycler()
        chatAdapter.setOnChatListener {
            val user = it.user
            val bundle = Bundle().apply {
                putSerializable("userClicked", user)
            }
            findNavController().navigate(R.id.action_chatsFragment_to_messageFragment, bundle)
        }
        viewModel.chats.observe(viewLifecycleOwner, {
            chatAdapter.submitList(it)
        })
        viewModel.getAllChats()
    }

    fun setupRecycler() {
        chatsRecycler.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
    }
}