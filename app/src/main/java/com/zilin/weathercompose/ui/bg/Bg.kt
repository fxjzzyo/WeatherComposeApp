package com.zilin.weathercompose.ui.bg

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zilin.weathercompose.LocalDrawerState
import com.zilin.weathercompose.MyApp
import com.zilin.weathercompose.data.repository.LoginRepo
import com.zilin.weathercompose.vm.BgSettingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BgNavHostPage(modifier: Modifier = Modifier) {
    val myApp = LocalContext.current.applicationContext as MyApp
    val userBgDao = myApp.db.userBgConfigDao()
    val loginRepo = LoginRepo(myApp)

    val bgVM = viewModel {
        BgSettingViewModel(userBgConfigDao = userBgDao, loginRepo = loginRepo)
    }
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("背景选择", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "菜单", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BgSetting(vm = bgVM)
        }
    }
}

@Composable
fun BgSetting(
    vm: BgSettingViewModel,
    modifier: Modifier = Modifier
) {
    val bgList = remember { listOf("bg1", "bg2", "bg3", "bg4") }
    // 核心修复这一行
    val currentBgName by vm.selectedBgResName.collectAsStateWithLifecycle()

    val initPageIndex = remember(currentBgName) {
        bgList.indexOf(currentBgName ?: "bg1").coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(
        initialPage = initPageIndex,
        pageCount = { bgList.size }
    )
    var previewSelectIndex by remember { mutableIntStateOf(initPageIndex) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        previewSelectIndex = pagerState.currentPage
    }
    // 数据库用户背景变更，自动滚动到对应页
    LaunchedEffect(currentBgName) {
        val idx = bgList.indexOf(currentBgName ?: "bg1").coerceAtLeast(0)
        pagerState.scrollToPage(idx)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "选择背景图片",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 10.dp),
            contentPadding = PaddingValues(start = 0.dp),
            pageSpacing = 0.dp,
            pageSize = PageSize.Fill
        ) { page ->
            val resName = bgList[page]
            val ctx = LocalContext.current

            val resId = remember(resName) {
                ctx.resources.getIdentifier(resName, "drawable", ctx.packageName)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "背景预览",
                    modifier = Modifier.size(320.dp, 520.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        PagerDotIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Button(
            onClick = {
                val selectName = bgList[previewSelectIndex]
                vm.saveBg(selectName)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("确认使用此背景", color = MaterialTheme.colorScheme.onPrimary)
        }

        Button(
            onClick = {
                vm.saveBg("bg1")
                scope.launch {
                    pagerState.scrollToPage(0)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text("恢复默认背景")
        }
    }
}

/**
 * Pager小圆点指示器
 */
@Composable
fun PagerDotIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (isSelected) 12.dp else 8.dp),
                shape = CircleShape,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Gray.copy(alpha = 0.4f)
                }
            ) {}
        }
    }
}
