package com.example.myapplication

data class LottoResult(
    val round: String,
    val rounddate: String,
    val winningNumbers: List<Int>,
    val bonusNumber: Int,
    val winnerCount: Int,
    val prizeAmount: Long
)