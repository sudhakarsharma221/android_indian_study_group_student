package com.indianstudygroup.bottom_nav_bar.gym.ui.adapter

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.gym.model.GymResponseItem
import com.indianstudygroup.bottom_nav_bar.gym.ui.GymDetailsActivity
import com.indianstudygroup.databinding.LibraryShowItemLayoutBinding

class GymAdapterDistrict(
    val context: Context,
    val locationPermission: Boolean,
    val currentLatitude: Double,
    val currentLongitude: Double,
    private val list: List<GymResponseItem>,
    private val onNotFavouriteClickListener: (GymResponseItem) -> Unit,
    private val onFavouriteClickListener: (GymResponseItem) -> Unit,
    private val gymDetailsLauncher: (Intent) -> Unit

) : RecyclerView.Adapter<GymAdapterDistrict.MyViewHolder>() {
    inner class MyViewHolder(val binding: LibraryShowItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(gym: GymResponseItem, context: Context, position: Int) {


            val isFavourite = AppConstant.wishListGym.contains(gym.id)

            binding.favImage.setImageResource(
                if (isFavourite) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            binding.favourite.setOnClickListener {
                if (AppConstant.wishListGym.contains(gym.id)) {
                    AppConstant.wishListGym.remove(gym.id)
                    binding.favImage.setImageResource(R.drawable.baseline_favorite_border_24)
                    onNotFavouriteClickListener(gym)
                } else {
                    AppConstant.wishListGym.add(gym.id!!)
                    binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
                    onFavouriteClickListener(gym)
                }
            }
            var rating = 1f
            if (gym.rating?.count == 0) {
                binding.tvRating.text = "1.0"
            } else {
                if (gym.rating?.count == null) {
                    binding.tvRating.text = "1.0"
                } else {

                    rating = (gym.rating?.count?.toFloat()?.let {
                        gym.rating?.totalRatings?.toFloat()?.div(
                            it
                        )
                    })?.toFloat()!!

                }

            }
            binding.tvRating.text = String.format("%.1f", rating)
            if (locationPermission) {
                binding.tvDistance.text = calculateDistance(
                    gym.address?.latitude?.toDouble(),
                    gym.address?.longitude?.toDouble(),
                    currentLatitude,
                    currentLongitude
                )
            } else {
                binding.tvDistance.text = "-- km away"
            }

            binding.tvName.text = gym.name
            binding.tvPrice.text = HtmlCompat.fromHtml(
                "<b><font color='#5669FF'>₹${gym.pricing?.daily}</font></b> /Day<br/>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            if (gym.photo?.isNotEmpty() == true) {
                Glide.with(context).load(gym.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.imageView)
            }


            binding.layoutView.setOnClickListener {
                val intent = Intent(context, GymDetailsActivity::class.java)
                intent.putExtra("GymId", gym.id)
                gymDetailsLauncher(intent)
            }
        }
    }

    fun calculateDistance(lat1: Double?, lon1: Double?, lat2: Double?, lon2: Double?): String {
        val results = FloatArray(1)
        Location.distanceBetween(lat1 ?: 0.0, lon1 ?: 0.0, lat2 ?: 0.0, lon2 ?: 0.0, results)
        // Convert the distance from meters to kilometers


        return String.format("%.1f", results[0] / 1000) + " km away"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LibraryShowItemLayoutBinding.inflate(
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