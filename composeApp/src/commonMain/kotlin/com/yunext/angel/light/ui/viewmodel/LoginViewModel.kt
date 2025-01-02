package com.yunext.angel.light.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.isEmpty
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repository.AppRepo
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.effectCompleted
import com.yunext.kotlin.kmp.common.domain.effectFail
import com.yunext.kotlin.kmp.common.domain.effectIdle
import com.yunext.kotlin.kmp.common.domain.effectProgress
import com.yunext.kotlin.kmp.common.domain.effectSuccess

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginState(
    val user: User = User.Empty,
    val loginEffect: Effect<Unit, Boolean> = effectIdle(),
)

class LoginViewModel(private val appRepo: AppRepo) : ViewModel() {
    private val effect: MutableStateFlow<Effect<Unit, Boolean>> =
        MutableStateFlow(effectIdle())
    private val user: Flow<User> = appRepo.user

    val state: Flow<LoginState> = combine(user, effect) { u, e ->
        LoginState(u, e)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                effect.value = effectProgress()
                val result = withContext(Dispatchers.IO) {
                    appRepo.login(user = username, pwd = password)
                }
                when (result) {
                    is HDResult.Fail -> {
                        effect.value = effectFail(RuntimeException("登录失败"))
                    }

                    is HDResult.Success -> {
                        val user = result.data
                        if (user.isEmpty) {
                            effect.value = effectFail(RuntimeException("登录失败"))
                        } else {
                            appRepo.saveUser(user)
                            effect.value = effectSuccess(true)
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                effect.value = effectFail(e)
            } finally {
                effect.value = effectCompleted()
            }
        }
    }
}