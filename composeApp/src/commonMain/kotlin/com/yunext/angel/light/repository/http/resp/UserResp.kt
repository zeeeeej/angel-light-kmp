package com.yunext.angel.light.repository.http.resp

import com.yunext.angel.light.domain.poly.User
import kotlinx.serialization.Serializable

@Serializable
data class UserResp(
    val token: String?,
    val tokenUser: UserInnerResp?
)

@Serializable
data class UserInnerResp(
    val id: String?,
    val name: String?,
    val account: String?,
    val roleId: String?,
    val roleName: String?,
    val root: String?,
    val company: String?,
    val userType: String?,
)

fun UserResp.convert(): User {
    return User(
        username = this.tokenUser?.name ?: "",
        account = this.tokenUser?.account ?: "",
        root = this.tokenUser?.root ?: "",
        company = this.tokenUser?.company ?: "",
        userType = this.tokenUser?.userType ?: "",
        token = this.token ?: ""
    )
}
