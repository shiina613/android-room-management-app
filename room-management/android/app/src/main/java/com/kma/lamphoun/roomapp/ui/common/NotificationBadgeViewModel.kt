package com.kma.lamphoun.roomapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.StompManager
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.WsPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationBadgeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val stompManager: StompManager
) : ViewModel() {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _inAppBanner = MutableStateFlow<WsPayload?>(null)
    val inAppBanner: StateFlow<WsPayload?> = _inAppBanner.asStateFlow()

    init {
        loadUnreadCount()
        observeWebSocketEvents()
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                val result = apiService.getUnreadCount()
                if (result.isSuccessful && result.body()?.success == true) {
                    _unreadCount.value = (result.body()!!.data?.count ?: 0L).toInt()
                }
            } catch (e: Exception) {
                // silently ignore
            }
        }
    }

    private fun observeWebSocketEvents() {
        viewModelScope.launch {
            stompManager.events.collect { payload ->
                // Increment unread count for any incoming event
                _unreadCount.update { it + 1 }
                // Show in-app banner for 4 seconds
                _inAppBanner.value = payload
                delay(4000)
                _inAppBanner.value = null
            }
        }
    }

    fun clearBanner() {
        _inAppBanner.value = null
    }
}
