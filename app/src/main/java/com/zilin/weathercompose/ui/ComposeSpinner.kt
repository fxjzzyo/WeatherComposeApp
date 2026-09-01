package com.zilin.weathercompose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComposeSpinner(
    options: List<String>,
    selectedValue: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        // 触发按钮（相当于Spinner显示选中项）
        OutlinedButton(
            onClick = { expanded = !expanded }
        ) {
            Text(text = selectedValue)
        }

        // 下拉弹窗：通过 offset 把 popup 锚点下移到按钮下方
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }

        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "ComposeSpinner Preview",
    showSystemUi = true,
    device = "id:pixel_6"
)
@Composable
fun ComposeSpinnerPreview() {
    val options = listOf(
        "北京",
        "上海",
        "广州",
        "深圳",
        "杭州"
    )
    var selectedValue by remember { mutableStateOf(options[0]) }

    ComposeSpinner(
        options = options,
        selectedValue = selectedValue,
        onSelect = { selectedValue = it }
    )
}
