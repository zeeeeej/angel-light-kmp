package com.yunext.angel.light.repo.http

import kotlinx.serialization.Serializable

@Serializable
class HttpResp<T>(
    val code: Int,
    val msg: String,
    val data: T,
    val success: Boolean
) {
//    companion object : AppDomain {
//        private val channel: Channel<Unit> = Channel()
//        val tokenExpiredChannel: Channel<Unit>
//            get() = channel
//
//        override fun onTokenExpired() {
//            MyApp.holder.clear()
//            channel.trySend(Unit)
//        }
//    }
}

val HttpResp<*>.tokenExpired: Boolean
    get() = this.code == 103

