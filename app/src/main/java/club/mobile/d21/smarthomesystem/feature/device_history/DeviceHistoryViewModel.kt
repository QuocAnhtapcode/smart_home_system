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

class DeviceHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _deviceHistory = MutableLiveData<Map<Long, DeviceHistory>>()
    val deviceHistory: LiveData<Map<Long, DeviceHistory>> get() = _deviceHistory

    private var lastKey: String? = null
    private var firstKey: String? = null

    init {
        if (FirebaseManager.isUserIdSet()) {
            fetchDeviceHistory(10, "next")
        }
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
                        // Cập nhật khóa đầu và cuối nếu có dữ liệu
                        firstKey = snapshot.children.firstOrNull()?.key
                        lastKey = snapshot.children.lastOrNull()?.key
                        _deviceHistory.value = deviceHistoryList.toMap()
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
