package com.yunext.angel.light.repo

import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repo.sp.UserStore
import kotlinx.coroutines.flow.Flow

interface UserDatasource {
    val user: Flow<User>

    suspend fun saveUser(user: User)
}

internal class UserDatasourceImpl(private val userStore: UserStore) : UserDatasource {

    override val user: Flow<User> = userStore.user

    override suspend fun saveUser(user: User) {
        userStore.saveUser(user)
    }


}