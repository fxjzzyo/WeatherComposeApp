package com.zilin.weathercompose.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zilin.weathercompose.vm.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onGotoRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var toast by remember { mutableStateOf("") }
    LaunchedEffect(toast) {
        if (toast.isNotEmpty()) {
            delay(1500)
            toast = ""
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("天气预报 - 登录",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary)
        Spacer(Modifier.height(32.dp))
        TextField(
            value = vm.username,
            onValueChange = { vm.username = it },
            label = { Text("账号") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = vm.password,
            onValueChange = { vm.password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            vm.login(onSuccess = onLoginSuccess) { toast = it }
        }) {
            Text("登录")
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onGotoRegister) {
            Text("没有账号？去注册")
        }
        if(toast.isNotEmpty()){
            Text(toast, color = Color.Red, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    vm: LoginViewModel,
    onRegisterSuccess: () -> Unit
) {
    var toast by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("注册账号",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary)
        Spacer(Modifier.height(32.dp))
        TextField(
            value = vm.username,
            onValueChange = { vm.username = it },
            label = { Text("账号") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = vm.password,
            onValueChange = { vm.password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            vm.register(onSuccess = onRegisterSuccess) { toast = it }
        }) {
            Text("注册")
        }
        if(toast.isNotEmpty()){
            Text(toast, color = Color.Red, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
