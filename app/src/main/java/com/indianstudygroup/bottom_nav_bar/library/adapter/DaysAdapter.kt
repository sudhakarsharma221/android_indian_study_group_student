package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.DaysItemLayoutBinding
import com.indianstudygroup.databinding.LibraryShowItemLayoutBinding

class DaysAdapter(val context: Context, private val list: ArrayList<String>) :
    Adapter<DaysAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: DaysItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: String) {
            binding.textDay.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            DaysItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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