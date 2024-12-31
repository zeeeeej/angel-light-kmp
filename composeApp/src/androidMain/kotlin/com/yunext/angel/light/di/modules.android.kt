package com.yunext.angel.light.di

import android.content.Context
import com.yunext.angel.light.repo.sp.UserStore
import com.yunext.angel.light.ui.viewmodel.MainViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformViewModelModule: Module = module {

//    viewModel {
//        MainViewModel(get())
//    }
    factory { params ->
        MainViewModel(get())
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