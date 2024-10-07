package club.mobile.d21.smarthomesystem.feature.home

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import club.mobile.d21.smarthomesystem.core.util.FirebaseManager
import club.mobile.d21.smarthomesystem.core.util.Util.getCurrentDate
import club.mobile.d21.smarthomesystem.data.model.device.DeviceStatus
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentDevice = MutableLiveData<DeviceStatus>()
    private val _dataHistory = MutableLiveData<Map<Long, SensorData>>()
    private val _warningCount = MutableLiveData<Int>()

    private val _combinedLiveData = MediatorLiveData<Triple<Map<Long, SensorData>?, DeviceStatus?,Int?>>()
    val combinedLiveData: LiveData<Triple<Map<Long, SensorData>?, DeviceStatus?,Int?>> get() = _combinedLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L

    private val database = FirebaseManager.getDatabaseReference()

    private val updateDataRunnable = object : Runnable {
        override fun run() {
            fetchDataHistory(1)
            handler.postDelayed(this, updateInterval)
        }
    }
    init {
        if (FirebaseManager.isUserIdSet()) {
            fetchCurrentDeviceData()
            startUpdatingData()
            updateWarningCount(getCurrentDate())
        }
        _warningCount.postValue(0)
        _combinedLiveData.addSource(_dataHistory) { data ->
            _combinedLiveData.value = Triple(data, _currentDevice.value, _warningCount.value)
        }
        _combinedLiveData.addSource(_currentDevice) { device ->
            _combinedLiveData.value = Triple(_dataHistory.value, device, _warningCount.value)
        }
        _combinedLiveData.addSource(_warningCount){ count ->
            _combinedLiveData.value = Triple(_dataHistory.value, _currentDevice.value, count)
        }
    }
    private fun fetchCurrentDeviceData() {
        database.child("currentDevice").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentDevice = snapshot.getValue(DeviceStatus::class.java) ?: DeviceStatus()
                _currentDevice.postValue(currentDevice)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Error fetching currentDevice: ${error.message}")
            }
        })
    }

    private fun fetchDataHistory(limit: Int) {
        val dataHistoryQuery = database.child("dataHistory").orderByKey().limitToLast(limit)
        dataHistoryQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dataHistory = snapshot.children.mapNotNull { dataSnapshot ->
                        val key = dataSnapshot.key?.toLongOrNull()
                        val value = dataSnapshot.getValue(SensorData::class.java)
                        if (key != null && value != null){
                            key to value
                        } else null
                    }.toMap()
                    _dataHistory.postValue(dataHistory)
                } else {
                    Log.e("HomeViewModel", "No data exists at the dataHistory reference.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Error fetching dataHistory: ${error.message}")
            }
        })
    }

    fun logLedStatus(ledName: String, isOn: Boolean) {
        val timestamp = System.currentTimeMillis()
        val ledStatus = mapOf(
            "name" to ledName,
            "status" to isOn
        )
        database.child("currentDevice").child(ledName).setValue(isOn)
        database.child("deviceHistory").child(timestamp.toString()).setValue(ledStatus)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "LED status logged successfully.")
                fetchCurrentDeviceData()
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Failed to log LED status: ${e.message}")
            }
    }

    fun logWarningStatus(isOn: Boolean){
        val database = FirebaseManager.getDatabaseReference()
        database.child("currentDevice").child("warning").setValue(isOn)
    }

    fun addWarningCount(currentDate: String,startTime: Long, endTime: Long){
        val lightExceedanceData = mapOf(
            "endTime" to endTime,
            "duration" to (endTime-startTime)/1000
        )
        database.child("warningCount").child(currentDate)
            .child(startTime.toString()).push().setValue(lightExceedanceData)
        updateWarningCount(currentDate)
    }
    private fun updateWarningCount(currentDate: String) {
        val warningCountRef = database.child("warningCount").child(currentDate)
        warningCountRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val count = snapshot.childrenCount.toInt()
                _warningCount.postValue(count)
            }
        }.addOnFailureListener { error ->
            Log.e("HomeViewModel", "Error updating warning count: ${error.message}")
        }
    }

    private fun startUpdatingData() {
        handler.post(updateDataRunnable)
    }

    private fun stopUpdatingData() {
        handler.removeCallbacks(updateDataRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingData()
    }
}
