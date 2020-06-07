package com.chuckerteam.chucker.sample.api

import android.content.Context
import android.widget.Toast

object HelloWorld {
    fun printSomething(context: Context) {
        Toast.makeText(context, "API Library here 👋", Toast.LENGTH_SHORT).show()
    }
}
