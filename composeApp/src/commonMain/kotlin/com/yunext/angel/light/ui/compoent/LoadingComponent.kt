package com.yunext.angel.light.ui.compoent

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.loading_b
import com.yunext.angel.light.resources.loading_s
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview()
@Composable
fun LoadingComponentPreview(modifier: Modifier = Modifier) {
    LoadingComponent()
}

@Preview()
@Composable
fun CancelableLoadingComponentPreview(modifier: Modifier = Modifier) {
    CancelableLoadingComponent(){}
}

@Composable
fun CancelableLoadingComponent(modifier: Modifier = Modifier,onCancel:()->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,modifier=modifier) {
        LoadingComponent()
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                    onCancel()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(Icons.Default.Close, null, colorFilter = ColorFilter.tint(Color.Red))
            Text(
                "取消", style = TextStyle.Default.copy(color = Color.Black),

                )
        }
    }
}

@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(
                RoundedCornerShape(
                    16.dp
                )
            )
            .shadow(
                4.dp, RoundedCornerShape(
                    16.dp
                )
            )
            .background(Color.White), contentAlignment = Alignment.Center
    ) {

        if (content == null) {
            // 创建一个可观察的动画状态
            val infiniteTransition = rememberInfiniteTransition(label = "1")
            val animationValue by infiniteTransition.animateFloat(
                // 使用 infiniteRepeatable 创建无限重复的动画
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000), // 1000毫秒的补间动画
                    repeatMode = RepeatMode.Restart // 动画重复模式
                ), label = "2"
            )
            Image(
                painter = painterResource(Res.drawable.loading_b),
                null,
                modifier = Modifier.rotate(animationValue)
            )
        } else {
            content()
        }
    }
}

@Composable
fun LoadingIcon(modifier: Modifier = Modifier) {
    // 创建一个可观察的动画状态
    val infiniteTransition = rememberInfiniteTransition(label = "1")
    val animationValue by infiniteTransition.animateFloat(
        // 使用 infiniteRepeatable 创建无限重复的动画
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // 1000毫秒的补间动画
            repeatMode = RepeatMode.Restart // 动画重复模式
        ), label = "LoadingIcon"
    )
    Image(
        painter = painterResource(Res.drawable.loading_s),
        null,
        modifier = Modifier.rotate(animationValue)
    )
}