package com.zilin.weathercompose.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zilin.weathercompose.FavoriteCityViewModel
import com.zilin.weathercompose.data.db.CityDao

class FavoriteCityViewModelFactory(
    private val dao: CityDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 判断目标ViewModel类型
        if (modelClass.isAssignableFrom(FavoriteCityViewModel::class.java)) {
            return FavoriteCityViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
