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
import club.mobile.d21.smarthomesystem.data.model.device.DeviceStatus
import club.mobile.d21.smarthomesystem.data.model.sensor_data.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentDevice = MutableLiveData<DeviceStatus>()
    val currentDevice: LiveData<DeviceStatus> get() = _currentDevice
    private val _dataHistory = MutableLiveData<Map<Long, SensorData>>()
    val dataHistory: LiveData<Map<Long, SensorData>> get() = _dataHistory

    private val _combinedLiveData = MediatorLiveData<Pair<Map<Long, SensorData>?, DeviceStatus?>>()
    val combinedLiveData: LiveData<Pair<Map<Long, SensorData>?, DeviceStatus?>> get() = _combinedLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L

    private val updateDataRunnable = object : Runnable {
        override fun run() {
            fetchDataHistory(1)
            handler.postDelayed(this, updateInterval)
        }
    }
    init {
        if (FirebaseManager.isUserIdSet()) {
            fetchCurrentDeviceData()
            fetchDataHistory(25)
            startUpdatingData()
        }
        _combinedLiveData.addSource(_dataHistory) { data ->
            _combinedLiveData.value = Pair(data, _currentDevice.value)
        }
        _combinedLiveData.addSource(_currentDevice) { device ->
            _combinedLiveData.value = Pair(_dataHistory.value, device)
        }
    }

    private fun fetchCurrentDeviceData() {
        val database = FirebaseManager.getDatabaseReference()
        database.child("currentDevice").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentDevice = snapshot.getValue(DeviceStatus::class.java) ?: DeviceStatus()
                _currentDevice.value = currentDevice
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Error fetching currentDevice: ${error.message}")
            }
        })
    }

    private fun fetchDataHistory(limit: Int) {
        val database = FirebaseManager.getDatabaseReference()
        val dataHistoryQuery = database.child("dataHistory").orderByKey().limitToLast(limit)
        dataHistoryQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dataHistory = snapshot.children.mapNotNull { dataSnapshot ->
                        val key = dataSnapshot.key?.toLongOrNull()
                        val value = dataSnapshot.getValue(SensorData::class.java)
                        if (key != null && value != null) key to value else null
                    }.toMap()
                    _dataHistory.value = dataHistory
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
        val database = FirebaseManager.getDatabaseReference()
        val timestamp = System.currentTimeMillis()
        val ledStatus = mapOf(
            "name" to ledName,
            "status" to isOn
        )

        database.child("deviceHistory").child(timestamp.toString()).setValue(ledStatus)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "LED status logged successfully.")
                fetchCurrentDeviceData()
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Failed to log LED status: ${e.message}")
            }

        database.child("currentDevice").child(ledName).setValue(isOn)
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
