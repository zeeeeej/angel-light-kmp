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
import com.yunext.angel.light.di.koinViewModelP1
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.ui.compoent.LoadingComponent
import com.yunext.angel.light.ui.viewmodel.MainState
import com.yunext.angel.light.ui.viewmodel.MainViewModel
import com.yunext.kotlin.kmp.common.domain.Effect

@Composable
fun HomeScreenWithVm(
    modifier: Modifier,
    user: User,
    onProductModelSelected:OnProductModelSelected
) {
    val vm: MainViewModel = koinViewModelP1(user)
    val state by vm.state.collectAsStateWithLifecycle(MainState(user = user))
    val logoutEffect by remember {
        derivedStateOf {
            state.logoutEffect
        }
    }

    LaunchedEffect(logoutEffect) {
        when (logoutEffect) {
            Effect.Completed -> {}
            is Effect.Fail -> {}
            Effect.Idle -> {}
            is Effect.Progress<*, *> -> {}
            is Effect.Success -> {}
        }
    }

    LaunchedEffect(Unit) {
        vm.listProduct()
    }
    Box() {
        val loading: Boolean by remember {
            derivedStateOf {
                state.logoutEffect is Effect.Progress<*, *>
            }
        }
        HomeScreen(modifier = modifier,
            products = state.products,
            productModels = state.productModels,
            onProductModelSelected = onProductModelSelected,
            onExit = {
                vm.logout()
            },
            onProductSelected = {})
        if (loading) {
            Dialog(
                onDismissRequest = {

                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoadingComponent(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}