package com.indianstudygroup.book_seat_gym

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indianstudygroup.R

class SeatBookGymActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_book_gym)
        window.statusBarColor = Color.WHITE

    }
}