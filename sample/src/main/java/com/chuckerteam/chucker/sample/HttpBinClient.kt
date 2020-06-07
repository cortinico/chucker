package com.chuckerteam.chucker.sample

import android.content.Context
import com.chuckerteam.chucker.sample.HttpBinApi.Data
import com.chuckerteam.chucker.sample.api.InterceptorProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

private const val BASE_URL = "https://httpbin.org"

class HttpBinClient(
    context: Context
) {

    private val httpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        // Add a ChuckerInterceptor instance to your OkHttp client
        InterceptorProvider.provideInterceptor(context)?.let {
            builder.addInterceptor(it)
        }
        builder.build()
    }

    private val api: HttpBinApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(HttpBinApi::class.java)
    }

    @Suppress("MagicNumber")
    internal fun doHttpActivity() {
        val cb = object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) = Unit

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        }

        with(api) {
            get().enqueue(cb)
            post(Data("posted")).enqueue(cb)
            patch(Data("patched")).enqueue(cb)
            put(Data("put")).enqueue(cb)
            delete().enqueue(cb)
            status(201).enqueue(cb)
            status(401).enqueue(cb)
            status(500).enqueue(cb)
            delay(9).enqueue(cb)
            delay(15).enqueue(cb)
            redirectTo("https://http2.akamai.com").enqueue(cb)
            redirect(3).enqueue(cb)
            redirectRelative(2).enqueue(cb)
            redirectAbsolute(4).enqueue(cb)
            stream(500).enqueue(cb)
            streamBytes(2048).enqueue(cb)
            image("image/png").enqueue(cb)
            gzip().enqueue(cb)
            xml().enqueue(cb)
            utf8().enqueue(cb)
            deflate().enqueue(cb)
            cookieSet("v").enqueue(cb)
            basicAuth("me", "pass").enqueue(cb)
            drip(512, 10, 1, 200).enqueue(cb)
            deny().enqueue(cb)
            cache("Mon").enqueue(cb)
            cache(30).enqueue(cb)
            redirectTo("https://ascii.cl?parameter=%22Click+on+%27URL+Encode%27%21%22").enqueue(cb)
            redirectTo("https://ascii.cl?parameter=\"Click on 'URL Encode'!\"").enqueue(cb)
            postForm("Value 1", "Value with symbols &$%").enqueue(cb)
        }
        downloadSampleImage(colorHex = "fff")
        downloadSampleImage(colorHex = "000")
    }

    private fun downloadSampleImage(colorHex: String) {
        val request = Request.Builder()
            .url("https://dummyimage.com/200x200/$colorHex/$colorHex.png")
            .get()
            .build()
        httpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) = Unit

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body()?.source()?.use { it.readByteString() }
            }
        })
    }
}
