package club.mobile.d21.smarthomesystem

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModel
import club.mobile.d21.smarthomesystem.viewmodel.MainViewModelFactory

class SmartHomeSystem : Application() {

    // Khai báo ViewModelStore để quản lý vòng đời của ViewModel
    private val appViewModelStore: ViewModelStore by lazy { ViewModelStore() }

    lateinit var mainViewModel: MainViewModel
        private set

    override fun onCreate() {
        super.onCreate()

        // Khởi tạo MainViewModel dùng chung cho toàn ứng dụng
        val factory = MainViewModelFactory(this)
        mainViewModel = ViewModelProvider(appViewModelStore, factory)[MainViewModel::class.java]
    }
}

