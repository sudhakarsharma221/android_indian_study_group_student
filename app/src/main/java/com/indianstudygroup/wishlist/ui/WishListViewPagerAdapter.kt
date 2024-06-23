package com.indianstudygroup.wishlist.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.indianstudygroup.bottom_nav_bar.schedule.gym.ui.GymScheduleFragment
import com.indianstudygroup.bottom_nav_bar.schedule.library.ui.LibraryScheduleFragment
import com.indianstudygroup.wishlist.ui.screens.GymWishListFragment
import com.indianstudygroup.wishlist.ui.screens.LibraryWishlistFragment

class WishListViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            LibraryWishlistFragment()
        } else {
            GymWishListFragment()
        }
    }
}