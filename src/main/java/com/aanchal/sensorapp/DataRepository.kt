package com.aanchal.sensorapp

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class DataRepository(private val orientationDao: OrientationDao) {
    suspend fun updateDatabase(
        _orientation: MutableStateFlow<OrientationData>
    ) {
        withContext(Dispatchers.IO) {

            val rotation = OrientationData(
                roll = _orientation.value.roll,
                pitch = _orientation.value.pitch,
                yaw = _orientation.value.yaw,
                time = getCurrentTime(),
            )
            orientationDao.insert(rotation)
        }
    }
    suspend fun getData(
        databaseSet: MutableSet<OrientationData>,
        isLoading: MutableState<Boolean>
    ){
        withContext(Dispatchers.IO) {
            isLoading.value=true // Set loading flag to true
            val rotationsFromDB = orientationDao.getAllOrientationData()
            databaseSet.addAll(rotationsFromDB)
            isLoading.value= (false) // Clear loading flag after data fetching is done
        }
    }
    private fun getCurrentTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault())
        val formattedTime = dateFormat.format(currentTimeMillis)
        return formattedTime
    }
}