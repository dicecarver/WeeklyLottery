package com.example.weeklylottery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.ImageButton
import android.widget.LinearLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val naviButton = findViewById<ImageButton>(R.id.naviButton)
        val historyButton = findViewById<ImageButton>(R.id.historyButton)
        val gameButton = findViewById<ImageButton>(R.id.gameButton)

        naviButton.setOnClickListener {
            val intent = Intent(this, NaviActivity::class.java)
            startActivity(intent)
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        gameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}