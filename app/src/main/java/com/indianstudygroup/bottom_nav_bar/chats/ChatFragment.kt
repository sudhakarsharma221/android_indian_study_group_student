package com.indianstudygroup.bottom_nav_bar.chats

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indianstudygroup.R
import com.indianstudygroup.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

//         inflater.inflate(R.layout.fragment_chat, container, false)
        return binding.root
    }
}