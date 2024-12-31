package com.yunext.angel.light.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunext.angel.light.di.koinViewModel
import com.yunext.angel.light.ui.compoent.LoadingComponent
import com.yunext.angel.light.ui.viewmodel.LoginState
import com.yunext.angel.light.ui.viewmodel.LoginViewModel
import com.yunext.kotlin.kmp.common.domain.Effect

@Composable
fun LoginScreenWithVm(modifier: Modifier) {
    val vm: LoginViewModel = koinViewModel()
    val state by vm.state.collectAsStateWithLifecycle(LoginState())
    val effect by remember {
        derivedStateOf {
            state.loginEffect
        }
    }
    LaunchedEffect(effect) {

        when (effect) {
            Effect.Completed -> {}
            is Effect.Fail -> {}
            Effect.Idle -> {}
            is Effect.Progress<*, *> -> {}
            is Effect.Success -> {
            }
        }
    }
    Box {
        LoginScreen(modifier = modifier, onCommit = { u, p ->
            vm.login(username = u, password = p)
        })

        val loading: Boolean by remember {
            derivedStateOf {
                state.loginEffect is Effect.Progress<*, *>
            }
        }
        if (loading) {
            Dialog(
                onDismissRequest = {

                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingComponent(modifier = Modifier.align(Alignment.Center))
                    }
                },
            )


        }
    }


}