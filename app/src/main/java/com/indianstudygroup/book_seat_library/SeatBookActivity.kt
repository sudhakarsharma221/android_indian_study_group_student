package com.indianstudygroup.book_seat_library

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.indianstudygroup.R

class SeatBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_book)
        window.statusBarColor = Color.WHITE

    }
}