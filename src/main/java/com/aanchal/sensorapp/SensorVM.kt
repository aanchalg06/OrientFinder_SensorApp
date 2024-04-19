package com.aanchal.sensorapp

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SensorVM(private val dao: OrientationDao) : ViewModel()  {
    private val _orientation = MutableStateFlow(OrientationData(0f, 0f, 0f,getCurrentTime()))
    val orientation: StateFlow<OrientationData> = _orientation
    var databaseSet: MutableSet<OrientationData> = mutableSetOf()
    fun updateOrientation(roll: Float, pitch: Float, yaw: Float) {
        _orientation.value = OrientationData(roll, pitch, yaw, getCurrentTime() )

    }
    fun updateIntoDatabase(roll: Float, pitch: Float, yaw: Float) {
        viewModelScope.launch {
            val repo = DataRepository(dao)
            repo.updateDatabase(_orientation)
        }
    }
    fun copyDataintoDataset(isLoading: MutableState<Boolean>, function: () -> Unit) {
        viewModelScope.launch {
            val repo = DataRepository(dao)
            repo.getData(databaseSet,isLoading)
        }
    }
    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}