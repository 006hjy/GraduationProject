package com.hexin.test001

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.hexin.test001.ui.theme.Test001Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


var userName by mutableStateOf("")
var password by mutableStateOf("")
var isLogin by mutableStateOf(false)
var bookName by mutableStateOf("")
var imageLink by mutableStateOf("")
var description by mutableStateOf("")
var price by mutableStateOf("")
var contact by mutableStateOf("")
var address by mutableStateOf("")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test001Theme {
                MainScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Test001Theme {
        MainScreen()
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Shop,
        Screen.Release,
        Screen.Account
    )

    var selectedItem by remember { mutableStateOf(items[0]) }

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == item,
                onClick = {
                    selectedItem = item
                    navController.navigate(item.route)
                }
            )
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Shop : Screen("shop", "浏览", Icons.Default.Home)
    object Release : Screen("release", "发布", Icons.Default.Add)
    object Account : Screen("account", "账户", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val sharedPref = LocalContext.current.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    isLogin = sharedPref.getBoolean("isLogin", false)
    userName = sharedPref.getString("username", "").toString()
    password = sharedPref.getString("password", "").toString()
    address = sharedPref.getString("address", "").toString()
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Shop.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Shop.route) { ShopScreen(LocalContext.current) }
            composable(Screen.Release.route) { ReleaseScreen(LocalContext.current) }
            composable(Screen.Account.route) { AccountScreen(LocalContext.current) }
        }
    }
}
