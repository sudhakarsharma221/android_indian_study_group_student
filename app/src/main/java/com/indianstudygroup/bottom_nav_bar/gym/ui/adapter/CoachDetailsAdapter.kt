package com.indianstudygroup.bottom_nav_bar.gym.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.gym.model.Trainer
import com.indianstudygroup.bottom_nav_bar.library.adapter.ReviewAdapter
import com.indianstudygroup.databinding.CoachDetailItemLayoutBinding
import com.indianstudygroup.databinding.ReviewItemLayoutBinding
import com.indianstudygroup.libraryDetailsApi.model.Reviews
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CoachDetailsAdapter(val context: Context, private val list: ArrayList<Trainer>) :
    RecyclerView.Adapter<CoachDetailsAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: CoachDetailItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: Trainer, context: Context, position: Int) {
            binding.name.text = HtmlCompat.fromHtml(
                "<b>Name - </b>${item.name}",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            binding.certificate.text = HtmlCompat.fromHtml(
                "<b>Certificate Associated - </b>${item.certificate}",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CoachDetailItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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