package com.zilin.weathercompose.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zilin.weathercompose.data.db.UserBgConfigDao
import com.zilin.weathercompose.data.db.UserCityDao
import com.zilin.weathercompose.data.db.UserDao
import com.zilin.weathercompose.data.repository.LoginRepo

class AppViewModelFactory(
    private val userDao: UserDao,
    private val userCityDao: UserCityDao,
    private val userBgConfigDao: UserBgConfigDao,
    private val loginRepo: LoginRepo
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(userDao, loginRepo) as T
        modelClass.isAssignableFrom(
            FavoriteCityViewModel::class.java
        ) -> FavoriteCityViewModel(userCityDao, loginRepo) as T
        modelClass.isAssignableFrom(
            BgSettingViewModel::class.java
        ) -> BgSettingViewModel(userBgConfigDao, loginRepo) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class")
    }
}
