package com.indianstudygroup.book_seat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.R
import com.indianstudygroup.databinding.SeatsItemLayoutBinding

class SeatAdapter(
    val context: Context,
    private val totalSeats: Int,
    private val vacantSeats: Int,
    private val onSeatSelected: (Int) -> Unit
) : RecyclerView.Adapter<SeatAdapter.MyViewHolder>() {

    private var selectedSeatIndex: Int? = null

    inner class MyViewHolder(val binding: SeatsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SeatsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return totalSeats
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val isVacant = position < vacantSeats

        // Set seat color
        holder.binding.imageView.setColorFilter(
            if (isVacant) {
                if (selectedSeatIndex == position) context.resources.getColor(R.color.blue1)
                else context.resources.getColor(R.color.green)
            } else context.resources.getColor(R.color.drawable_color)
        )

        holder.itemView.setOnClickListener {
            if (isVacant) {
                // Handle vacant seat click
                selectedSeatIndex = if (selectedSeatIndex == position) null else position
                onSeatSelected(selectedSeatIndex ?: -1)
                notifyDataSetChanged()
            }
        }

        // Deselect the previously selected seat if the position matches the selectedSeatIndex
        if (selectedSeatIndex == position) {
            holder.binding.imageView.setColorFilter(
                context.resources.getColor(R.color.blue1)
            )
        }
    }
}