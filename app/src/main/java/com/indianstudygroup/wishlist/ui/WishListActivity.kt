package com.indianstudygroup.wishlist.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.indianstudygroup.databinding.ActivityWishListBinding

class WishListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWishListBinding
    private lateinit var adapter: WishListViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
    }

    private fun initListener() {
        binding.backButton.setOnClickListener {
            finish()
        }


        adapter = WishListViewPagerAdapter(supportFragmentManager, lifecycle)
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
        binding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })


    }
}
