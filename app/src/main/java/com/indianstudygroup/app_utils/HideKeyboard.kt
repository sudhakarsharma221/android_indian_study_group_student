package com.indianstudygroup.app_utils

import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

object HideKeyboard {
    fun hideKeyboard(context: Context, windowToken: IBinder) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}