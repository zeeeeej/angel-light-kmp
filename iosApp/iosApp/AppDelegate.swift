//
//  AppDelegate.swift
//  iosApp
//
//  Created by 项鹏乐 on 2024/12/31.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

import UIKit
import ComposeApp

//@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // 初始化操作
        initializeOkioStorage()
        print("iOS init...")
        // 其他初始化代码...
        
        return true
    }

    // 初始化 OkioStorage 的函数
    private func initializeOkioStorage() {
//         由于 OkioStorage 是 Kotlin 代码，你需要通过 Kotlin/Native 互操作性来调用它
//         假设 OkioStorage 有一个静态初始化方法或者单例
//        OkioStorageKt.okioStorageInitialization()
//        let kotlinInit = KoinInit()
//        kotlinInit.doInit(appDeclaration: <#T##(Koin_coreKoinApplication) -> Void#> )
    }
}
