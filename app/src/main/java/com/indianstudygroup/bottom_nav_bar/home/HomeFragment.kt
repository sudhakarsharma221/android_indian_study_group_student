package com.indianstudygroup.bottom_nav_bar.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.home.tab_layout.adapter.TabLayoutAdapter
import com.indianstudygroup.bottom_nav_bar.home.tab_layout.screens.coaching.CoachingFragment
import com.indianstudygroup.bottom_nav_bar.home.tab_layout.screens.library.LibraryFragment
import com.indianstudygroup.bottom_nav_bar.home.tab_layout.screens.posts.PostsFragment
import com.indianstudygroup.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TabLayoutAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
//        inflater.inflate(R.layout.fragment_home, container, false)

        initListener()
        return binding.root
    }

    private fun initListener() {

        val tabTitles = mutableListOf<String>()
        val fragmentList = mutableListOf<Fragment>()
        tabTitles.add("Posts")
        fragmentList.add(PostsFragment())
        tabTitles.add("Library")
        fragmentList.add(LibraryFragment())
        tabTitles.add("Coaching")
        fragmentList.add(CoachingFragment())

        adapter =
            TabLayoutAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager2.adapter = adapter

        for (title in tabTitles) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title))
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

}