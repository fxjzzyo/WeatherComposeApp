package com.zilin.weathercompose

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlinweather2.data.daily.WeatherDaily
import com.zilin.weathercompose.data.DrawerMenuItemBean
import com.zilin.weathercompose.data.WeatherDailyState
import com.zilin.weathercompose.data.repository.LoginRepo
import com.zilin.weathercompose.data.repository.LoginUiState
import com.zilin.weathercompose.ui.DrawerMenu
import com.zilin.weathercompose.ui.about.AboutNavHostPage
import com.zilin.weathercompose.ui.bg.BgNavHostPage
import com.zilin.weathercompose.ui.city.FavoriteCityNavHostPage
import com.zilin.weathercompose.ui.login.LoginScreen
import com.zilin.weathercompose.ui.login.RegisterScreen
import com.zilin.weathercompose.ui.theme.WeatherComposeTheme
import com.zilin.weathercompose.ui.weather.WeatherHomeScreen
import com.zilin.weathercompose.vm.AppViewModelFactory
import com.zilin.weathercompose.vm.BgSettingViewModel
import com.zilin.weathercompose.vm.LoginViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    // 数据库、repo 先初始化（放到 onCreate 或者 by lazy）
    private val db by lazy {
        (applicationContext as MyApp).db
    }
    private val loginRepo by lazy { LoginRepo(this) }

    private val appViewModelFactory by lazy {
        AppViewModelFactory(
            userDao = db.userDao(),
            userCityDao = db.userCityDao(),
            userBgConfigDao = db.userBgConfigDao(),
            loginRepo = loginRepo
        )
    }

    private val loginVm: LoginViewModel by viewModels {
        appViewModelFactory
    }

    private val bgVm: BgSettingViewModel by viewModels {
        appViewModelFactory
    }


    @SuppressLint("ContextCastToActivity")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 当前登录UID（null=未登录）
