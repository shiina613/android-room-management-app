package com.kma.lamphoun.roomapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val isLoading: Boolean = false,
    val dashboard: DashboardResponse? = null,
    val adminName: String = "",
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val name = tokenDataStore.fullName.firstOrNull() ?: ""
            _uiState.update { it.copy(adminName = name) }
            loadDashboard()
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = apiService.getDashboard()
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.update { it.copy(isLoading = false, dashboard = response.body()!!.data) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không thể tải dữ liệu") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Lỗi kết nối") }
            }
        }
    }
}
