package club.mobile.d21.smarthomesystem.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import club.mobile.d21.smarthomesystem.model.FirebaseData
import club.mobile.d21.smarthomesystem.model.device.DeviceHistory
import club.mobile.d21.smarthomesystem.model.device.DeviceStatus
import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData
import club.mobile.d21.smarthomesystem.util.Util.currentPage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var lastKey: String? = null
    private var firstKey: String? = null
    private lateinit var database: DatabaseReference
    private var userId: String = ""

    private val _firebaseData = MutableLiveData<FirebaseData>()
    val firebaseData: LiveData<FirebaseData> get() = _firebaseData
    private val _deviceHistory = MutableLiveData<Map<Long, DeviceHistory>>()
    val deviceHistory: LiveData<Map<Long, DeviceHistory>> get() = _deviceHistory
    private val _sortedDataHistory = MutableLiveData<List<Pair<Long, SensorData>>>()
    val sortedDataHistory: LiveData<List<Pair<Long, SensorData>>> get() = _sortedDataHistory

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 3000L

    private val updateDataRunnable = object : Runnable {
        override fun run() {
            fetchDataFromFirebase()
            handler.postDelayed(this, updateInterval)
        }
    }

    fun setUserId(uid: String) {
        userId = uid
        if (userId.isNotEmpty()) {
            Log.e("MainViewModel", userId)
            database = FirebaseDatabase.getInstance().reference.child(userId)
        } else {
            Log.e("MainViewModel", "User ID is empty, cannot initialize database reference.")
        }
    }

    fun fetchDataFromFirebase() {
        fetchDataHistory()
    }

    private fun fetchDataHistory() {
        val dataHistoryQuery = database.child("dataHistory").orderByKey().limitToLast(100)
        dataHistoryQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dataHistory = snapshot.children.mapNotNull { dataSnapshot ->
                        val key = dataSnapshot.key?.toLongOrNull()
                        val value = dataSnapshot.getValue(SensorData::class.java)
                        if (key != null && value != null) key to value else null
                    }.toMap()
                    val latestCurrentData = dataHistory.entries.maxByOrNull { it.key }?.value ?: SensorData()
                    updateFirebaseData(dataHistory = dataHistory, currentData = latestCurrentData )
                } else {
                    Log.e("MainViewModel", "No data exists at the dataHistory reference.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error fetching dataHistory: ${error.message}")
            }
        })
    }

    private fun fetchCurrentDeviceData() {
        database.child("currentDevice").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentDevice = snapshot.getValue(DeviceStatus::class.java) ?: DeviceStatus()
                updateFirebaseData(currentDevice = currentDevice)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error fetching currentDevice: ${error.message}")
            }
        })
    }

    private fun updateFirebaseData(
        dataHistory: Map<Long, SensorData>? = null,
        currentDevice: DeviceStatus? = null,
        currentData: SensorData? = null
    ) {
        val existingData = _firebaseData.value ?: FirebaseData()
        val newData = existingData.copy(
            dataHistory = dataHistory ?: existingData.dataHistory,
            currentDevice = currentDevice ?: existingData.currentDevice,
            currentData = currentData ?: existingData.currentData
        )
        _firebaseData.value = newData
    }

    fun logLedStatus(ledName: String, isOn: Boolean) {
        if (!this::database.isInitialized) {
            Log.e("MainViewModel", "Database is not initialized. Please set user ID first.")
            return
        }
        val timestamp = System.currentTimeMillis()
        val ledStatus = mapOf(
            "name" to ledName,
            "status" to isOn
        )
        database.child("deviceHistory").child(timestamp.toString()).setValue(ledStatus)
            .addOnSuccessListener {
                currentPage = 1
                fetchDeviceHistoryData(10, "next")
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "Failed to log LED status: ${e.message}")
            }
        database.child("currentDevice").child(ledName).setValue(isOn)
        fetchCurrentDeviceData()
    }

    fun fetchDeviceHistoryData(limit: Int, direction: String) {
        if (!this::database.isInitialized) {
            Log.e("MainViewModel", "Database is not initialized. Please set user ID first.")
            return
        }

        var query = database.child("deviceHistory").orderByKey()

        query = if (direction == "next" && firstKey != null) {
            query.endBefore(firstKey).limitToLast(limit)
        } else if (direction == "previous" && lastKey != null) {
            query.startAfter(lastKey).limitToFirst(limit)
        } else {
            query.limitToLast(limit)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val deviceHistoryList = mutableListOf<Pair<Long, DeviceHistory>>()

                    for (data in snapshot.children) {
                        val key = data.key
                        val value = data.getValue(DeviceHistory::class.java)
                        val timestamp = key?.toLongOrNull()
                        if (timestamp != null && value != null) {
                            deviceHistoryList.add(Pair(timestamp, value))
                        }
                    }
                    if (deviceHistoryList.isNotEmpty()) {
                        // Cập nhật khóa đầu và cuối nếu có dữ liệu
                        firstKey = snapshot.children.firstOrNull()?.key
                        lastKey = snapshot.children.lastOrNull()?.key

                        _deviceHistory.value = deviceHistoryList.toMap()
                        Log.d("MainViewModel", "Fetched ${deviceHistoryList.size} items for direction: $direction")
                    } else {
                        Log.d("MainViewModel", "No new data found for direction: $direction, not updating keys.")
                    }
                } else {
                    Log.e("MainViewModel", "No data found for the given query.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error fetching data: ${error.message}")
            }
        })
    }
    fun updateSortedDataHistory(sortOption: Int) {
        val dataHistoryList = _firebaseData.value!!.dataHistory.mapNotNull { (key, value) ->
            Pair(key * 1000, value)
        }
        val sortedList = when (sortOption) {
            0 -> dataHistoryList.sortedBy { it.first }
            1 -> dataHistoryList.sortedByDescending { it.first }
            2 -> dataHistoryList.sortedBy { it.second.temperature }
            3 -> dataHistoryList.sortedByDescending { it.second.temperature }
            4 -> dataHistoryList.sortedBy { it.second.humidity }
            5 -> dataHistoryList.sortedByDescending { it.second.humidity }
            6 -> dataHistoryList.sortedBy { it.second.light }
            7 -> dataHistoryList.sortedByDescending { it.second.light }
            else -> dataHistoryList.sortedByDescending { it.first }
        }

        _sortedDataHistory.value = sortedList
    }
    fun startUpdatingData() {
        handler.post(updateDataRunnable)
    }

    fun stopUpdatingData() {
        handler.removeCallbacks(updateDataRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingData()
    }
}
