package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.SearchItemLayoutBinding
import java.util.Locale

class LibrarySearchAdapter(
    val context: Context, val list: ArrayList<String>, private val onItemClick: (String?) -> Unit
) : Adapter<LibrarySearchAdapter.MyViewHolder>() {


    private var filteredSections: List<String> = list
    private var fullList: List<String> = list

    fun filter(query: String?) {
        if (query.isNullOrBlank()) {
            filteredSections = fullList
        } else {
            filteredSections = fullList.filter {
                it.lowercase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
            }
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<String>) {
        fullList = newList
        filteredSections = newList
        notifyDataSetChanged()
    }


    inner class MyViewHolder(val binding: SearchItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(item: String, context: Context, position: Int) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list[position].let { item ->
            holder.bindView(item, context, position)
            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}