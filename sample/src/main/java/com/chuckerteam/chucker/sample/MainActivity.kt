package com.chuckerteam.chucker.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chuckerteam.chucker.sample.api.HelloWorld
import kotlinx.android.synthetic.main.activity_main_sample.*

class MainActivity : AppCompatActivity() {

    private val client: HttpBinClient by lazy {
        HttpBinClient(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sample)

        do_http.setOnClickListener {
            client.doHttpActivity()
            HelloWorld.printSomething(this)
        }
    }
}
