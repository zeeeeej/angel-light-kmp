package com.yunext.angel.light.di

import com.yunext.angel.light.repo.AppRepo
import com.yunext.angel.light.repo.UserDatasource
import com.yunext.angel.light.repo.UserDatasourceImpl
import com.yunext.angel.light.ui.viewmodel.LoginViewModel
import com.yunext.angel.light.ui.viewmodel.MainViewModel
import kotlinx.serialization.json.Json
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single<UserDatasource> { UserDatasourceImpl(get()) } //bind UserDatasource::class
    single<AppRepo> { AppRepo(get()) }

    factory { params ->
        LoginViewModel(get())
    }

    factory { params ->
        MainViewModel(get(),params.get())
    }
}


expect val platformViewModelModule: Module
expect val platformUserStoreModule: Module

object KoinInit {
    fun init(appDeclaration: KoinAppDeclaration): Koin {
        return startKoin {
            appDeclaration()
            modules(
                platformUserStoreModule, appModule, platformViewModelModule
            )
        }.koin
    }
}

val json = Json { ignoreUnknownKeys = true }
