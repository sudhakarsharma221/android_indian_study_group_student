package com.indianstudygroup.bottom_nav_bar.schedule.library.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModelItem
import com.indianstudygroup.bottom_nav_bar.schedule.library.ui.ScheduleDetailsActivity
import com.indianstudygroup.databinding.ScheduleItemLayoutBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LibraryScheduleAdapter(
    val context: Context, private val list: ArrayList<LibraryScheduleResponseModelItem>
) : Adapter<LibraryScheduleAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: ScheduleItemLayoutBinding) : ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(item: LibraryScheduleResponseModelItem, context: Context, position: Int) {

            binding.tvName.text = item.name
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date = LocalDateTime.parse(item.date, formatter)
            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

            binding.tvDate.text = "$formattedDate - $formattedDate"
            if (item.address?.pincode == null) {
                binding.tvAdress.visibility = View.GONE
            } else {
                binding.tvAdress.visibility = View.VISIBLE

                binding.tvAdress.text =
                    "${item?.address?.street}, ${item?.address?.district}, ${item?.address?.state}, ${item?.address?.pincode}"
            }

            if (item.pricing?.daily == null) {
                binding.tvPrice.visibility = View.GONE
            } else {
                binding.tvPrice.visibility = View.VISIBLE

                binding.tvPrice.text = "₹ ${item.pricing?.daily}"
            }

//            binding.tvDate.text = ""
            if (item.photo?.isNotEmpty() == true) {
                Glide.with(context).load(item.photo[0]).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.libImage)
            }
            binding.scheduleLayout.setOnClickListener {
                val intent = Intent(context, ScheduleDetailsActivity::class.java)
                intent.putExtra("ScheduleData", item)
                context.startActivity(intent)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list[position].let {
            holder.bindView(it, context, position)
        }
    }
}