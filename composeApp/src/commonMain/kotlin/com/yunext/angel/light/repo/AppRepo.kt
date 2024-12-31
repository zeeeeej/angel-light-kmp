package com.yunext.angel.light.repo

import com.yunext.angel.light.domain.UserDomain
import com.yunext.angel.light.domain.poly.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow


class AppRepo(
    private val userDatasource: UserDatasource,

) : UserDomain {
    override val user: Flow<User> = userDatasource.user

    override suspend fun saveUser(user: User) {
        userDatasource.saveUser(user)
    }
}