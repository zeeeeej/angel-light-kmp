package com.yunext.angel.light.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.login_bg
import com.yunext.angel.light.ui.compoent.ImageBackground
import com.yunext.angel.light.ui.compoent.Logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(modifier: Modifier = Modifier, content: Boolean = true) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        ImageBackground()
        if (content) {
            Box(modifier) {
                Column(
                    Modifier.padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(300.dp))
                    Logo(modifier = Modifier, version = "")
                    Spacer(Modifier.height(4.dp))
//                                Text(
//                                    "v${MyApp.version}",
//                                    style = TextStyle.Default.copy(color = Color333)
//                                )
                }
            }
        }
    }
}