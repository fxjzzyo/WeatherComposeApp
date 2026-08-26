package com.zilin.weathercompose.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zilin.weathercompose.data.db.UserBgConfig
import com.zilin.weathercompose.data.db.UserBgConfigDao
import com.zilin.weathercompose.data.repository.LoginRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BgSettingViewModel(
    private val userBgConfigDao: UserBgConfigDao,
    private val loginRepo: LoginRepo
) : ViewModel() {

    val bgList = listOf("bg1", "bg2", "bg3", "bg4")

    val selectedBgResName: StateFlow<String?> = loginRepo.currentUidFlow
        .flatMapLatest { uid ->
            if (uid == null) {
                flowOf("bg1")
            } else {
                userBgConfigDao.getBgConfigByUid(uid)
                    .map { it?.bgResName ?: "bg1" }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "bg1"
        )

    fun saveBg(bgResName: String?) {
        viewModelScope.launch {
            val uid = loginRepo.currentUidFlow.firstOrNull() ?: return@launch
            userBgConfigDao.saveBgConfig(
                UserBgConfig(uid = uid, bgResName = bgResName)
            )
        }
    }
}
