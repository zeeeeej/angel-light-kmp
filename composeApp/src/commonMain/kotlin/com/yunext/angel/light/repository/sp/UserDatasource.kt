package com.yunext.angel.light.repository.sp

import com.yunext.angel.light.domain.poly.User
import kotlinx.coroutines.flow.Flow

interface UserDatasource {
    val user: Flow<User>

    suspend fun saveUser(user: User)
    suspend fun clear()
}

internal class UserDatasourceImpl(private val userStore: UserStore) : UserDatasource {

    override val user: Flow<User> = userStore.user

    override suspend fun saveUser(user: User) {
        userStore.saveUser(user)
    }

    override suspend fun clear() {
        userStore.clear()
    }


}