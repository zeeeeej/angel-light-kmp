package com.yunext.angel.light

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.yunext.angel.light.di.koinViewModel
import com.yunext.angel.light.domain.isEmpty
import com.yunext.angel.light.ui.screen.LoginScreenWithVm
import com.yunext.angel.light.ui.screen.RootScreen
import com.yunext.angel.light.ui.screen.SplashScreen
import com.yunext.angel.light.ui.viewmodel.AppState
import com.yunext.angel.light.ui.viewmodel.AppViewModel
import com.yunext.kotlin.kmp.common.domain.Effect
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    LaunchedEffect(Unit){
        Napier.d {
            "Compose App"
        }
    }
    MaterialTheme {
        // for coil3
        // https://coil-kt.github.io/coil/getting_started/
        setSingletonImageLoaderFactory { ctx ->
            ImageLoader(ctx).newBuilder()
                .crossfade(true)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
        }

        val viewModel = koinViewModel<AppViewModel>()
        val state by viewModel.state.collectAsState(AppState())
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
                if (state.user.isEmpty) {
                    LoginScreenWithVm(Modifier.fillMaxSize())
                } else {
                    RootScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        user = state.user,
                        requestPermission = suspend {
                            true
                        }
                    )
                }
            }
        }
    }
}