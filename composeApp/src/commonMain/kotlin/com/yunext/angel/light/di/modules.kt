package com.yunext.angel.light.di

import com.yunext.angel.light.repository.AppRepo
import com.yunext.angel.light.repository.http.HttpDatasource
import com.yunext.angel.light.repository.http.HttpDatasourceImpl
import com.yunext.angel.light.repository.sp.UserDatasource
import com.yunext.angel.light.repository.sp.UserDatasourceImpl
import com.yunext.angel.light.ui.viewmodel.HomeRootViewModel
import com.yunext.angel.light.ui.viewmodel.LoginViewModel
import com.yunext.angel.light.ui.viewmodel.MainViewModel
import com.yunext.angel.light.ui.viewmodel.ProductionViewModel
import com.yunext.angel.light.ui.viewmodel.ScanViewModel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single<UserDatasource> { UserDatasourceImpl(get()) } //bind UserDatasource::class
    single<HttpDatasource> { HttpDatasourceImpl() } //bind UserDatasource::class
    single<AppRepo> { AppRepo(get(),get()) }

    factory { params ->
        LoginViewModel(get())
    }

    factory { params ->
        HomeRootViewModel(get(),params.get())
    }

    factory { params ->
        MainViewModel(get(),params.get())
    }
    factory { params ->
        ScanViewModel(get(),params.get())
    }
    factory { params ->
        ProductionViewModel(get(),params.get(),params.get())
    }
}


expect val platformViewModelModule: Module
expect val platformUserStoreModule: Module

object KoinInit {
    fun init(appDeclaration: KoinAppDeclaration): Koin {
        Napier.base(DebugAntilog())
        return startKoin {
            appDeclaration()
            modules(
                platformUserStoreModule, appModule, platformViewModelModule
            )
        }.koin
    }
}

val myJson = Json { ignoreUnknownKeys = true }
