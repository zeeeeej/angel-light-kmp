package com.yunext.angel.light.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repo.AppRepo
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.effectCompleted
import com.yunext.kotlin.kmp.common.domain.effectIdle
import com.yunext.kotlin.kmp.common.domain.effectProgress
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AppState(
    val user: User = User.Empty,
    val effect: Effect<Unit, Boolean> = effectIdle()
)

class AppViewModel(private val appRepo: AppRepo) : ViewModel() {
    private val effect: MutableStateFlow<Effect<Unit, Boolean>> =
        MutableStateFlow(effectIdle())
    private val user: Flow<User> = appRepo.user

    val state: Flow<AppState> = combine(user, effect) { u, e ->
        AppState(u, e)
    }

    init {
        Napier.d {
            "AppViewModel init... "
        }
        loadUser()
    }

    private fun loadUser(){
        viewModelScope.launch {
            effect.value = effectProgress()
            delay(1600)
            effect.value = effectCompleted()
        }
    }
}