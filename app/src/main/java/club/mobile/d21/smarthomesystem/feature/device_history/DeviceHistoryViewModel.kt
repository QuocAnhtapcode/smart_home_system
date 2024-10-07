package club.mobile.d21.smarthomesystem.feature.device_history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import club.mobile.d21.smarthomesystem.core.util.FirebaseManager
import club.mobile.d21.smarthomesystem.data.model.device.DeviceHistory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class DeviceHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _deviceHistory = MutableLiveData<List<Pair<Long, DeviceHistory>>>()
    val deviceHistory: LiveData<List<Pair<Long, DeviceHistory>>> get() = _deviceHistory

    private val database = FirebaseManager.getDatabaseReference()
    private var lastKey: String? = null
    private var firstKey: String? = null

    init {
        if (FirebaseManager.isUserIdSet()) {
            fetchDeviceHistoryByFilters("","All")
        }
    }
    fun fetchDeviceHistoryByFilters(selectedDate: String, deviceType: String) {
        val query = database.child("deviceHistory").orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val deviceHistoryList = mutableListOf<Pair<Long, DeviceHistory>>()
                    if(selectedDate==""){
                        for (data in snapshot.children) {
                            val key = data.key
                            val value = data.getValue(DeviceHistory::class.java)
                            val timestamp = key?.toLongOrNull()
                            if (timestamp != null && value != null) {
                                val matchesDevice = deviceType == "All" || value.name == deviceType
                                if (matchesDevice) {
                                    deviceHistoryList.add(Pair(timestamp, value))
                                }
                            }
                        }
                    }else{
                        val selectedCalendar = Calendar.getInstance()
                        val dateParts = selectedDate.split("-")
                        if (dateParts.size == 3) {
                            selectedCalendar.set(dateParts[0].toInt(), dateParts[1].toInt()-1, dateParts[2].toInt())
                        }
                        val startOfDay = selectedCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                        val endOfDay = selectedCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }.timeInMillis
                        for (data in snapshot.children) {
                            val key = data.key
                            val value = data.getValue(DeviceHistory::class.java)
                            val timestamp = key?.toLongOrNull()

                            if (timestamp != null && value != null) {
                                val matchesDate = selectedDate.isEmpty() || (timestamp in startOfDay..endOfDay)
                                val matchesDevice = deviceType == "All" || value.name == deviceType

                                if (matchesDate && matchesDevice) {
                                    deviceHistoryList.add(Pair(timestamp, value))
                                }
                            }
                        }
                    }
                    _deviceHistory.value = deviceHistoryList
                    Log.d("DeviceHistoryViewModel", "Fetched ${deviceHistoryList.size} items after filtering by date and device type")
                } else {
                    Log.e("DeviceHistoryViewModel", "No data found for the given query.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DeviceHistoryViewModel", "Error fetching data: ${error.message}")
            }
        })
    }

    fun fetchDeviceHistory(limit: Int, direction: String) {
        val database = FirebaseManager.getDatabaseReference()
        var query = database.child("deviceHistory").orderByKey()

        query = when {
            direction == "next" && firstKey != null -> query.endBefore(firstKey).limitToLast(limit)
            direction == "previous" && lastKey != null -> query.startAfter(lastKey).limitToFirst(limit)
            else -> query.limitToLast(limit)
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
                        firstKey = snapshot.children.firstOrNull()?.key
                        lastKey = snapshot.children.lastOrNull()?.key
                        _deviceHistory.value = deviceHistoryList
                        Log.d("DeviceHistoryViewModel", "Fetched ${deviceHistoryList.size} items for direction: $direction")
                    } else {
                        Log.d("DeviceHistoryViewModel", "No new data found for direction: $direction, not updating keys.")
                    }
                } else {
                    Log.e("DeviceHistoryViewModel", "No data found for the given query.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DeviceHistoryViewModel", "Error fetching data: ${error.message}")
            }
        })
    }
}