//            val currentUid by loginRepo.currentUidFlow.collectAsStateWithLifecycle(initialValue = null)
            val loginState by loginRepo.loginStateFlow.collectAsStateWithLifecycle(initialValue = LoginUiState.Loading)

            // 当前用户背景资源名
            val userBgResNameState = bgVm.selectedBgResName.collectAsStateWithLifecycle()

            // 解析背景图
            // 先算出最终drawable id
            val bgResId = remember(userBgResNameState.value) {
                val name = userBgResNameState.value
                val ctx = this@MainActivity
                if (name.isNullOrBlank()) {
                    R.drawable.bg1
                } else {
                    val resId = ctx.resources.getIdentifier(
                        name,
                        "drawable",
                        ctx.packageName
                    )
                    if (resId == 0) R.drawable.bg1 else resId
                }
            }
            val bgPainter = painterResource(id = bgResId)

            WeatherComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    // 全局抽屉状态
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope() // ✅ 组合函数协程作用域
                    // 监听导航栈，拿到当前路由
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val currentRoute = navBackStackEntry?.destination?.route

                    // 判断当前是否是天气页面home（兼容 home / home?cityName=xxx）
                    val isWeatherPage = currentDestination?.hierarchy?.any {
                        it.route == "home" || it.route?.startsWith("home?") == true
                    } == true

                    var backPressTime by remember { mutableLongStateOf(0L) }
                    BackHandler(enabled = isWeatherPage) {
                        val now = System.currentTimeMillis()
                        if (now - backPressTime < 2000) {
                            this.finish()
                        } else {
                            backPressTime = now
                            Toast.makeText(
                                this,
                                "再按一次退出应用",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    LaunchedEffect(loginState) {
                        Log.i("LoginRepo", "LaunchedEffect: $loginState")
                        when(loginState){
                            LoginUiState.Loading -> {
                                // 什么都不做，等待数据读取完成
                            }
                            is LoginUiState.Success -> {
                                val uid = (loginState as LoginUiState.Success).uid
                                if(uid == null){
                                    navController.navigate("login"){ popUpTo(0) }
                                }else{
                                    navController.navigate("home"){ popUpTo(0) }
                                }
                            }
                        }
                    }

                    /*LaunchedEffect(currentUid) {
                        Log.i("LoginRepo", "LaunchedEffect: $currentUid")
                        if (currentUid == null) {
                            // 未登录 → 去登录页，清空栈
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        } else {
                            // 已登录 → 去首页，清空栈
                            navController.navigate("home") {
                                popUpTo(0)
                            }
                        }
                    }*/

                    // 把菜单统一定义成列表，便于维护
                    val menuList = remember {
                        listOf(
                            DrawerMenuItemBean(title = "天气", route = "home"),
                            DrawerMenuItemBean(title = "收藏城市", route = "collect_city"),
                            DrawerMenuItemBean(title = "背景设置", route = "bg_setting"),
                            DrawerMenuItemBean(title = "关于", route = "about"),
                            DrawerMenuItemBean(title = "退出登录", route = "logout")
                        )
                    }

                    // 封装方便子页面获取抽屉状态
                    CompositionLocalProvider(LocalDrawerState provides drawerState) {
                        // ========== 全局模态侧滑抽屉 ==========
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            // 抽屉内容
                            drawerContent = {
                                DrawerMenu(
                                    menuList = menuList,
                                    currentRoute = currentRoute,
                                    onItemClick = { route ->
                                        scope.launch { drawerState.close() }
                                        if (route == "logout") {
                                            loginVm.logout {
                                                navController.navigate("login") {
                                                    popUpTo(0)
                                                }
                                            }
                                        } else {
                                            navController.navigate(route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }
                        ) {
                            Image(
                                bgPainter,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.Black.copy(0.15f),
                                                Color.Black.copy(0.4f)
                                            )
                                        )
                                    )
                            )

                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable(
                                    route = "home?cityName={cityName}",
                                    arguments = listOf(
                                        navArgument(name = "cityName") {
                                            nullable = true
                                            defaultValue = null
                                        }
                                    )
                                ) { backStackEntry ->
//                                    Log.i("LoginRepo", "home: $currentUid")
                                    val targetCity: String? =
                                        backStackEntry.arguments?.getString("cityName")
                                    WeatherHomeScreen(
                                        jumpCityName = targetCity
                                    )
                                }
                                composable("collect_city") {
                                    FavoriteCityNavHostPage { cityName ->
                                        navController.navigate("home?cityName=$cityName") {
                                            popUpTo("collect_city") { inclusive = true }
                                        }
                                    }
                                }
                                composable("bg_setting") {
                                    BgNavHostPage()
                                }
                                composable("about") {
                                    AboutNavHostPage()
                                }
                                composable("login") {
                                    LoginScreen(
                                        vm = loginVm,
                                        onGotoRegister = {
                                            navController.navigate("register")
                                        },
                                        onLoginSuccess = {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        })
                                }
                                composable("register") {
                                    RegisterScreen(vm = loginVm, onRegisterSuccess = {
                                        navController.popBackStack() // 注册成功回到登录页
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// CompositionLocal 让子页面拿到DrawerState
val LocalDrawerState = staticCompositionLocalOf<DrawerState> {
    error("DrawerState not provided")
}


@Composable
fun FutureWeather(
    provideState: () -> WeatherDailyState,
    modifier: Modifier = Modifier
) {
    val weatherDailyState = provideState.invoke()
    val weatherDailyList = weatherDailyState.weatherDaily.firstOrNull()?.weatherDaily ?: emptyList()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = weatherDailyList, key = {
            it.date
        }) { weatherDaily ->
            Box(modifier = Modifier.fillParentMaxWidth(1 / 3f)) {
                FutureWeatherItem(weatherDaily)
            }
        }
    }
}

@Composable
fun FutureWeatherItem(weatherDaily: WeatherDaily, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Image(painter = painterResource(R.drawable.ic_0_2x), contentDescription = "")
        Text(text = weatherDaily.date, color = MaterialTheme.colorScheme.onPrimary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "白天：", color = MaterialTheme.colorScheme.onPrimary)
            Image(
                painter = painterResource(R.drawable.ic_0_2x), contentDescription = "",
                Modifier.size(12.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(text = weatherDaily.text_day, color = MaterialTheme.colorScheme.onPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "黑夜：", color = MaterialTheme.colorScheme.onPrimary)
            Image(
                painter = painterResource(R.drawable.ic_0_2x), contentDescription = "",
                Modifier.size(12.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(text = weatherDaily.text_night, color = MaterialTheme.colorScheme.onPrimary)
        }
        Text(
            text = "${weatherDaily.low}~${weatherDaily.high}℃",
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "${weatherDaily.wind_direction}${weatherDaily.wind_scale}级",
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(text = weatherDaily.humidity, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Preview
@Composable
private fun FutureWeatherItemPreview() {
    FutureWeatherItem(WeatherDaily())
}