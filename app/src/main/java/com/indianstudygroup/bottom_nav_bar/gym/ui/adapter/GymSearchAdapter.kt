package com.indianstudygroup.bottom_nav_bar.gym.ui.adapter

import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.bottom_nav_bar.gym.model.GymResponseItem
import com.indianstudygroup.bottom_nav_bar.gym.ui.GymDetailsActivity
import com.indianstudygroup.databinding.SearchItemLayoutBinding
import java.util.Locale

class GymSearchAdapter(
    val context: Context,
    val locationPermission: Boolean,
    val currentLatitude: Double,
    val currentLongitude: Double,
    val wishList: ArrayList<String>,
    private val list: List<GymResponseItem>,
    private val onFilterResult: (Boolean) -> Unit,
    private val gymDetailsLauncher: (Intent) -> Unit

) : RecyclerView.Adapter<GymSearchAdapter.MyViewHolder>() {

    private var filteredList: List<GymResponseItem> = list

    inner class MyViewHolder(val binding: SearchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(gym: GymResponseItem, context: Context, position: Int) {

            if (locationPermission) {
                binding.tvDistance.text = calculateDistance(
                    gym.address?.latitude?.toDouble(),
                    gym.address?.longitude?.toDouble(),
                    currentLatitude,
                    currentLongitude
                ).toString() + "km away"
            } else {
                binding.tvDistance.text = "-- km away"
            }


            binding.tvLibName.text = gym.name
            if (gym.photo?.isNotEmpty() == true) {
                Glide.with(context).load(gym.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.ivProfile)
            }

            binding.searchLayout.setOnClickListener {
                val intent = Intent(context, GymDetailsActivity::class.java)
                intent.putExtra("GymId", gym.id)
                intent.putExtra("wishList", wishList)
                gymDetailsLauncher(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(filteredList[position], context, position)

    }

    fun filter(query: String) {
        val searchText = query.toLowerCase(Locale.getDefault())
        filteredList = if (searchText.isEmpty()) {
            list
        } else {
            list.filter { gym ->
                gym.name?.toLowerCase(Locale.getDefault())?.contains(searchText) == true
            }
        }
        onFilterResult(filteredList.isEmpty()) // Notify the fragment
        notifyDataSetChanged()
    }


    fun calculateDistance(lat1: Double?, lon1: Double?, lat2: Double?, lon2: Double?): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1 ?: 0.0, lon1 ?: 0.0, lat2 ?: 0.0, lon2 ?: 0.0, results)
        // Convert the distance from meters to kilometers


        return String.format("%.1f", results[0] / 1000).toFloat()
    }
}