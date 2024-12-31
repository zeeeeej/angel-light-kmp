package com.yunext.angel.light.di

import com.yunext.angel.light.repo.sp.UserStore
import com.yunext.angel.light.ui.viewmodel.AppViewModel
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val platformViewModelModule: Module = module {
//    singleOf(::MainViewModel)

    factory { params ->
        AppViewModel(get())
    }
}

actual val platformUserStoreModule: Module = module {
    single {
        createDataStore()
    }
}

private fun createDataStore(): UserStore {
    return UserStore {
        "${fileDirectory()}/user.json"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun fileDirectory(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory).path!!
}