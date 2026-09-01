package com.zilin.weathercompose.ui.weather

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinweather2.data.WeatherNow
import com.zilin.weathercompose.FutureWeather
import com.zilin.weathercompose.LocalDrawerState
import com.zilin.weathercompose.R
import com.zilin.weathercompose.vm.WeatherViewModel
import com.zilin.weathercompose.data.Location
import com.zilin.weathercompose.data.WeatherDailyState
import com.zilin.weathercompose.data.WeatherInfo
import com.zilin.weathercompose.data.WeatherState
import com.zilin.weathercompose.data.fake.FakeData
import com.zilin.weathercompose.data.remote.WeatherRetrofitClient
import com.zilin.weathercompose.data.repository.WeatherRepository
import com.zilin.weathercompose.ui.OfficialSpinner
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    jumpCityName: String?
) {
    val api = WeatherRetrofitClient.api
    val repository = WeatherRepository(api)
    val viewModel = viewModel {
        WeatherViewModel(repository)
    }
    // 首页专属状态，放在页面内部，不再污染主Activity
    var isSearchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val drawerState = LocalDrawerState.current // 拿到抽屉状态，打开侧滑菜单

    val weatherState by viewModel.weatherState.collectAsState()
    val weatherDailyState by viewModel.weatherDailyState.collectAsState()
    var selectedCity by remember {
        mutableStateOf(jumpCityName ?: "北京")
    }

    LaunchedEffect(selectedCity) {
        if (selectedCity.isNotBlank()) {
            viewModel.getWeather(selectedCity)
            viewModel.getWeatherDaily(selectedCity)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchMode) {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(isSearchMode) {
                            if (isSearchMode) focusRequester.requestFocus()
                        }
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("请输入城市名称", color = Color.White.copy(alpha = 0.8f)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.2f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = TextStyle.Default.copy(color = Color.White),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    val city = searchText.trim()
                                    if (city.isNotEmpty()) {
                                        isSearchMode = false
                                        searchText = ""
                                        focusRequester.freeFocus()
                                        viewModel.getWeather(city)
                                    }
                                }
                            ),
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(onClick = { searchText = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "清空", tint = Color.White)
                                    }
                                }
                            }
                        )
                    } else {
                        Text("天气预报", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                navigationIcon = {
                    if (isSearchMode) {
                        IconButton(onClick = {
                            isSearchMode = false
                            searchText = ""
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "退出搜索", tint = Color.White)
                        }
                    } else {
                        // 汉堡按钮打开抽屉，子页面直接用LocalDrawerState
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "打开菜单", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                actions = {
                    if (isSearchMode) {
                        IconButton(onClick = {
                            val cityName = searchText.trim()
                            if (cityName.isNotBlank()) {
                                isSearchMode = false
                                searchText = ""
                                viewModel.getWeather(cityName)
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "确认搜索", tint = Color.White)
                        }
                    } else {
                        IconButton(onClick = { isSearchMode = true }) {
                            Icon(Icons.Default.Search, contentDescription = "搜索", tint = Color.White)
                        }
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多", tint = Color.White)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("刷新天气") },
                                onClick = {
                                    expanded = false
                                    viewModel.getWeather(selectedCity)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                }
            )
        }
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
        val weatherState = weatherState.invoke()
        Log.i("TAG", "WeatherScreen2: $weatherState")
        when {
            weatherState.loading -> Loading()
            weatherState.error != null -> ErrorView(weatherState.error)
            weatherState.weatherInfo == null -> ErrorView("weather is null")
            else -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WeatherToday(weatherState.weatherInfo, onCitySelect)
                    FutureWeather(featureWeatherState)
                }
//                WeatherToday(weatherState.weatherInfo, onCitySelect)
            }
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
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.White
        )
    }
}

@Composable
fun ErrorView(errorMsg: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMsg,
            color = Color.White,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
    }
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
            painter = painterResource(R.drawable.ic_0_2x),
            contentDescription = "",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = weatherInfo.weatherNow.text,
            color = MaterialTheme.colorScheme.onPrimary,
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
        ),
        onCitySelect = {
        }
    )
}

@Preview(
    device = "id:pixel_6",
    showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
    backgroundColor = 0xFF3F51B5
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
        },
        {
            WeatherDailyState(
                weatherDaily = FakeData.weatherDailyResponse.weatherDailies
            )
        },
        {
        }
    )
}
