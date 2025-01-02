package com.yunext.angel.light.di

import android.content.Context
import com.yunext.angel.light.repository.sp.UserStore
import com.yunext.angel.light.ui.viewmodel.AppViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformViewModelModule: Module = module {

//    viewModel {
//        MainViewModel(get())
//    }
    factory { params ->
        AppViewModel(get())
    }
}
actual val platformUserStoreModule: Module = module {

    single {
        createDataStore(get())
    }
}

private fun createDataStore(application: Context): UserStore {
    return UserStore {
        application.filesDir.absolutePath+"/user.data"
    }
}