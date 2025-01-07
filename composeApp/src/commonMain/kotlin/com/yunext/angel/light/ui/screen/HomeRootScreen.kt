package com.yunext.angel.light.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yunext.angel.light.di.koinViewModelP1
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repository.ble.currentTime
import com.yunext.angel.light.ui.AppScreen
import com.yunext.angel.light.ui.RouteOwner
import com.yunext.angel.light.ui.navArgument
import com.yunext.angel.light.ui.route
import com.yunext.angel.light.ui.tryGetPacketSimple
import com.yunext.angel.light.ui.tryGetScanResultSimple
import com.yunext.angel.light.ui.viewmodel.HomeRootViewModel
import com.yunext.angel.light.ui.viewmodel.RootState
import com.yunext.angel.light.ui.vo.Packet
import io.github.aakira.napier.Napier

import kotlinx.coroutines.launch

/**
 * ---------------------
 * APP入口-Navigation版本
 * ---------------------
 */
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    user: User,
    requestPermission: suspend () -> Boolean,
//    chePermission: suspend () -> List<String>
) {
    val navController = rememberNavController()
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()
    val vm: HomeRootViewModel = koinViewModelP1(user)
    val state by vm.state.collectAsStateWithLifecycle(RootState(user = user))
    val coroutineScope = rememberCoroutineScope()



    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RouteOwner.Home(user).destination
    ) {
        /* 主页 */
        composable(route = AppScreen.Home.route) {
            HomeScreenWithVm(
                Modifier
                    .fillMaxSize()
                    .safeContentPadding(),
                user = user,
                onProductModelSelected = { productModel, product, productType ->
                    coroutineScope.launch {
                        val checked = requestPermission()
                        if (checked) {
                            val scan = RouteOwner.Scan(
                                packet = Packet(
                                    product = product,
                                    productModel = productModel,
                                    type = productType
                                )
                            )
                            navController.navigate(
                                scan.destination
                            ) {
                                launchSingleTop = true
//                                anim {
//                                    enter = 0
//                                    exit = 0
//                                }
                            }
                        }
                    }
                },
            )
        }

        /* 扫码 */
        composable(
            route = AppScreen.Scan.route,
            arguments = AppScreen.Scan.navArgument
        ) { navBackStackEntry ->
            val packet = navBackStackEntry.tryGetPacketSimple()
            check(packet != null) {
                "packet is null"
            }
            ScanScreen(
                Modifier
                    .fillMaxSize(),
                packet = packet,
                onScanResult = { newPacket, newScanResultVo ->
//                    ToastUtil.toast("扫码成功")
                    val production = RouteOwner.Production(
                        newScanResultVo,
                        packet = newPacket
                    )

//                    XLog.d(
//                        "ScanScreen",
//                        """
//                        |扫码检查结果
//                        |packet           :   $newPacket
//                        |newScanResultVo  :   $newScanResultVo
//                    """.trimMargin()
//                    )
                    navController.navigate(
                        production.destination
                    ) {
                        launchSingleTop = true
                        popUpTo(AppScreen.Scan.route) {
                            inclusive = true
                        }
                    }
                }, onBack = {
                    // TODO 区别
//                    val result = navController.popBackStack(RouteOwner.Home(user).destination,false)
                    val result = navController.popBackStack(AppScreen.Scan.route,true)
                    Napier.w {
                        "ScanScreen onBack ${currentTime()} $result"
                    }

                }
            )
        }

        /* 扫码结果 */
        composable(route = AppScreen.ScanResult.route) {
//            ScanResultScreenPreview()
        }

        /* 产测 */
        composable(
            route = AppScreen.Production.route,
            arguments = AppScreen.Production.navArgument
        ) { navBackStackEntry ->
            val packet = navBackStackEntry.tryGetPacketSimple()
            val scanResult = navBackStackEntry.tryGetScanResultSimple()
            check(packet != null) {
                "packet is null"
            }
            check(scanResult != null) {
                "scanResult is null"
            }


            ProductionScreenWithVm(modifier = Modifier
                .fillMaxSize(), scanResult = scanResult, packet = packet, toScan = {
                val scan = RouteOwner.Scan(
                    packet = packet
                )
                navController.navigate(
                    scan.destination
                ) {
                    launchSingleTop = true
                    popUpTo(AppScreen.Production.route) {
                        inclusive = true
                    }
                }
            }, onBack = {
                val u = user
                val home = RouteOwner.Home(
                    user = u
                )
                navController.navigate(
                    home.destination
                ) {
                    launchSingleTop = true
                    popUpTo(AppScreen.Production.route) {
                        inclusive = true
                    }
                }
            })
        }
    }

    //<editor-fold desc="测试">
    var debug by remember { mutableStateOf("") }
    Text(debug)
    //</editor-fold>

    //<editor-fold desc="处理返回">
//    BackHandler(
//        currentBackStackEntryAsState?.destination?.route != AppScreen.Login.route &&
//                currentBackStackEntryAsState?.destination?.route != AppScreen.Home.route
//    ) {
//        navController.popBackStack()
//    }
    //</editor-fold>
}