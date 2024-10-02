package club.mobile.d21.smarthomesystem.feature.detail

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import club.mobile.d21.smarthomesystem.core.util.FirebaseManager
import club.mobile.d21.smarthomesystem.core.util.Util
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DataHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private var _dataHistory = MutableLiveData<List<Pair<Long, SensorData>>>()
    val dataHistory: LiveData<List<Pair<Long, SensorData>>> get() = _dataHistory

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 10000L

    private val updateDataRunnable = object : Runnable {
        override fun run() {
            fetchDataHistory()
            handler.postDelayed(this, updateInterval)
        }
    }

    init {
        if (FirebaseManager.isUserIdSet()) {
            fetchDataHistory()
        }
    }

    private fun fetchDataHistory() {
        val database = FirebaseManager.getDatabaseReference()
        val dataHistoryQuery = database.child("dataHistory").orderByKey().limitToLast(500)
        dataHistoryQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dataHistory: List<Pair<Long, SensorData>> = snapshot.children.mapNotNull { dataSnapshot ->
                        val key = dataSnapshot.key?.toLongOrNull()?.times(1000)
                        val value = dataSnapshot.getValue(SensorData::class.java)
                        if (key != null && value != null) key to value else null
                    }.sortedByDescending { it.first }

                    _dataHistory.postValue(dataHistory)
                } else {
                    Log.e("DataHistoryViewModel", "No data exists at the dataHistory reference.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DataHistoryViewModel", "Error fetching dataHistory: ${error.message}")
            }
        })
    }
    fun updateSortedDataHistory(sortOption: Int) {
        val dataHistoryList = _dataHistory.value?.map { (key, value) ->
            Pair(key, value)
        } ?: emptyList()

        val sortedList = when (sortOption) {
            0 -> dataHistoryList.sortedByDescending { it.first }
            1 -> dataHistoryList.sortedBy { it.first }
            2 -> dataHistoryList.sortedByDescending { it.second.temperature }
            3 -> dataHistoryList.sortedBy { it.second.temperature }
            4 -> dataHistoryList.sortedByDescending { it.second.humidity }
            5 -> dataHistoryList.sortedBy { it.second.humidity }
            6 -> dataHistoryList.sortedByDescending { it.second.light }
            7 -> dataHistoryList.sortedBy { it.second.light }
            else -> dataHistoryList
        }
        _dataHistory.postValue(sortedList)
    }
    fun performSearch(option: String, value: String) {
        val filteredList = _dataHistory.value?.filter { (key, sensorData) ->
            when (option) {
                "All" -> matchesAllCriteria(Util.formatTimestamp(key), sensorData, value)
                "Time" -> matchesTime(Util.formatTimestamp(key), value)
                "Temperature" -> matchesTemperature(sensorData.temperature, value)
                "Humidity" -> matchesHumidity(sensorData.humidity, value)
                "Light" -> matchesLight(sensorData.light, value)
                else -> true
            }
        }?.sortedByDescending { it.first }
        _dataHistory.postValue(filteredList ?: emptyList())
    }

    private fun matchesAllCriteria(time: String, sensorData: SensorData, value: String): Boolean {
        return matchesTime(time, value) ||
                matchesTemperature(sensorData.temperature, value) ||
                matchesHumidity(sensorData.humidity, value) ||
                matchesLight(sensorData.light, value)
    }

    private fun matchesTime(time: String, value: String): Boolean {
        return time.contains(value)
    }

    private fun matchesTemperature(temperature: Float, value: String): Boolean {
        return temperature.toString().contains(value)
    }

    private fun matchesHumidity(humidity: Float, value: String): Boolean {
        return humidity.toString().contains(value)
    }

    private fun matchesLight(light: Float, value: String): Boolean {
        return light.toString().contains(value)
    }

    fun startUpdatingData() {
        handler.post(updateDataRunnable)
    }
    fun stopUpdatingData(){
        handler.removeCallbacks(updateDataRunnable)
    }
    override fun onCleared() {
        super.onCleared()
        stopUpdatingData()
    }
}
