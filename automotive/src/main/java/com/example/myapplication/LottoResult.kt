package com.example.myapplication

data class LottoResult(
    val round: String,
    val rounddate: String,
    val winningNumbers: List<Int>,
    val bonusNumber: Int,
    val winnerCount: Int,
    val prizeAmount: Long,
    val autoCount: Int,
    val manualCount: Int,
    val semiAutoCount: Int,
    val regionStats: Map<String, RegionDetail>
)

data class RegionDetail(
    val total: Int,
    val auto: Int,
    val manual: Int,
    val semiAuto: Int
)