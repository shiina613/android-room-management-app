package com.kma.lamphoun.roomapp.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.NotificationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadNotifications() }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getNotifications(page = 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()!!.data!!.content
                    _notifications.value = list
                    _unreadCount.value = list.count { !it.read }
                }
            } catch (e: Exception) { /* ignore */ }
            _isLoading.value = false
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                api.markAllRead()
                loadNotifications()
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch {
            try {
                api.markNotificationRead(id)
                loadNotifications()
            } catch (e: Exception) { /* ignore */ }
        }
    }
}

