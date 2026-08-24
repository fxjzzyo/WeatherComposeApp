package com.zilin.weathercompose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zilin.weathercompose.data.db.SelectCityItem
import com.zilin.weathercompose.data.fake.FakeData.getMockSelectCityList
import androidx.compose.runtime.mutableStateSetOf

@Composable
fun MultiSelectCityDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<SelectCityItem>) -> Unit
) {
    val candidateList = remember { getMockSelectCityList() }
    //保存选中的城市code集合，实现多选
    val selectedCodeSet = remember { mutableStateSetOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择要收藏的城市") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 350.dp)
            ) {
                items(candidateList, key = { it.cityCode }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedCodeSet.contains(item.cityCode),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedCodeSet.add(item.cityCode)
                                } else {
                                    selectedCodeSet.remove(item.cityCode)
                                }
                            }
                        )
                        Text(text = "${item.cityName}(${item.province})")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                //收集选中的城市
                val selected = candidateList.filter { selectedCodeSet.contains(it.cityCode) }
                onConfirm(selected)
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
