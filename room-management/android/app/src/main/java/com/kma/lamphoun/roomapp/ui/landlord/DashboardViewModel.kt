package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import com.kma.lamphoun.roomapp.data.remote.dto.NotificationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val data: DashboardResponse,
        val recentActivities: List<NotificationResponse> = emptyList()
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    var fullName: String = ""
        private set

    init {
        viewModelScope.launch {
            fullName = tokenDataStore.fullName.firstOrNull() ?: ""
            loadDashboard()
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val statsDeferred = async { api.getDashboard() }
                val notifDeferred = async {
                    try { api.getNotifications(page = 0) } catch (e: Exception) { null }
                }

                val statsResponse = statsDeferred.await()
                val notifResponse = notifDeferred.await()

                if (statsResponse.isSuccessful && statsResponse.body()?.success == true) {
                    val notifications = if (notifResponse?.isSuccessful == true && notifResponse.body()?.success == true) {
                        notifResponse.body()!!.data?.content?.take(5) ?: emptyList()
                    } else {
                        emptyList()
                    }
                    _uiState.value = DashboardUiState.Success(
                        data = statsResponse.body()!!.data!!,
                        recentActivities = notifications
                    )
                } else {
                    _uiState.value = DashboardUiState.Error("Không tải được dữ liệu")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Lỗi kết nối")
            }
        }
    }
}

