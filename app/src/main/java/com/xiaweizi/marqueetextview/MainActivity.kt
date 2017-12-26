package com.xiaweizi.marqueetextview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resume.setOnClickListener {
            marquee1.resumeScroll()
            marquee2.resumeScroll()
            marquee3.resumeScroll()
            marquee4.resumeScroll()
        }

        pause.setOnClickListener {
            marquee1.pauseScroll()
            marquee2.pauseScroll()
            marquee3.pauseScroll()
            marquee4.pauseScroll()
        }

        restart.setOnClickListener {
            marquee1.startScroll()
            marquee2.startScroll()
            marquee3.startScroll()
            marquee4.startScroll()
        }

        stop.setOnClickListener {
            marquee1.stopScroll()
            marquee2.stopScroll()
            marquee3.stopScroll()
            marquee4.stopScroll()
        }

    }
}
