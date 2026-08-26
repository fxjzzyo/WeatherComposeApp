package com.zilin.weathercompose.ui.city

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zilin.weathercompose.vm.FavoriteCityViewModel
import com.zilin.weathercompose.LocalDrawerState
import com.zilin.weathercompose.MyApp
import com.zilin.weathercompose.data.db.CityEntity
import com.zilin.weathercompose.data.repository.LoginRepo
import com.zilin.weathercompose.ui.MultiSelectCityDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCityNavHostPage(onCityClick: (String) -> Unit) {
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    // 获取Application实例
    val myApp = LocalContext.current.applicationContext as MyApp
    val userCityDao = myApp.db.userCityDao()
    val loginRepo = LoginRepo(myApp)

    // 构造带参数的ViewModel
    val vm: FavoriteCityViewModel = viewModel {
        FavoriteCityViewModel(userCityDao, loginRepo)
    }

    //控制弹窗显示
    var showSelectDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("收藏城市", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "菜单", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    //这个页面独有的右上角按钮（添加城市加号）
                    IconButton(onClick = {
                        showSelectDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "添加城市", tint = Color.White)
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
            FavoriteCityPage(
                vm,
                modifier = Modifier.padding(innerPadding),
                onCityClick
            )

            //弹出多选城市弹窗
            if (showSelectDialog) {
                MultiSelectCityDialog(
                    onDismiss = { showSelectDialog = false },
                    onConfirm = { selectItems ->
                        //把选中的候选城市转为Room实体，批量插入
                        val entityList = selectItems.map {
                            CityEntity(
                                cityName = it.cityName,
                                cityCode = it.cityCode,
                                province = it.province
                            )
                        }
                        vm.addCityList(entityList) {
                            // todo fanlulin toast
                        }
                        showSelectDialog = false
                    }
                )
            }
        }
    }
}


@Composable
fun FavoriteCityPage(
    vm: FavoriteCityViewModel,
    modifier: Modifier = Modifier,
    onCityClick: (String) -> Unit
) {
    // 传给UI页面
    CityList(
        vm = vm,
        onCityClick = onCityClick
    )
}


@Composable
fun CityList(
    vm: FavoriteCityViewModel,
    onCityClick: (city: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cityList =
        vm.cityList.collectAsStateWithLifecycle(initialValue = emptyList())
    if (cityList.value.isEmpty()) {
        EmptyCity(modifier = modifier)
    } else {
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(items = cityList.value, key = {
                it.cityName
            }) { userCity ->
                CityItem(
                    userCity.cityName,
                    onItemClick = {
                        onCityClick(userCity.cityName)
                    },
                    onItemDelete = {
                        vm.deleteCity(userCity.cityName)
                    })
            }
        }
    }
}

@Composable
fun CityItem(
    cityName: String,
    onItemClick: () -> Unit,
    onItemDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable {
                onItemClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = cityName,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier
            .width(16.dp))
        IconButton(onClick = onItemDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "删除收藏",
                tint = Color.White
            )
        }
    }
}

@Composable
fun EmptyCity(modifier: Modifier = Modifier) {
    Text(
        "还没有收藏城市呢！",
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}