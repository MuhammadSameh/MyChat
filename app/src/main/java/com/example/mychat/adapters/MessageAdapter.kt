package com.example.mychat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.models.Message
import com.example.mychat.models.MessageType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.chat_item_left.view.*
import kotlinx.android.synthetic.main.chat_item_left.view.show_message
import kotlinx.android.synthetic.main.chat_item_right.view.*
import kotlinx.android.synthetic.main.image_item_left.view.*
import kotlinx.android.synthetic.main.image_text_item.view.*
import java.text.SimpleDateFormat

class MessageAdapter: RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val TEXT_MESSAGE_RIGHT = 0
    private val TEXT_MESSAGE_LEFT = 1
    private val IMAGE_MESSAGE_RIGHT = 2
    private val IMAGE_MESSAGE_LEFT = 3

    val diffCallback = object : DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: MutableList<Message?>){
        differ.submitList(list)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            TEXT_MESSAGE_LEFT -> {
                MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left
                    ,
                    parent
                    ,
                    false))

            }
            TEXT_MESSAGE_RIGHT -> {
                MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right,parent,false))
            }
            IMAGE_MESSAGE_LEFT -> {
                MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_item_left,parent,false))
            }
            else -> MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_text_item,parent,false))
        }

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = differ.currentList[position]
        val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT,SimpleDateFormat.SHORT)
        when {
            getItemViewType(position) == TEXT_MESSAGE_RIGHT -> {
                holder.itemView.apply {
                    show_message_right.text = message.content
                    date.text = dateFormat.format(message.time!!)
                }
            }
            getItemViewType(position) == TEXT_MESSAGE_LEFT -> {
                holder.itemView.apply {
                    show_message.text = message.content
                    dateLeft.text = dateFormat.format(message.time!!)
                }
            }
            getItemViewType(position) == IMAGE_MESSAGE_LEFT -> {
                holder.itemView.apply {
                    Glide.with(this).load(message.content).into(Image_message_left)
                    image_date_left.text = dateFormat.format(message.time!!)
                }

            }
            else -> {
                holder.itemView.apply{
                    Glide.with(this).load(message.content).into(Image_message)
                    image_date.text = dateFormat.format(message.time!!)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val message = differ.currentList[position]
        val user = FirebaseAuth.getInstance().currentUser
        return if (message.senderId == user?.uid){
            if (message.type == MessageType.IMAGE)
                return IMAGE_MESSAGE_RIGHT
            TEXT_MESSAGE_RIGHT
        } else{
            if (message.type == MessageType.TEXT)
                return TEXT_MESSAGE_LEFT
            IMAGE_MESSAGE_LEFT
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}