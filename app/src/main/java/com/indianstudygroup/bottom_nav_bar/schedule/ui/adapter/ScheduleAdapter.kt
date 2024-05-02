package com.indianstudygroup.bottom_nav_bar.schedule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.NotificationItemLayoutBinding
import com.indianstudygroup.databinding.ScheduleItemLayoutBinding

class ScheduleAdapter(val context: Context, val list: ArrayList<String>) :
    Adapter<ScheduleAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: ScheduleItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: String, context: Context, position: Int) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ScheduleItemLayoutBinding.inflate(
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