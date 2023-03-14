package com.example.sleepmonitor_master_v3

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_body_movement.*
import kotlinx.android.synthetic.main.activity_situp.*
import kotlinx.android.synthetic.main.activity_situp.iv_back
import kotlinx.android.synthetic.main.activity_situp.tvProgress

class BodyMovementActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_body_movement)
        var stringExtra = intent.getStringExtra("body")
//        tvProgress.text = stringExtra
        iv_back.setOnClickListener {
            finish()
        }
    }
}