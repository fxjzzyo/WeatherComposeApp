package com.zilin.weathercompose

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.kotlinweather2.data.WeatherNow
import com.example.kotlinweather2.data.daily.WeatherDaily
import com.zilin.weathercompose.data.Location
import com.zilin.weathercompose.data.WeatherDailyState
import com.zilin.weathercompose.data.WeatherInfo
import com.zilin.weathercompose.data.WeatherState
import com.zilin.weathercompose.data.fake.FakeData
import com.zilin.weathercompose.data.remote.WeatherRetrofitClient
import com.zilin.weathercompose.data.repository.WeatherRepository
import com.zilin.weathercompose.ui.OfficialSpinner
import com.zilin.weathercompose.ui.theme.WeatherComposeTheme


class MainActivity : ComponentActivity() {

    private val initCity: String = "北京"

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

                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painterResource(R.drawable.bg1),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        /*Box(
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
                        )*/

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = Color.Transparent,
                        ) { padding ->
                            Log.i("TAG", "onCreate: before launched effect.")
                            val p = padding
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
                                modifier = Modifier.padding(padding)
                            )
                            LaunchedEffect(Unit) {
                                viewModel.getWeather(initCity)
                                viewModel.getWeatherDaily(initCity)
                            }
                        }
                    }
                }
            }
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
            modifier = Modifier.padding(horizontal = 16.dp))
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

        Text(text = weatherInfo.location.path,
            fontSize = TextUnit(20f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "${weatherInfo.weatherNow.temperature}℃",
            fontSize = TextUnit(20f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "未来天气:",
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
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