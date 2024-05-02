package com.indianstudygroup.bottom_nav_bar.more.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.bottom_nav_bar.more.wallet.PaymentStatusActivity
import com.indianstudygroup.databinding.PaymentHistoryItemLayoutBinding

class PaymentHistoryAdapter(val context: Context, val list: ArrayList<String>) :
    Adapter<PaymentHistoryAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: PaymentHistoryItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bindView(item: String, context: Context, position: Int) {
            binding.paymentHistoryLayout.setOnClickListener {

                IntentUtil.startIntent(context, PaymentStatusActivity())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PaymentHistoryItemLayoutBinding.inflate(
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