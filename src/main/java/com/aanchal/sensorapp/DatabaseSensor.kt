package com.aanchal.sensorapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OrientationData::class], version = 1)
abstract class DatabaseSensor : RoomDatabase() {
    abstract fun orientationDao(): OrientationDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseSensor? = null

        fun getDatabase(context: Context): DatabaseSensor {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseSensor::class.java,
                    "orientation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}