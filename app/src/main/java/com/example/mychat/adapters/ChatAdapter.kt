package com.example.mychat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.models.Chat
import com.example.mychat.models.MessageType
import kotlinx.android.synthetic.main.chat_item.view.*
import java.text.SimpleDateFormat

class ChatAdapter: RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {



    private val diffCallback = object : DiffUtil.ItemCallback<Chat>(){
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: MutableList<Chat?>){
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.chat_item,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = differ.currentList[position]
        val dateFormat = SimpleDateFormat.getDateTimeInstance(
            SimpleDateFormat.SHORT,
            SimpleDateFormat.SHORT)
        holder.itemView.apply {
            if (chat.user!!.picturePath != "default"){
                Glide.with(this).load(chat.user!!.picturePath).into(image_chatitem)
            }
            if (chat.lastMessage?.type == MessageType.IMAGE){
                last_message_chatitem.text = "Image"
            } else {
                last_message_chatitem.text = chat.lastMessage?.content
            }
            lastMessageDate_chatItem.text = dateFormat.format(chat.lastMessage?.time!!)
            name_chat_item.text = chat.user?.name

                setOnClickListener {
                    onChatClickListener?.let { it1 -> it1(chat) }
                }

        }
    }

    var onChatClickListener: ((chat: Chat) -> Unit)? = null

    fun setOnChatListener(listener: ((chat: Chat) -> Unit)?){
        onChatClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}