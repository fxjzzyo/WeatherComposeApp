package com.zilin.weathercompose.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zilin.weathercompose.data.db.User
import com.zilin.weathercompose.data.db.UserDao
import com.zilin.weathercompose.data.repository.LoginRepo
import kotlinx.coroutines.launch

class LoginViewModel(private val userDao: UserDao, private val loginRepo: LoginRepo) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    fun register(onSuccess: () -> Unit, onMsg: (String) -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                onMsg("账号密码不能为空")
                return@launch
            }
            val count = userDao.isUserExist(username)
            if (count > 0) {
                onMsg("账号已存在")
                return@launch
            }
            val newUser = User(username = username, password = password)
            userDao.insertUser(newUser)
            onSuccess()
        }
    }

    fun login(onSuccess: () -> Unit, onMsg: (String) -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUser(username, password)
            if (user == null) {
                onMsg("账号或密码错误")
                return@launch
            }
            loginRepo.saveLoginUid(user.uid)
            onSuccess()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            loginRepo.logout()
            onSuccess()
        }
    }
}
