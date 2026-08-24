package com.zilin.weathercompose.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_setting")

object BgDataStore {
    // 保存选中背景图片索引，默认0（第一张）
    private val KEY_BG_INDEX = intPreferencesKey("bg_image_index")

    fun getBgIndexFlow(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { pref ->
                pref[KEY_BG_INDEX] ?: 0
            }
    }

    suspend fun saveBgIndex(context: Context, index: Int) {
        context.dataStore.edit { pref ->
            pref[KEY_BG_INDEX] = index
        }
    }
}
