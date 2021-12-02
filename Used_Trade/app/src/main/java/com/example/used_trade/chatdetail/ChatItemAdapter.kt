package com.example.used_trade.chatdetail

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.used_trade.R
import com.example.used_trade.databinding.ItemChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatItemAdapter: ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    private lateinit var context: Context

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(chatItem: ChatItem) {
            if(auth.currentUser?.email == chatItem.sendId) {
                binding.messageTextView.background = context.resources.getDrawable(R.drawable.background_chat_yellow)
                binding.chatLinearLayout.gravity = Gravity.END
            } else {
                binding.messageTextView.background = context.resources.getDrawable(R.drawable.background_chat_white)
                binding.chatLinearLayout.gravity = Gravity.START
            }
            binding.senderTextView.text = chatItem.sendId
            binding.messageTextView.text = chatItem.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}