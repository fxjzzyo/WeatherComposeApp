package com.zilin.weathercompose.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.*
// 扩展
val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore("login_info")
private val KEY_CURRENT_UID = longPreferencesKey("current_uid")

sealed class LoginUiState {
    object Loading : LoginUiState()
    data class Success(val uid: Long?) : LoginUiState()
}

class LoginRepo(private val context: Context) {

    val loginStateFlow: Flow<LoginUiState> = flow {
        emit(LoginUiState.Loading)
        emitAll(
            context.loginDataStore.data
                .map { prefs ->
                    val uid = prefs[KEY_CURRENT_UID]
                    LoginUiState.Success(uid)
                }
        )
    }

    // 获取当前登录uid，null=未登录
    val currentUidFlow: Flow<Long?> = context.loginDataStore.data
        .map { prefs -> prefs[KEY_CURRENT_UID] }

    // 保存登录uid
    suspend fun saveLoginUid(uid: Long) {
        Log.d("LoginRepo", "保存uid=$uid")
        context.loginDataStore.edit { it[KEY_CURRENT_UID] = uid }
    }

    // 退出登录，清空uid
    suspend fun logout() {
        context.loginDataStore.edit { it.remove(KEY_CURRENT_UID) }
    }
}
