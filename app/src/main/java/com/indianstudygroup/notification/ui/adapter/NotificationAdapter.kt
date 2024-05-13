package com.indianstudygroup.notification.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.NotificationItemLayoutBinding
import com.indianstudygroup.notification.model.NotificationResponseModel

class NotificationAdapter(
    val context: Context, private val list: ArrayList<NotificationResponseModel>
) : Adapter<NotificationAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: NotificationItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bindView(item: NotificationResponseModel, context: Context, position: Int) {
            binding.tvHeading.text = item.heading
            binding.tvSubHeading.text = item.subHeading
            if (item.isNew == true) {
                binding.newNotification.visibility = View.VISIBLE
            } else {
                binding.newNotification.visibility = View.GONE
            }

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