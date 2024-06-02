package com.indianstudygroup.notification.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.indianstudygroup.databinding.NotificationItemLayoutBinding
import com.indianstudygroup.userDetailsApi.model.Notifications
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import com.indianstudygroup.R


class NotificationAdapter(
    val context: Context, private val list: ArrayList<Notifications>,

    private val onMarkClick: (String?) -> Unit

) : Adapter<NotificationAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: NotificationItemLayoutBinding) :
        ViewHolder(binding.root) {
        fun bindView(item: Notifications, context: Context, position: Int) {
            binding.tvHeading.text = item.title
            binding.tvSubHeading.text = item.message
            binding.tvDate.text = formatDate(item.date)

            binding.more.setOnClickListener { view ->
                showPopupMenu(view, item.id)
            }

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date: Date = inputFormat.parse(item.date)!!
            val currentTime = Date()
            val diffInMillis = currentTime.time - date.time

            // Convert the difference into a human-readable format
            binding.tvTime.text = getTimeAgo(diffInMillis)

            if (item.status == "unread") {
                binding.newNotification.visibility = View.VISIBLE
            } else {
                binding.newNotification.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NotificationItemLayoutBinding.inflate(
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

    private fun formatDate(input: String?): String {
        // Define the input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse the timestamp into a Date object
        val date: Date = inputFormat.parse(input)!!

        // Define the output date format
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        outputFormat.timeZone = TimeZone.getDefault()

        // Format the Date object into the desired format
        return outputFormat.format(date)
    }

    fun getTimeAgo(diffInMillis: Long): String {
        val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return when {
            diffInSeconds < 60 -> "$diffInSeconds sec ago"
            diffInMinutes < 60 -> "$diffInMinutes min ago"
            diffInHours < 24 -> "$diffInHours hr ago"
            else -> "$diffInDays days ago"
        }
    }

    private fun showPopupMenu(
        view: View, id: String?
    ) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.notification_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_markread -> {
                    onMarkClick(id)
                    true
                }

                else -> false
            }
        }
        popupMenu.setOnDismissListener {
            view.setBackgroundColor(Color.WHITE)
        }
        popupMenu.show()
    }
}