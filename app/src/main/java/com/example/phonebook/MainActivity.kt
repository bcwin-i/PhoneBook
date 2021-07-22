package com.example.phonebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Creating a load timer
        val timer = object : CountDownTimer(2000, 20){
            override fun onFinish() {
                txt_counting.text = "100 %"
                val intent = Intent(this@MainActivity, Homepage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            var count = 0
            override fun onTick(millisUntilFinished: Long) {
                count++
                var actual = 1 * count
                txt_counting.text = actual.toString() + " %"
            }

        }
        timer.start()
    }
}
