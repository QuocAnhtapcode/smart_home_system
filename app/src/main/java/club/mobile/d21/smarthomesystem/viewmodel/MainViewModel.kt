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
import club.mobile.d21.smarthomesystem.model.sensor_data.CurrentData
import club.mobile.d21.smarthomesystem.model.sensor_data.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var database: DatabaseReference
    private var userId: String = ""

    private val _firebaseData = MutableLiveData<FirebaseData>()
    val firebaseData: LiveData<FirebaseData> get() = _firebaseData

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 10000L // 10 giây

    private val updateDataRunnable = object : Runnable {
        override fun run() {
            fetchDataFromFirebase() // Lấy dữ liệu từ Firebase
            handler.postDelayed(this, updateInterval) // Lặp lại sau mỗi 10 giây
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
        if (!this::database.isInitialized) {
            Log.e("MainViewModel", "Database is not initialized. Please set user ID first.")
            return
        }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        // Lấy dữ liệu từ từng phần riêng biệt để kiểm tra và log nếu cần thiết
                        val deviceHistory = snapshot.child("deviceHistory").getValue(object : GenericTypeIndicator<Map<String, DeviceHistory>>() {})
                            ?: emptyMap()
                        Log.d("MainViewModel", "Fetched deviceHistory: $deviceHistory")

                        val dataHistory = snapshot.child("dataHistory").getValue(object : GenericTypeIndicator<Map<String, SensorData>>() {})
                            ?: emptyMap()
                        Log.d("MainViewModel", "Fetched dataHistory: $dataHistory")

                        val currentDevice = snapshot.child("currentDevice").getValue(DeviceStatus::class.java) ?: DeviceStatus()
                        Log.d("MainViewModel", "Fetched currentDevice: $currentDevice")

                        val currentData = snapshot.child("currentData").getValue(CurrentData::class.java) ?: CurrentData()
                        Log.d("MainViewModel", "Fetched currentData: $currentData")

                        // Tạo đối tượng FirebaseData từ các phần dữ liệu đã lấy được
                        val data = FirebaseData(
                            deviceHistory = deviceHistory,
                            dataHistory = dataHistory,
                            currentDevice = currentDevice,
                            currentData = currentData
                        )
                        _firebaseData.value = data
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Error parsing data from Firebase: ${e.message}")
                    }
                } else {
                    Log.e("MainViewModel", "No data exists at the Firebase reference.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error fetching data from Firebase: ${error.message}")
            }
        })
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
                Log.d("MainViewModel", "LED status logged successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "Failed to log LED status: ${e.message}")
            }
        database.child("currentDevice").child(ledName).setValue(isOn)
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