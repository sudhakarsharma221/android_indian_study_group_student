package com.indianstudygroup.wishlist.ui.adapter

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.library.LibraryDetailsActivity
import com.indianstudygroup.databinding.LibraryShowItemLayoutBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem

class WishListAdapter(
    val context: Context,
    val currentLatitude: Double,
    val currentLongitude: Double,
    private val list: ArrayList<LibraryResponseItem>,
    private val listSize: (Int) -> Unit,
    private val onNotFavouriteClickListener: (LibraryResponseItem) -> Unit,
    private val onFavouriteClickListener: (LibraryResponseItem) -> Unit,
    private val libraryDetailsLauncher: (Intent) -> Unit
) : RecyclerView.Adapter<WishListAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: LibraryShowItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(library: LibraryResponseItem, context: Context, position: Int) {
            val isFavourite = AppConstant.wishList.contains(library.id)

            binding.favImage.setImageResource(
                if (isFavourite) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            binding.favourite.setOnClickListener {
                Log.d("WISHLISTAPPCONSTANT3", AppConstant.wishList.toString())
                if (AppConstant.wishList.contains(library.id)) {
                    // Remove from wishlist
                    AppConstant.wishList.remove(library.id)
                    binding.favImage.setImageResource(R.drawable.baseline_favorite_border_24)
                    onNotFavouriteClickListener(library)
                    // Remove the item from the list and notify the adapter
                    val index = list.indexOf(library)
                    if (index >= 0) {
                        list.removeAt(index)
                        notifyItemRemoved(index)
                        notifyItemRangeChanged(index, list.size)
                        listSize(list.size)
                    }
                } else {
                    // Add to wishlist
                    AppConstant.wishList.add(library.id!!)
                    binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
                    onFavouriteClickListener(library)
                }
            }
            var rating = 1f
            if (library.rating?.count == 0) {
                binding.tvRating.text = "1.0"
            } else {
                if (library.rating?.count == null) {
                    binding.tvRating.text = "1.0"
                } else {
                    Log.d("RATING", rating.toInt().toString())


                    rating = (library.rating?.count?.toFloat()?.let {
                        library.rating?.totalRatings?.toFloat()?.div(
                            it
                        )
                    })?.toFloat()!!

                    Log.d("RATINGG", rating.toString())
                }

            }
            binding.tvRating.text = String.format("%.1f", rating)
            binding.tvDistance.text = calculateDistance(
                library.address?.latitude?.toDouble(),
                library.address?.longitude?.toDouble(),
                currentLatitude,
                currentLongitude
            )
            binding.tvName.text = library.name
            binding.tvPrice.text = HtmlCompat.fromHtml(
                "<b><font color='#5669FF'>₹${library.pricing?.daily}</font></b> /Day<br/>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            if (library.photo?.isNotEmpty() == true) {
                Glide.with(context).load(library.photo?.get(0)).placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage).into(binding.imageView)
            }


            binding.layoutView.setOnClickListener {
                val intent = Intent(context, LibraryDetailsActivity::class.java)
                intent.putExtra("LibraryId", library.id)
                libraryDetailsLauncher(intent)
            }
        }
    }

//    private fun updateWishlist(libraryId: String?, add: Boolean) {
//        libraryId?.let {
//            if (add) {
//                if (!wishList.contains(it)) {
//                    wishList.add(it)
//                } else {
//                    wishList.remove(it)
//                }
//            }
//        }
//    }

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