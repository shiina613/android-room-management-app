package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val data: DashboardResponse) : DashboardUiState()
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
                val response = api.getDashboard()
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = DashboardUiState.Success(response.body()!!.data!!)
                } else {
                    _uiState.value = DashboardUiState.Error("Không tải được dữ liệu")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Lỗi kết nối")
            }
        }
    }
}
