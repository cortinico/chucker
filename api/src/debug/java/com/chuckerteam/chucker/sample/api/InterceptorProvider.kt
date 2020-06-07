package com.chuckerteam.chucker.sample.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager

object InterceptorProvider {

    fun provideInterceptor(context: Context): okhttp3.Interceptor? {
        val collector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor(
            context = context,
            collector = collector,
            maxContentLength = 250000L,
            headersToRedact = emptySet()
        )
    }
}
