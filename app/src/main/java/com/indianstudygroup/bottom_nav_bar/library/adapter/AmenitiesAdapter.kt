package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.AmenitiesItemLayoutBinding
import com.indianstudygroup.libraryDetailsApi.model.AmenityItem

class AmenitiesAdapter(private val context: Context, private val list: List<AmenityItem>) :
    Adapter<AmenitiesAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: AmenitiesItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: AmenityItem) {
            binding.ivDrawable.setImageDrawable(item.drawable)
            binding.tvLabel.text = item.label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AmenitiesItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list[position].let {
            holder.bindView(it)
        }
    }
}