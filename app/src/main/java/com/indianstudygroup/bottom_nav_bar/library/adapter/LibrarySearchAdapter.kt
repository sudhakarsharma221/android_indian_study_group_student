package com.indianstudygroup.bottom_nav_bar.library.adapter

import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.library.LibraryDetailsActivity
import com.indianstudygroup.databinding.SearchItemLayoutBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import java.util.Locale

class LibrarySearchAdapter(
    val context: Context,
    val currentLatitude: Double,
    val currentLongitude: Double,
    val wishList: ArrayList<String>,
    private val list: ArrayList<LibraryResponseItem>,
    private val onItemClick: (String?) -> Unit
) : Adapter<LibrarySearchAdapter.MyViewHolder>() {


//    private var filteredSections: List<String> = list[0]
//    private var fullList: List<String> = list

//    fun filter(query: String?) {
//        if (query.isNullOrBlank()) {
//            filteredSections = fullList
//        } else {
//            filteredSections = fullList.filter {
//                it.lowercase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
//            }
//        }
//        notifyDataSetChanged()
//    }

//    fun updateList(newList: List<String>) {
//        fullList = newList
//        filteredSections = newList
//        notifyDataSetChanged()
//    }


    inner class MyViewHolder(val binding: SearchItemLayoutBinding) : ViewHolder(binding.root) {
        fun bindView(library: LibraryResponseItem, context: Context, position: Int) {
            binding.tvDistance.text = calculateDistance(
                library.address?.latitude?.toDouble(),
                library.address?.longitude?.toDouble(),
                currentLatitude,
                currentLongitude
            ).toString() + "km away"
            binding.tvLibName.text = library.name
            if (library.photo?.isNotEmpty() == true) {
                Glide.with(context).load(library.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.ivProfile)
            }

            binding.searchLayout.setOnClickListener {
                val intent = Intent(context, LibraryDetailsActivity::class.java)
                intent.putExtra("LibraryId", library.id)
                intent.putExtra("wishList", wishList)
                context.startActivity(intent)
            }
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
//            holder.itemView.setOnClickListener {
//                onItemClick(item)
//            }
        }
    }

    fun calculateDistance(lat1: Double?, lon1: Double?, lat2: Double?, lon2: Double?): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1 ?: 0.0, lon1 ?: 0.0, lat2 ?: 0.0, lon2 ?: 0.0, results)
        // Convert the distance from meters to kilometers


        return String.format("%.1f", results[0] / 1000).toFloat()
    }
}