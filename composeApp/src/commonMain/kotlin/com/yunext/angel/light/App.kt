package com.yunext.angel.light

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yunext.angel.light.di.koinViewModel
import com.yunext.angel.light.di.koinViewModelAny
import com.yunext.angel.light.di.koinViewModelP1
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.logo
import com.yunext.angel.light.ui.screen.SplashScreen
import com.yunext.angel.light.ui.viewmodel.MainState
import com.yunext.angel.light.ui.viewmodel.MainViewModel
import com.yunext.kotlin.kmp.common.domain.Effect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {

//        val viewModel = koinViewModelP1<MainViewModel,String>("hello ios")
        val viewModel = koinViewModel<MainViewModel>()
        val state by viewModel.state.collectAsState(MainState())
        val loading by remember { derivedStateOf { state.effect !is Effect.Completed } }
        AnimatedContent(loading, transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                    scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(200)))
        }) { target ->
            if (target) {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    SplashScreen(modifier = Modifier)
                }
            } else {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Column(
                        Modifier.fillMaxWidth().safeContentPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painterResource(Res.drawable.logo), null)
                        Button(onClick = { }) {
                            Text("Click me! ${state.user}")
                        }
                    }
                }
            }
        }


    }
}