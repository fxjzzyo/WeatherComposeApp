package com.zilin.weathercompose

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kotlinweather2.data.WeatherNow
import com.example.kotlinweather2.data.daily.WeatherDaily
import com.zilin.weathercompose.data.DrawerMenuItemBean
import com.zilin.weathercompose.data.Location
import com.zilin.weathercompose.data.WeatherDailyState
import com.zilin.weathercompose.data.WeatherInfo
import com.zilin.weathercompose.data.WeatherState
import com.zilin.weathercompose.data.fake.FakeData
import com.zilin.weathercompose.data.remote.WeatherRetrofitClient
import com.zilin.weathercompose.data.repository.WeatherRepository
import com.zilin.weathercompose.ui.OfficialSpinner
import com.zilin.weathercompose.ui.about.About
import com.zilin.weathercompose.ui.bg.BgSetting
import com.zilin.weathercompose.ui.city.CityScreen
import com.zilin.weathercompose.ui.theme.WeatherComposeTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val initCity: String = "北京"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val api = WeatherRetrofitClient.api
        val repository = WeatherRepository(api)
        val viewModel = WeatherViewModel(repository)
        setContent {
            WeatherComposeTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {

                    // 全局抽屉状态
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope() // ✅ 组合函数协程作用域

                    var isSearchMode by remember { mutableStateOf(false) }
                    var searchText by remember { mutableStateOf("") }

                    // 监听导航栈，拿到当前路由
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // 把菜单统一定义成列表，便于维护
                    val menuList = remember {
                        listOf(
                            DrawerMenuItemBean(title = "天气", route = "home"),
                            DrawerMenuItemBean(title = "收藏城市", route = "collect_city"),
                            DrawerMenuItemBean(title = "背景设置", route = "bg_setting"),
                            DrawerMenuItemBean(title = "关于", route = "about")
                        )
                    }

                    // 封装方便子页面获取抽屉状态
                    CompositionLocalProvider(LocalDrawerState provides drawerState) {
                        // ========== 全局模态侧滑抽屉 ==========
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            // 抽屉内容
                            drawerContent = {
                                ModalDrawerSheet(
                                    modifier = Modifier.requiredWidth(200.dp),
                                    drawerContainerColor = Color.White.copy(alpha = 0.92f)
                                ) {
                                    Column {
                                        menuList.forEach { item ->
                                            // 当前路由等于item.route，就选中高亮
                                            val isSelected = currentRoute == item.route

                                            NavigationDrawerItem(
                                                label = { Text(item.title) },
                                                selected = isSelected,
                                                onClick = {
                                                    navController.navigate(item.route) {
                                                        launchSingleTop = true // 避免重复创建页面实例
                                                    }
                                                    scope.launch { drawerState.close() }
                                                },
                                                modifier = Modifier.padding(
                                                    NavigationDrawerItemDefaults.ItemPadding
                                                ),
                                                // ========== 自定义选中高亮颜色（可选） ==========
                                                colors = NavigationDrawerItemDefaults.colors(
                                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer, //选中背景
                                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    unselectedContainerColor = Color.Transparent
                                                )
                                            )

                                        }
                                    }
                                }
                            }
                        ) {
                            Image(
                                painterResource(R.drawable.bg1),
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

                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        title = {
                                            if (isSearchMode) {
                                                // ========== 搜索模式：输入框 ==========
                                                val focusRequester = remember { FocusRequester() }
                                                LaunchedEffect(isSearchMode){
                                                    if(isSearchMode){
                                                        focusRequester.requestFocus()
                                                    }
                                                }

                                                TextField(
                                                    value = searchText,
                                                    onValueChange = { searchText = it },
                                                    placeholder = { Text("请输入城市名称", color = Color.White.copy(alpha = 0.8f)) },
                                                    singleLine = true,
                                                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                                                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                                                        focusedIndicatorColor = Color.Transparent,
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                    ),
                                                    textStyle = TextStyle.Default.copy(color=Color.White),
                                                    keyboardActions = KeyboardActions(
                                                        onSearch = {
                                                            val city = searchText.trim()
                                                            if (city.isNotEmpty()) {
                                                                isSearchMode = false
                                                                searchText = ""
                                                                focusRequester.freeFocus()
                                                            }
                                                        }
                                                    ),
                                                    trailingIcon = {
                                                        if (searchText.isNotEmpty()) {
                                                            IconButton(onClick = {
                                                                searchText = ""
                                                            }) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Clear,
                                                                    contentDescription = "清空",
                                                                    tint = Color.White
                                                                )
                                                            }
                                                        }
                                                    },
                                                )
                                            } else {
                                                Text(
                                                    "天气预报",
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        },
                                        navigationIcon = {
                                            if (isSearchMode) {
                                                // 搜索模式：返回箭头，退出搜索
                                                IconButton(onClick = {
                                                    isSearchMode = false
                                                    searchText = "" //清空输入

                                                }) {
                                                    Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "退出搜索",
                                                        tint = Color.White
                                                    )
                                                }
                                            } else {
                                                // 左上角汉堡按钮打开抽屉
                                                IconButton(onClick = {
                                                    scope.launch { drawerState.open() }
                                                }) {
                                                    Icon(
                                                        Icons.Default.Menu,
                                                        contentDescription = "打开菜单",
                                                        tint = Color.White
                                                    )
                                                }
                                            }

                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = Color.Transparent,
                                            scrolledContainerColor = Color.Transparent
                                        ),
                                        actions = {
                                            if (currentRoute == "home") {
                                                if (isSearchMode) {
                                                    // 搜索模式：确认搜索按钮
                                                    IconButton(onClick = {
                                                        val cityName = searchText.trim()
                                                        if (cityName.isNotBlank()){
                                                            isSearchMode = false
                                                            searchText = ""
                                                            viewModel.getWeather(cityName)
                                                        }
                                                    }) {
                                                        Icon(
                                                            Icons.Default.Check,
                                                            contentDescription = "确认搜索",
                                                            tint = Color.White
                                                        )
                                                    }
                                                } else {
                                                    // 搜索按钮
                                                    IconButton(onClick = {
                                                        // 跳转到搜索城市页面 / 打开搜索弹窗
                                                        isSearchMode = true
                                                    }) {
                                                        Icon(
                                                            Icons.Default.Search,
                                                            contentDescription = "搜索",
                                                            tint = Color.White
                                                        )
                                                    }

                                                    // 三点更多下拉菜单
                                                    var expanded by remember { mutableStateOf(false) }
                                                    IconButton(onClick = { expanded = true }) {
                                                        Icon(
                                                            Icons.Default.MoreVert,
                                                            contentDescription = "更多",
                                                            tint = Color.White
                                                        )
                                                    }
                                                    DropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        DropdownMenuItem(
                                                            text = { Text("刷新天气") },
                                                            onClick = {
                                                                expanded = false
                                                                // 调用viewModel刷新天气
                                                            }
                                                        )
                                                        DropdownMenuItem(
                                                            text = { Text("选择城市") },
                                                            onClick = {
                                                                expanded = false
                                                            }
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxSize(),
                                containerColor = Color.Transparent,
                            ) { innerPadding ->
                                Log.i("TAG", "onCreate: before launched effect.")

                                NavHost(
                                    navController = navController,
                                    startDestination = "home",
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable("home") {
                                        WeatherHomeScreen(viewModel = viewModel)
                                    }
                                    composable("collect_city") {
                                        CityScreen()
                                    }
                                    composable("bg_setting") {
                                        BgSetting()
                                    }
                                    composable("about") {
                                        About()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 自定义抽屉Item组件
@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(text) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

// CompositionLocal 让子页面拿到DrawerState
val LocalDrawerState = staticCompositionLocalOf<DrawerState> {
    error("DrawerState not provided")
}

@Composable
fun WeatherHomeScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val weatherState by viewModel.weatherState.collectAsState()
    val weatherDailyState by viewModel.weatherDailyState.collectAsState()
    var selectedCity by remember {
        mutableStateOf("北京")
    }
    TotalScreen(
        provideCity = {
            selectedCity
        },
        weatherState = {
            weatherState
        },
        featureWeatherState = {
            weatherDailyState
        },
        onCitySelect = { city ->
            selectedCity = city
            viewModel.getWeather(city)
            viewModel.getWeatherDaily(city)
        },
        modifier = modifier
    )
    LaunchedEffect(Unit) {
        viewModel.getWeather(selectedCity)
        viewModel.getWeatherDaily(selectedCity)
    }
}


@Composable
fun TotalScreen(
    provideCity: () -> String,
    weatherState: () -> WeatherState,
    featureWeatherState: () -> WeatherDailyState,
    onCitySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherScreen(weatherState, onCitySelect)
            FutureWeather(featureWeatherState)
        }

        OfficialSpinner(
            options = FakeData.cities,
            selectedValue = provideCity.invoke(),
            onSelect = { city ->
                onCitySelect.invoke(city)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

}

@Composable
fun WeatherScreen(
    provideState: () -> WeatherState, onCitySelect: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val weatherState = provideState.invoke()
    Log.i("TAG", "WeatherScreen2: $weatherState")
    when {
        weatherState.loading -> Loading()
        weatherState.error != null -> ErrorView(weatherState.error)
        weatherState.weatherInfo == null -> ErrorView("weather is null")
        else -> {
            WeatherToday(weatherState.weatherInfo, onCitySelect)
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.White
        )
    }

}

@Composable
fun ErrorView(errorMsg: String, modifier: Modifier = Modifier) {
    Text(text = errorMsg, color = Color.White)
}

@Composable
fun WeatherToday(
    weatherInfo: WeatherInfo,
    onCitySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.i("TAG", "WeatherToday: ")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 40.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_0_2x), contentDescription = "",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = weatherInfo.weatherNow.text, color = MaterialTheme.colorScheme.onPrimary,
            fontSize = TextUnit(20f, TextUnitType.Sp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = weatherInfo.location.path,
            fontSize = TextUnit(20f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${weatherInfo.weatherNow.temperature}℃",
            fontSize = TextUnit(20f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "未来天气:",
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}


@Preview
@Composable
private fun WeatherScreenPreview() {
    WeatherToday(
        weatherInfo = WeatherInfo(
            location = Location(
                country = "中国",
                id = "1",
                name = "西安",
                path = "中国，西安",
                timezone = "中国北京",
                timezone_offset = "1"
            ),
            weatherNow = WeatherNow(
                temperature = "23-26",
                text = "晴天"
            )
        ), onCitySelect = {

        })
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

@Preview(
    device = "id:pixel_6", showSystemUi = false, showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL, backgroundColor = 0xFF3F51B5
)
@Composable
private fun TotalPreview() {
    TotalScreen(
        provideCity = {
            "北京"
        },
        {
            WeatherState(
                weatherInfo = FakeData.weatherInfo
            )
        }, {
            WeatherDailyState(
                weatherDaily = FakeData.weatherDailyResponse.weatherDailies
            )
        }, {

        })
}