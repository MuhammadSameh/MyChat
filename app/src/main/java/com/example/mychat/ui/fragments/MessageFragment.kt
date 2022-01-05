package com.example.mychat.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.adapters.MessageAdapter
import com.example.mychat.firebaseUtils.Constants.FLAG_SEND
import com.example.mychat.models.Message
import com.example.mychat.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_message.*
import java.util.*

class MessageFragment: Fragment(R.layout.fragment_message) {


    private val args: MessageFragmentArgs by navArgs()
    lateinit var messageAdapter: MessageAdapter
    private val viewModel: MainViewModel by viewModels()
    lateinit var pickPic: ActivityResultLauncher<String>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.userClicked
        username_ontoolbar_message.text = user.name
        if (user.picturePath != "default"){
            Glide.with(requireContext()).load(user.picturePath).into(profile_image_toolbar_message)
        }
        pickPic = registerForActivityResult(ActivityResultContracts.GetContent()){
            viewModel.uploadToStorageAndSend(FLAG_SEND,it,user.Uid)
        }
        setUpRecycler()
        viewModel.messages.observe(viewLifecycleOwner, {
            Log.d("Message Fragment", "observed message change")
            messageAdapter.submitList(it)
            recyclerview_messages.scrollToPosition(messageAdapter.itemCount-1)
        })
        viewModel.getOldMessages(user.Uid)
        viewModel.attachListener(user.Uid)

        camera_gallery_btn.setOnClickListener {
            pickPic.launch("image/*")
        }

        send_messsage_btn.setOnClickListener {
           val message = edit_message_text.text.toString()
            if (message.isNotEmpty()){
                val messageObject = Message(message,viewModel.getCurrentUserId(),Calendar.getInstance().time)
                viewModel.sendMessage(user.Uid, messageObject)
            }
            edit_message_text.setText("")
        }



    }

    fun setUpRecycler(){
        recyclerview_messages.apply {
            messageAdapter = MessageAdapter()
            adapter  = messageAdapter
            layoutManager = LinearLayoutManager(requireContext())


        }
    }
}