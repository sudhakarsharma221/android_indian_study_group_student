package com.indianstudygroup.notification.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.NotificationItemLayoutBinding

class NotificationAdapter(val context: Context, val list: ArrayList<String>) :
    Adapter<NotificationAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: NotificationItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bindView(item: String, context: Context, position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NotificationItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list[position].let {
            holder.bindView(it, context, position)
        }
    }
}