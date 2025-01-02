package com.yunext.angel.light.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.isEmpty
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repo.AppRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

data class RootState(
    val user: User = User.Empty,
    val checkPermission: List<String> = emptyList()
)

class HomeRootViewModel(private val appRepo: AppRepo, val user: User) : ViewModel() {
    init {
        require(!user.isEmpty) {
            "user.isEmpty 怎么可能进入主页呢？"
        }
    }
    private val checkPermission: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val state: Flow<RootState> = combine(checkPermission){
        RootState(user = user,checkPermission = it[0])
    }


    private fun loadPermissionNoGant() = emptyList<String>()
        //listOfNotNull(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val has = ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) == PackageManager.PERMISSION_GRANTED
//            if (has) null else Manifest.permission.BLUETOOTH_CONNECT
//        } else {
//            null
//        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val has = ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.BLUETOOTH_SCAN
//            ) == PackageManager.PERMISSION_GRANTED
//            if (has) null else Manifest.permission.BLUETOOTH_SCAN
//        } else {
//            null
//        }, run {
//            val has = ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//            if (has) null else Manifest.permission.ACCESS_FINE_LOCATION
//        }, run {
//            val has = ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//            if (has) null else Manifest.permission.CAMERA
//        }
//    )

    fun refreshNoGantPermission() {
        val permissions = loadPermissionNoGant()
        checkPermission.value = permissions

    }

    init {
        this.refreshNoGantPermission()
    }
}
