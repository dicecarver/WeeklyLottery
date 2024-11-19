package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.BufferedWriter

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val seekBars = Array(45) { findViewById<SeekBar>(resources.getIdentifier("seekBar${it + 1}", "id", packageName)) }
        val valueTexts = Array(45) { findViewById<TextView>(resources.getIdentifier("valueText${it + 1}", "id", packageName)) }

        // CSV 파일에서 값 읽어오기 (없으면 기본값 50으로 설정)
        val savedValues = readCsv()

        // 읽어온 값으로 SeekBar와 TextView 설정
        for (i in 0 until 45) {
            seekBars[i].progress = savedValues[i]
            valueTexts[i].text = savedValues[i].toString()

            seekBars[i].setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    valueTexts[i].text = "$progress"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val probabilities = seekBars.map { it.progress }
            writeCsv(probabilities)

            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }

        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            for (i in 0 until 45) {
                seekBars[i].progress = 50
                valueTexts[i].text = "50"
            }
        }
    }
    // CSV 파일에서 데이터를 읽는 함수
    private fun readCsv(): List<Int> {
        val file = File(filesDir, "weights.csv")
        if (file.exists()) {
            val weights = mutableListOf<Int>()
            val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            reader.forEachLine {
                val value = it.toIntOrNull() ?: 50  // 잘못된 값은 50으로 처리
                weights.add(value)
            }
            reader.close()
            return weights
        }
        return List(45) { 50 }  // 파일이 없으면 기본값 50으로 초기화
    }

    // CSV 파일에 데이터를 쓰는 함수
    private fun writeCsv(data: List<Int>) {
        val file = File(filesDir, "weights.csv")
        val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file)))
        data.forEach {
            writer.write("$it\n")
        }
        writer.close()
    }
}