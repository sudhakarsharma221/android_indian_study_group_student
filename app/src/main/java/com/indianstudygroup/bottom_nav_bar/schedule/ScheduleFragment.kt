package com.indianstudygroup.bottom_nav_bar.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.indianstudygroup.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding

    private lateinit var adapter: ScheduleViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        initListener()

        return binding.root
    }

    private fun initListener() {
        adapter = ScheduleViewPagerAdapter(childFragmentManager, lifecycle)
//
//        tabLayout.addTab(tabLayout.newTab().setText("Personal"))
//        tabLayout.addTab(tabLayout.newTab().setText("Business"))
//        tabLayout.addTab(tabLayout.newTab().setText("Merchant"))
        binding.viewPager2.adapter = adapter

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