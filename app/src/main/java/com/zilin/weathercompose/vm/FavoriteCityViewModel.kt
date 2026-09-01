package com.zilin.weathercompose.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zilin.weathercompose.data.db.CityEntity
import com.zilin.weathercompose.data.db.UserCity
import com.zilin.weathercompose.data.db.UserCityDao
import com.zilin.weathercompose.data.repository.LoginRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteCityViewModel(private val userCityDao: UserCityDao, private val loginRepo: LoginRepo) : ViewModel() {
    // 当前登录uid
    private val currentUidFlow = loginRepo.currentUidFlow

    // ✅ 自动跟随uid切换，数据库变更自动推送新数据
    val cityList: StateFlow<List<UserCity>> = currentUidFlow
        .flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                userCityDao.getCityListByUid(uid)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCity(cityEntity: CityEntity, onMsg: (String) -> Unit) {
        viewModelScope.launch {
            val uid = currentUidFlow.firstOrNull() ?: return@launch
            userCityDao.insertCity(
                UserCity(
                    uid,
                    cityEntity.cityName,
                    cityEntity.cityCode,
                    cityEntity.province
                )
            )
            onMsg("添加成功")
        }
    }

    fun addCityList(cityList: List<CityEntity>, onMsg: (String) -> Unit) {
        viewModelScope.launch {
            val uid = currentUidFlow.firstOrNull() ?: return@launch

            val userCityList = cityList.map {
                UserCity(uid, it.cityName, it.cityCode, it.province)
            }
            userCityDao.addFavoriteList(userCityList)
            onMsg("添加成功")
        }
    }

    fun deleteCity(cityName: String) {
        viewModelScope.launch {
            val uid = currentUidFlow.firstOrNull() ?: return@launch
            userCityDao.deleteCity(uid, cityName)
        }
    }
}
