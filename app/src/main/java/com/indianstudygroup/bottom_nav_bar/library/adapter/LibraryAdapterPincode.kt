package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.bottom_nav_bar.library.LibraryDetailsActivity
import com.indianstudygroup.databinding.LibraryShowItemLayoutBinding

class LibraryAdapterPincode(
    val context: Context, val list: ArrayList<LibraryResponseItem>
) : Adapter<LibraryAdapterPincode.MyViewHolder>() {
    inner class MyViewHolder(val binding: LibraryShowItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(library: LibraryResponseItem, context: Context, position: Int) {

//            binding.favourite.setOnClickListener {
//                binding.favourite.setCardBackgroundColor(Color.RED)
//            }
            binding.tvName.text = library.name
            binding.tvPrice.text = HtmlCompat.fromHtml(
                "<b><font color='#5669FF'>₹${library.pricing?.daily}</font></b> /Day<br/>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(context).load(library.photo).placeholder(R.drawable.noimage)
                .error(R.drawable.noimage).into(binding.imageView)
            binding.layoutView.setOnClickListener {
                val intent = Intent(context, LibraryDetailsActivity::class.java)
                intent.putExtra("LibraryId", library.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LibraryShowItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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