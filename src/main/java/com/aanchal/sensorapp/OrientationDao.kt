package com.aanchal.sensorapp


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrientationDao {
    @Insert
    fun insert(orientationData: OrientationData)

    @Query("Select * from OrientationData")
    fun getAllOrientationData(): List<OrientationData>
}