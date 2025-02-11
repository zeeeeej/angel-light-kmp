package com.yunext.angel.light.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.angel.light.BuildConfigX
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.password_invisible
import com.yunext.angel.light.resources.password_visible
import com.yunext.angel.light.ui.common.CommitButton
import com.yunext.angel.light.ui.common.CommonEditText
import com.yunext.angel.light.ui.common.clickablePure
import com.yunext.angel.light.ui.compoent.ImageBackground
import com.yunext.angel.light.ui.compoent.Logo
import org.jetbrains.compose.resources.painterResource

import org.jetbrains.compose.ui.tooling.preview.Preview

typealias LoginScreenOnCommit = (String, String) -> Unit

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(Modifier.fillMaxSize(), onCommit = { _, _ -> })
}

@Composable
fun LoginScreen(modifier: Modifier, onCommit: LoginScreenOnCommit) {
    Box(Modifier.fillMaxSize()) {
        ImageBackground()
        Box(modifier) {
            Column {
                Spacer(Modifier.height(47.dp))
                Logo(version = BuildConfigX.VERSION)
                Spacer(Modifier.height(32.dp))
                Inputs(onCommit = onCommit)
            }
        }
    }
}

@Composable
private fun Inputs(
    modifier: Modifier = Modifier,
    onCommit: LoginScreenOnCommit,
    debug: Boolean = BuildConfigX.DEBUG_X
) {
    var username: String by remember { mutableStateOf(if (debug) "zhangcc" else "") }
    var password: String by remember { mutableStateOf(if (debug) "123456" else "") }
    var passwordVisible: Boolean by remember { mutableStateOf(false) }
    val enable: Boolean by remember {
        derivedStateOf {
            username.isNotBlank() && password.isNotBlank()
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 47.5.dp, vertical = 32.dp),
    ) {
        Text("账号", style = TextStyle.Default.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        CommonEditText(
            value = username,
            hint = "请输入账号",
            onValueChange = {
                username = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
//                .padding(horizontal = 16.dp, vertical = 13.5.dp)
        )
        Spacer(Modifier.height(20.dp))
        Text("密码", style = TextStyle.Default.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        CommonEditText(
            value = password,
            hint = "请输入密码",
            onValueChange = {
                password = it
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Image(
                    painterResource(
                        if (passwordVisible) Res.drawable.password_visible else
                            Res.drawable.password_invisible
                    ),
                    null,
                    modifier = Modifier.clickablePure {
                        passwordVisible = !passwordVisible
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
//                .padding(horizontal = 16.dp, vertical = 13.5.dp)
        )
        Spacer(Modifier.height(20.dp))
        CommitButton(text = "登录", enable = enable, onCommit = {
            onCommit.invoke(username, password)
        })
    }
}