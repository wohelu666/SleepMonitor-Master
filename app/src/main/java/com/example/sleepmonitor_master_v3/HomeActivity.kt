package com.example.sleepmonitor_master_v3

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_home)
        val list = mutableListOf<String>("json1","json2","json3","json4")
        for (i in 0..100){
            Log.e(TAG, "onCreate: "+list.random())

        }

    }


}