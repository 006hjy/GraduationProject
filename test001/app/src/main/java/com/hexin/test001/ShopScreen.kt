package com.hexin.test001

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URL
import java.security.MessageDigest


@Composable
fun ShopScreen(current: Context) {
    var number by remember { mutableStateOf(0) }
    var showcard by remember { mutableStateOf(false) }
    var showinfo by remember { mutableStateOf("") }
    var tip by remember { mutableStateOf("init") }
    var tipVisible by remember { mutableStateOf(false) }
    BackHandler {
        // 这里定义返回键的逻辑
        if (showcard) showcard = false
        else {
            if (current is Activity) {
                // context是Activity类型的对象
                current.finish()
            }
        }
    }
    LaunchedEffect(tip) {
        tipVisible = true
        delay(2000)
        tipVisible = false
    }
    //使用 LaunchedEffect 在界面创建时做网络请求获取数字
    LaunchedEffect(true) {
        try {
            val result = withContext(Dispatchers.IO) {
                URL("http://$address/booknumber").openStream()
            }.bufferedReader().use { it.readText() }
            number = result.toInt()
        } catch (e: Exception) {
            // 处理其他类型的异常
            Log.e("MyTag", e.message, e)
        }
    }

    Box(Modifier.fillMaxSize()) {
        // TODO: 商城页面内容
        LazyColumn(Modifier.fillMaxWidth()) {
            for (i in 1..number) {
                var result = ""
                var bookId by mutableStateOf("")
                var bookName by mutableStateOf("")
                var bookPrice by mutableStateOf("")
                var sellerId by mutableStateOf("")
                var bookPic by mutableStateOf("")
                var bookBlurb by mutableStateOf("")
                var contact by mutableStateOf("")
                var flag by mutableStateOf(true)
                Thread {
                    val url = "http://$address/show?order=$i"
                    try {
                        result = URL(url).openStream().bufferedReader().use { it.readText() }
                        if ((result == "NotFound") or (result == "")) {
                            flag = false
                        } else {
                            val lines = result.split("\n")
                            bookId = lines[0]
                            bookName = lines[1]
                            bookPrice = lines[2]
                            sellerId = lines[3]
                            bookPic = lines[4]
                            bookBlurb = lines[5]
                            contact = lines[6]
                        }
                    } catch (e: Exception) {
                        // 处理其他类型的异常
                        Log.e("MyTag", e.message, e)
                    }
                }.start()
                item {
                    if (flag) {
                        Card(
                            modifier = Modifier
                                .widthIn((LocalConfiguration.current.screenWidthDp).dp)
                                .height(200.dp)
                                .padding(5.dp), elevation = 5.dp

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!showcard) showinfo = result
                                        showcard = !showcard
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = bookPic,
                                    contentDescription = null,
                                    Modifier
                                        .widthIn(170.dp)
                                        .height(170.dp)
                                        .padding(start = 10.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        fontWeight = FontWeight.Bold, fontSize = 25.sp,
                                        text = bookName,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Text(
                                            text = bookPrice,
                                            Modifier.align(alignment = Alignment.BottomStart),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp,
                                            style = TextStyle(
                                                color = Color.Red, // 设置文本颜色为红色
                                            )
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        if (showcard) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                        .background(
                            when {
                                isSystemInDarkTheme() -> Color.Black // 深色模式下的颜色
                                else -> Color.White // 浅色模式下的颜色
                            }
                        )
                ) {
                    var bookId by remember { mutableStateOf("") }
                    var bookName by remember { mutableStateOf("") }
                    var bookPrice by remember { mutableStateOf("") }
                    var sellerId by remember { mutableStateOf("") }
                    var bookPic by remember { mutableStateOf("") }
                    var bookBlurb by remember { mutableStateOf("") }
                    var contact by remember { mutableStateOf("") }
                    var flag by remember { mutableStateOf(true) }
                    if ((showinfo == "NotFound") or (showinfo == "")) {
                        flag = false
                    } else {
                        val lines = showinfo.split("\n")
                        bookId = lines[0]
                        bookName = lines[1]
                        bookPrice = lines[2]
                        sellerId = lines[3]
                        bookPic = lines[4]
                        bookBlurb = lines[5]
                        contact = lines[6]
                    }
                    Card(
                        modifier = Modifier.fillMaxSize(), elevation = 50.dp
                    ) {
                        LazyColumn(
                            Modifier.clickable { showcard = false }
                        ) {
                            item() {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val passwordmd5 = MessageDigest
                                                .getInstance("MD5")
                                                .digest(password.toByteArray())
                                                .joinToString("") { "%02x".format(it) }
                                            /* 删除请求 */
                                            Thread {
                                                val url =
                                                    "http://$address/delete?username=$userName&password=$passwordmd5" +
                                                            "&book_id=$bookId"
                                                tip = try {
                                                    val result =
                                                        URL(url)
                                                            .openStream()
                                                            .bufferedReader()
                                                            .use { it.readText() }
                                                    result
                                                } catch (e: Exception) {
                                                    // 处理其他类型的异常
                                                    Log.e("MyTag", e.message, e)
                                                    "网络错误"
                                                }
                                            }.start()
                                            showcard = false
                                        }
                                ) {
                                    Text(
                                        text = "×",
                                        Modifier.align(alignment = Alignment.CenterHorizontally),
                                        fontSize = 20.sp,
                                        style = TextStyle(
                                            color = Color.Red,
                                        )
                                    )
                                }

                                AsyncImage(
                                    model = bookPic,
                                    contentDescription = null,
                                    Modifier
                                        .fillMaxWidth()
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp, 0.dp)
                                ) {
                                    Text(
                                        text = bookName,
                                        Modifier.align(alignment = Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold, fontSize = 35.sp,
                                    )
                                    Text(
                                        text = bookPrice,
                                        Modifier.align(alignment = Alignment.CenterHorizontally),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        style = TextStyle(
                                            color = Color.Red,
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(
                                        text = "介绍：\n     $bookBlurb",
                                        fontSize = 25.sp,
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(
                                        text = "卖家联系方式：\n$contact",
                                        fontSize = 25.sp,
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }

        if (tipVisible and (tip != "init")) {
            Snackbar(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = tip)
            }
        }
    }
}

