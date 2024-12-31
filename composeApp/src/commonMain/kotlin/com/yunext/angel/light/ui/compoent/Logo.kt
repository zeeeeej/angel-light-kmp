package com.yunext.angel.light.ui.compoent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.logo
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun Logo(modifier: Modifier = Modifier, version: String = "v1.0.0 kmp" ) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(Res.drawable.logo), null, modifier = Modifier)
        Spacer(Modifier.height(21.dp))
        Text(
            "轻智能产测",
            style = TextStyle.Default.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "${version}",
            style = TextStyle.Default.copy(fontSize = 11.sp, fontWeight = FontWeight.Normal)
        )

    }

}