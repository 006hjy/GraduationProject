package com.hexin.test001

import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URL
import java.security.MessageDigest

@Composable
fun ReleaseScreen(current: Context) {
    val bookName by rememberUpdatedState(bookName)
    val imageLink by rememberUpdatedState(imageLink)
    val description by rememberUpdatedState(description)
    val price by rememberUpdatedState(price)
    val contact by rememberUpdatedState(contact)
    var tip by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize()) {

        LazyColumn {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        fontWeight = FontWeight.Bold, fontSize = 50.sp,
                        text = "发布新书",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        Text(text = "书名")
                        TextField(
                            value = bookName,
                            onValueChange = { com.hexin.test001.bookName = it },
                            Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Column {
                        Text(text = "图片链接")
                        TextField(
                            value = imageLink,
                            onValueChange = { com.hexin.test001.imageLink = it },
                            Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Column {
                        Text(text = "描述")
                        TextField(
                            value = description,
                            onValueChange = { com.hexin.test001.description = it },
                            Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Column {
                        Text(text = "价格")
                        TextField(
                            value = price,
                            onValueChange = {// 限制只能输入两位小数
                                val temp = it.filter { i -> i.isDigit() || i == '.' }
                                var count = 0
                                var dotCount = 0
                                var afterDot = 0
                                for (c in temp) {
                                    count++
                                    if (dotCount != 0) afterDot++
                                    if (c == '.') dotCount++
                                }
                                when {
                                    (count == 1) and (dotCount == 1) -> {}
                                    dotCount > 1 -> {}
                                    afterDot > 2 -> {}
                                    else -> {
                                        com.hexin.test001.price = temp
                                    }
                                }
                            },
                            Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Column {
                        Text(text = "联系方式")
                        TextField(
                            value = contact,
                            onValueChange = { com.hexin.test001.contact = it },
                            Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = tip,
                            style = TextStyle(
                                color = Color.Red, // 设置文本颜色为红色
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
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
                                if (!isLogin) {
                                    tip = "未登录，请先登陆"
                                } else if (bookName == "") {
                                    tip = "书名不能为空"
                                } else if (price == "") {
                                    tip = "价格不能为空"
                                } else {
                                    val passwordmd5 = MessageDigest
                                        .getInstance("MD5")
                                        .digest(password.toByteArray())
                                        .joinToString("") { "%02x".format(it) }
                                    /* 登录逻辑 */
                                    Thread {
                                        val url =
                                            "http://$address/release?username=$userName&password=$passwordmd5" +
                                                    "&book_name=$bookName" +
                                                    "&book_price=$price" +
                                                    "&book_pic=$imageLink" +
                                                    "&book_blurb=$description" +
                                                    "&contact=$contact"
                                        try {
                                            val result =
                                                URL(url)
                                                    .openStream()
                                                    .bufferedReader()
                                                    .use { it.readText() }
                                            tip = result
                                        } catch (e: Exception) {
                                            // 处理其他类型的异常
                                            Log.e("MyTag", e.message, e)
                                            tip = "网络错误"
                                        }
                                    }.start()
                                }
                            })
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            "提交", fontSize = 36.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}