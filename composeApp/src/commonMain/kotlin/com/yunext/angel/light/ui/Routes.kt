package com.yunext.angel.light.ui

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.yunext.angel.light.domain.poly.ScanResult
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.ui.vo.Packet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//<editor-fold desc="Screen&Route">
enum class AppScreen {
    Login, Home, Scan, ScanResult, Production
    ;
}

val AppScreen.navArgument:List<NamedNavArgument>
    get() = when(this){
        AppScreen.Login -> emptyList()
        AppScreen.Home -> emptyList()
        AppScreen.Scan -> listOf(
            navArgument(NavigationKey.Packet.key) {
                type = NavType.StringType
            }
        )
        AppScreen.ScanResult -> emptyList()
        AppScreen.Production -> listOf(
            navArgument(NavigationKey.Packet.key) {
                type = NavType.StringType
            }, navArgument(NavigationKey.ScanResult.key) {
                type = NavType.StringType
            }
        )
    }

sealed interface RouteOwner {
    val screen: AppScreen
    val destination: String

    data object Login : RouteOwner {
        override val screen: AppScreen = AppScreen.Login
        override val destination: String
            get() = this.screen.route
    }

    data class Home(private val user: User) : RouteOwner {
        override val screen: AppScreen = AppScreen.Home

        override val destination: String
            get() = "${screen.routeRoot}/${this.user.encode()}"


    }

    data class Scan(val packet: Packet) : RouteOwner {
        override val screen: AppScreen = AppScreen.Scan

        override val destination: String
            get() = "${screen.routeRoot}/${this.packet.encode()}"
    }

    data class ScanResult(val scanResultVo: com.yunext.angel.light.domain.poly.ScanResult, val packet: Packet) : RouteOwner {
        override val screen: AppScreen = AppScreen.ScanResult

        override val destination: String
            get() = "${screen.routeRoot}/${this.packet.encode()}/${this.scanResultVo.encode()}"
    }

    class Production(private val scanResultVo: com.yunext.angel.light.domain.poly.ScanResult, private val packet: Packet) :
        RouteOwner {
        override val screen: AppScreen = AppScreen.Production

        override val destination: String
            get() = "${screen.routeRoot}/${this.packet.encode()}/${this.scanResultVo.encode()}"
    }
}


val AppScreen.routeRoot: String
    get() = this.name

val AppScreen.route: String
    get() = when (this) {
        AppScreen.Login -> routeRoot
        AppScreen.Home -> "${this.routeRoot}/${NavigationKey.User.wrapNavigationKey()}"
        AppScreen.Scan -> "${this.routeRoot}/${NavigationKey.Packet.wrapNavigationKey()}"
        AppScreen.ScanResult, AppScreen.Production -> "${this.routeRoot}/${NavigationKey.Packet.wrapNavigationKey()}/${NavigationKey.ScanResult.wrapNavigationKey()}"
    }
//</editor-fold>


//<editor-fold desc="NavigationKeys">
enum class NavigationKey {
    ScanResult,
    Packet,
    User
    ;
}

fun NavBackStackEntry.tryGetPacketSimple(): Packet? {
    val json = this.arguments?.getString(NavigationKey.Packet.key, "")
    return json?.decodeToPacket()
}

fun NavBackStackEntry.tryGetScanResultSimple(): ScanResult? {
    val json = this.arguments?.getString(NavigationKey.ScanResult.key, "")
    return json?.decodeToScanResult()
}

private val NavigationKey.key: String
    get() = when (this) {
        NavigationKey.ScanResult -> KEY_SCAN_RESULT
        NavigationKey.Packet -> KEY_PACKET
        NavigationKey.User -> KEY_USER
    }

private const val NAVIGATION_PREFIX = "@com.yunext.angel.light"
private const val KEY_SCAN_RESULT = "scan_result${NAVIGATION_PREFIX}"
private const val KEY_PACKET = "packet${NAVIGATION_PREFIX}"
private const val KEY_USER = "user${NAVIGATION_PREFIX}"
private fun wrapNavigationKeyInternal(key: String) = "{${key}}"
private fun String.wrapNavigationKey() = wrapNavigationKeyInternal(this)
private fun NavigationKey.wrapNavigationKey() = this.key.wrapNavigationKey()


//</editor-fold>

//<editor-fold desc="encode/decode">
fun User.encode(): String = encodeSimple(this)
fun String.encodeToUser(): User? = decodeSimple(this)

fun Packet.encode(): String = encodeSimple(this)
fun String.decodeToPacket(): Packet? = decodeSimple(this)

fun ScanResult.encode(): String = encodeSimple(this)
fun String.decodeToScanResult(): ScanResult? = decodeSimple(this)

private inline fun <reified T> decodeSimple(json: String): T? {
    return try {
        Json.decodeFromString<T>(json)
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

private inline fun <reified T : Any> encodeSimple(value: T): String {
    return Json.encodeToString(value)
}
//</editor-fold>


