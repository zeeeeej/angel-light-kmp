package com.yunext.angel.light.ui.compoent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.login_bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun ImageBackground(modifier: Modifier = Modifier.fillMaxSize()) {
    Image(
        painterResource(Res.drawable.login_bg),
        null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}