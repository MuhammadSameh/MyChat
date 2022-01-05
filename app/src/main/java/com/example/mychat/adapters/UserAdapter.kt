package com.example.mychat.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.layoutofusers.view.*

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    val diffCallback = object : DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.Uid == newItem.Uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: MutableList<User?>){
        differ.submitList(list)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layoutofusers
                ,parent
                ,false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = differ.currentList[position]
        if(user.Uid == FirebaseAuth.getInstance().currentUser?.uid){
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        }
        holder.itemView.apply {
            visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            username_userfrag.text = user.name
            if(user.picturePath.equals("default")){
                image_user_userfrag.setImageResource(R.drawable.user)
            }else {
                Glide.with(this).load(user.picturePath).into(image_user_userfrag)
            }
            status_user_item.text = user.status
            setOnClickListener {
                onClickListener?.let {
                    it(user)
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onClickListener: ((User) -> Unit)? = null

    fun setClickListener (listener: ((User) -> Unit)? ){
        onClickListener = listener
    }

    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}