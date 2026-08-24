package com.zilin.weathercompose.ui.bg

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.zilin.weathercompose.LocalDrawerState
import com.zilin.weathercompose.data.fake.FakeData.bgResList
import com.zilin.weathercompose.util.BgDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BgNavHostPage(modifier: Modifier = Modifier) {
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
            BgSetting()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BgSetting() {
    val context = LocalContext.current
    // pager状态
    val pagerState = rememberPagerState(pageCount = { bgResList.size })
    // 当前预览选中的页面索引（滑动时实时变化）
    var previewSelectIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // 同步pager滑动位置到previewSelectIndex
    LaunchedEffect(pagerState.currentPage) {
        previewSelectIndex = pagerState.currentPage
    }

    // 页面打开，读取DataStore中已经保存的背景，跳转到对应page
    LaunchedEffect(Unit) {
        BgDataStore.getBgIndexFlow(context).collectLatest { savedIndex ->
            pagerState.scrollToPage(savedIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "选择背景图片",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary)

        // 横向滑动Pager
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
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = bgResList[page]),
                    contentDescription = "背景预览",
                    modifier = Modifier
                        .size(320.dp, 520.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // ✅小圆点指示器
        PagerDotIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // 确认按钮：保存当前预览的索引到DataStore
        Button(
            onClick = {
                scope.launch {
                    BgDataStore.saveBgIndex(context, previewSelectIndex)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("确认使用此背景", color = MaterialTheme.colorScheme.onPrimary)
        }

        Button(
            onClick = {
                scope.launch {
                    BgDataStore.saveBgIndex(context, 0)
                    pagerState.scrollToPage(0)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
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
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Gray.copy(alpha = 0.4f)
            ) {}
        }
    }
}