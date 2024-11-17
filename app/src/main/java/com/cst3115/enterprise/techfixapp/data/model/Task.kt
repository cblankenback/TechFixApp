

package com.cst3115.enterprise.techfixapp.data.model

data class Task(
    val id: Int,
    val clientName: String,
    val address: String,
    val jobDescription: String,
    var isCompleted: Boolean = false,
    val latitude: Double,
    val longitude: Double
)
