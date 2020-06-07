package com.chuckerteam.chucker.sample.api

import android.content.Context
import android.widget.Toast

object InterceptorProvider {
    fun provideInterceptor(context: Context) : okhttp3.Interceptor? {
        return null
    }
}
