package com.aanchal.sensorapp


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class OrientationData(
    val roll: Float,
    val pitch: Float,
    val yaw: Float,
    val time: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)