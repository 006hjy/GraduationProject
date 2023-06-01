package com.hexin.test001

import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URL
import java.security.MessageDigest


@Composable
fun AccountScreen(current: Context) {
    var settings by remember { mutableStateOf(false) }
    val userName by rememberUpdatedState(userName)
    val password by rememberUpdatedState(password)
    val isLogin by rememberUpdatedState(isLogin)
    val address by rememberUpdatedState(address)
    var tip by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        fontWeight = FontWeight.Bold, fontSize = 50.sp,
                        text = if (isLogin) "你好,${userName}!" else "你好,游客!",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        settings = !settings
                                    }
                                )
                            }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (!isLogin) {
                        Column {
                            Text(text = "用户名")
                            TextField(
                                value = userName,
                                onValueChange = { com.hexin.test001.userName = it },
                                Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Column {
                            Text(text = "密码")
                            TextField(
                                value = password,
                                onValueChange = { com.hexin.test001.password = it },
                                Modifier.fillMaxWidth(),
                            )
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = tip,
                                style = TextStyle(
                                    color = Color.Red, // 设置文本颜色为红色
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp, when {
                                        isSystemInDarkTheme() -> Color.White // 深色模式下的颜色
                                        else -> Color.Black // 浅色模式下的颜色
                                    }
                                )
                                .clickable(onClick = {
                                    val passwordmd5 = MessageDigest
                                        .getInstance("MD5")
                                        .digest(password.toByteArray())
                                        .joinToString("") { "%02x".format(it) }
                                    /* 登录逻辑 */
                                    Thread {
                                        val url =
                                            "http://$address/login?username=${com.hexin.test001.userName}&password=$passwordmd5"
                                        try {
                                            val result =
                                                URL(url)
                                                    .openStream()
                                                    .bufferedReader()
                                                    .use { it.readText() }
                                            if ((result == "LoginSuccessful") or (result == "RegistrationSuccessful")) {
                                                com.hexin.test001.userName = userName
                                                com.hexin.test001.isLogin = true
                                                val sharedPref = current.getSharedPreferences(
                                                    "my_prefs",
                                                    Context.MODE_PRIVATE
                                                )
                                                sharedPref
                                                    .edit()
                                                    .putString("username", userName)
                                                    .apply()
                                                sharedPref
                                                    .edit()
                                                    .putString("password", password)
                                                    .apply()
                                                sharedPref
                                                    .edit()
                                                    .putBoolean("isLogin", true)
                                                    .apply()
                                            }
                                            if (result == "IncorrectPassword") {
                                                tip = "密码错误，请重新输入"
                                            }
                                            println(result)
                                        } catch (e: Exception) {
                                            // 处理其他类型的异常
                                            Log.e("MyTag", e.message, e)
                                            tip = "网络错误"
                                        }
                                    }.start()
                                })

                        ) {
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                "登录/注册", fontSize = 36.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    if (isLogin) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp, when {
                                        isSystemInDarkTheme() -> Color.White // 深色模式下的颜色
                                        else -> Color.Black // 浅色模式下的颜色
                                    }
                                )
                                .clickable(onClick = {
                                    /* 登出逻辑 */
                                    com.hexin.test001.userName = ""
                                    com.hexin.test001.password = ""
                                    com.hexin.test001.isLogin = false
                                    val sharedPref = current.getSharedPreferences(
                                        "my_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                    sharedPref
                                        .edit()
                                        .putString("username", "")
                                        .apply()
                                    sharedPref
                                        .edit()
                                        .putString("password", "")
                                        .apply()
                                    sharedPref
                                        .edit()
                                        .putBoolean("isLogin", false)
                                        .apply()

                                })
                        ) {
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                "退出登录", fontSize = 36.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                    if (settings) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Column {
                            Text(text = "服务器地址")
                            TextField(
                                value = address,
                                onValueChange = { com.hexin.test001.address = it },
                                Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp, when {
                                        isSystemInDarkTheme() -> Color.White // 深色模式下的颜色
                                        else -> Color.Black // 浅色模式下的颜色
                                    }
                                )
                                .clickable(onClick = {
                                    val sharedPref = current.getSharedPreferences(
                                        "my_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                    sharedPref
                                        .edit()
                                        .putString("address", address)
                                        .apply()
                                    settings = false
                                })
                        ) {
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                "保存", fontSize = 36.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    }
}
