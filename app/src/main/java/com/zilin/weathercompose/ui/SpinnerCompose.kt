package com.zilin.weathercompose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialSpinner(
    options: List<String>,
    selectedValue: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .width(180.dp)
            .padding(top = 40.dp)
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("请选择", color = MaterialTheme.colorScheme.onPrimary) },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f),
                    tint = Color.White
                )},
            colors = OutlinedTextFieldDefaults.colors(
                // 输入框背景透明
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                // 边框白色
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                // 光标颜色
                cursorColor = Color.White
            ),
            modifier = Modifier.menuAnchor(
                ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                true
            ) // 必须加这个标记锚点
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.Black.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier.heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState())
            ) {
                options.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city, color = MaterialTheme.colorScheme.onPrimary) },
                        onClick = {
                            onSelect(city)
                            expanded = false
                        }
                    )
                }
            }

            /*LazyColumn(modifier = Modifier.heightIn(350.dp)
                .fillMaxWidth()) {
                items(options, key = {it}) { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            onSelect(city)
                            expanded = false
                        }
                    )
                }
            }*/

        }
    }
}

@Preview(showBackground = true, name = "OfficialSpinner Preview", showSystemUi = true,
    backgroundColor = 0xFF3F51B5
)
@Composable
fun OfficialSpinnerPreview() {
    val options = listOf(
        "北京", "上海", "广州", "深圳", "杭州", "北京", "上海", "广州", "深圳", "杭州",
        "北京", "上海", "广州", "深圳", "杭州", "北京", "上海", "广州", "深圳", "杭州"
    )
    var selectedValue by remember { mutableStateOf(options[0]) }

    OfficialSpinner(
        options = options,
        selectedValue = selectedValue,
        onSelect = { selectedValue = it }
    )
}