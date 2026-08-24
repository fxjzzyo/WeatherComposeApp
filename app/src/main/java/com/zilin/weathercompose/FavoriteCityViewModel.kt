package com.zilin.weathercompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zilin.weathercompose.data.db.CityDao
import com.zilin.weathercompose.data.db.CityEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteCityViewModel(
    private val dao: CityDao
) : ViewModel() {

    // 收藏城市列表 Flow，UI直接collectAsState
    val favoriteCityList: Flow<List<CityEntity>> = dao.getAllFavoriteCity()

    // 添加收藏
    fun addCity(city: CityEntity) {
        viewModelScope.launch {
            dao.addFavorite(city)
        }
    }

    //批量添加多个城市
    fun addCityList(cityList: List<CityEntity>) {
        viewModelScope.launch {
            dao.addFavoriteList(cityList)
        }
    }

    // 删除收藏
    fun removeCity(city: CityEntity) {
        viewModelScope.launch {
            dao.deleteFavorite(city)
        }
    }

    // 判断是否收藏
    suspend fun checkCollect(code: String): Boolean {
        return dao.isCityCollected(code) > 0
    }
}
