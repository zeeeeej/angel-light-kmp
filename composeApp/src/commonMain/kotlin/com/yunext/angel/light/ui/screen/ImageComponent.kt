package com.yunext.angel.light.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.main
import com.yunext.angel.light.ui.vo.ComposeIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageComponent(modifier: Modifier = Modifier, composeIcon: ComposeIcon) {
    when (composeIcon) {
        is ComposeIcon.Local -> Image(
            painter = painterResource(composeIcon.res),
            null,
            modifier = modifier
        )

        is ComposeIcon.Remote -> AsyncImage(
            model = composeIcon.path,
            contentDescription = "网络图片",
            modifier = modifier,
            placeholder = painterResource(composeIcon.error), // 加载中显示的占位图
            error = painterResource(composeIcon.error) // 加载失败显示的图片
        )
    }
}


@Preview
@Composable
fun ImageComponentPreview(modifier: Modifier = Modifier) {
    ImageComponent(
        Modifier.size(100.dp),
        ComposeIcon.Remote(
            "http://t13.baidu.com/it/u=2363275402,233694669&fm=224&app=112&f=JPEG?w=184&h=210",
            Res.drawable.main
        )
    )
}