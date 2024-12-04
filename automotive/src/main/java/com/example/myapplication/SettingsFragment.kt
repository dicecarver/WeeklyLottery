package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlin.random.Random

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.BufferedWriter

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        val seekBars = Array(45) { rootView.findViewById<SeekBar>(resources.getIdentifier("seekBar${it + 1}", "id", requireContext().packageName)) }
        val valueTexts = Array(45) { rootView.findViewById<TextView>(resources.getIdentifier("valueText${it + 1}", "id", requireContext().packageName)) }

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

        val saveButton = rootView.findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val probabilities = seekBars.map { it.progress }
            writeCsv(probabilities)

            Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

        //val cancelButton = rootView.findViewById<Button>(R.id.cancelButton)
        //cancelButton.setOnClickListener {
        //    activity?.onBackPressed() // 부모 Activity의 onBackPressed() 호출
        //}

        val resetButton = rootView.findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            for (i in 0 until 45) {
                seekBars[i].progress = 50
                valueTexts[i].text = "50"
            }
        }

        val suggestButton = rootView.findViewById<Button>(R.id.suggestButton)
        suggestButton.setOnClickListener {
            for (i in 0 until 45) {
                val randomValue = Random.nextInt(0, 101) // 0부터 100 사이의 랜덤 값 생성
                seekBars[i].progress = randomValue
                valueTexts[i].text = randomValue.toString()
            }
        }

        return rootView
    }
    // CSV 파일에서 데이터를 읽는 함수
    private fun readCsv(): List<Int> {
        val file = File(requireContext().filesDir, "weights.csv")
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
        val file = File(requireContext().filesDir, "weights.csv")
        val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file)))
        data.forEach {
            writer.write("$it\n")
        }
        writer.close()
    }
}