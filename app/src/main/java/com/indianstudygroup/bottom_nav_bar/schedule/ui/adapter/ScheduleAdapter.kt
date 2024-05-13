package com.indianstudygroup.bottom_nav_bar.schedule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.databinding.NotificationItemLayoutBinding
import com.indianstudygroup.databinding.ScheduleItemLayoutBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem

class ScheduleAdapter(val context: Context, private val list: ArrayList<LibraryResponseItem>) :
    Adapter<ScheduleAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: ScheduleItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: LibraryResponseItem, context: Context, position: Int) {

            binding.tvName.text = item.name
            binding.tvPrice.text = "₹ ${item.pricing?.daily}"

            if (item.photo?.isNotEmpty() == true) {
                Glide.with(context).load(item.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.libImage)
            }
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