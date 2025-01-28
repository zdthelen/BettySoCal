package com.zachthelen.cbb_predict

data class Comment(
    var id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)