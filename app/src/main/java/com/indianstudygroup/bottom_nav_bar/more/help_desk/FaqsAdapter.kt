package com.indianstudygroup.bottom_nav_bar.more.help_desk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.FaqsItemLayoutBinding
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.model.Faqs

class FaqsAdapter(val context: Context, private val list: ArrayList<Faqs>) :
    Adapter<FaqsAdapter.MyViewHolder>() {
    private var isExpanded = false

    inner class MyViewHolder(val binding: FaqsItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: Faqs, context: Context, position: Int) {
            binding.apply {
                tvQuestion.text = item.question
                tvAnswer.text = item.answer

                faqsLayout.setOnClickListener {
                    if (!isExpanded) {
                        binding.tvAnswer.visibility = View.VISIBLE
                        binding.imageView2.rotation = 270f
                        isExpanded = true
                    } else {
                        binding.tvAnswer.visibility = View.GONE
                        binding.imageView2.rotation = 90f
                        isExpanded = false
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            FaqsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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