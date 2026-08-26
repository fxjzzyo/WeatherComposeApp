package com.zilin.weathercompose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import com.zilin.weathercompose.data.DrawerMenuItemBean
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    menuList: List<DrawerMenuItemBean>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.requiredWidth(200.dp),
        drawerContainerColor = Color.White.copy(alpha = 0.92f)
    ) {
        Column(modifier = Modifier.padding(vertical = 32.dp)) {
            menuList.forEach { item ->
                val isSelected = currentRoute == item.route || currentRoute?.startsWith("${item.route}?") == true
                NavigationDrawerItem(
                    label = { Text(item.title) },
                    selected = isSelected,
                    onClick = {
                        onItemClick(item.route)
                    },
                    modifier = Modifier.padding(
                        NavigationDrawerItemDefaults.ItemPadding
                    ),
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
