package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.DaysItemLayoutBinding

class DaysAdapter(
    val context: Context,
    private val presentList: ArrayList<String>,
    private val totalDaysList: ArrayList<String>
) : Adapter<DaysAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: DaysItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: String) {
            binding.textDay.text = item

            for (day in presentList) {
                if (day == item) {
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#5669FF"))
                    binding.textDay.setTextColor(Color.parseColor("#FFFFFFFF"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            DaysItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return totalDaysList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        totalDaysList[position].let {
            holder.bindView(it)
        }
    }
}