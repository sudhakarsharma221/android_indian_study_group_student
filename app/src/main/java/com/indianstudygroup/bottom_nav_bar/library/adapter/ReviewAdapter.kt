package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.databinding.ActivityReviewBinding
import com.indianstudygroup.databinding.ReviewItemLayoutBinding
import com.indianstudygroup.libraryDetailsApi.model.Reviews
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)

class ReviewAdapter(val context: Context, private val list: ArrayList<Reviews>) :
    Adapter<ReviewAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: ReviewItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: Reviews, context: Context, position: Int) {
            binding.name.text = item.userName
            binding.message.text = item.message
            Glide.with(context).load(item.photo).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.ivProfile)
            binding.date.text = convertDate(item.date!!)
        }
    }

    fun convertDate(inputDate: String): String {
        // Define the formatter for the input date
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        // Parse the input date string to LocalDateTime
        val dateTime = LocalDateTime.parse(inputDate, inputFormatter)

        // Define the formatter for the output date
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Format the LocalDateTime to the desired output format
        return dateTime.format(outputFormatter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ReviewItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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