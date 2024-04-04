package com.indianstudygroup.bottom_nav_bar.home.tab_layout.screens.posts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indianstudygroup.R
import com.indianstudygroup.databinding.FragmentPostsBinding

class PostsFragment : Fragment() {
    private lateinit var viewModel: PostsViewModel
    private lateinit var binding: FragmentPostsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PostsViewModel::class.java]
        // inflater.inflate(R.layout.fragment_posts, container, false)
        return binding.root
    }
}