package com.kma.lamphoun.roomapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.LoginRequest
import com.kma.lamphoun.roomapp.data.remote.dto.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    val isLoggedIn = tokenDataStore.isLoggedIn
    val role = tokenDataStore.role

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data!!
                    tokenDataStore.save(data.accessToken, data.role, data.userId, data.fullName)
                    _uiState.value = AuthUiState.Success(data.role)
                } else {
                    _uiState.value = AuthUiState.Error(response.body()?.message ?: "Sai tên đăng nhập hoặc mật khẩu")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Không thể kết nối server")
            }
        }
    }

    fun register(username: String, password: String, email: String, fullName: String, phone: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = api.register(RegisterRequest(username, password, email, fullName, phone, role))
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data!!
                    tokenDataStore.save(data.accessToken, data.role, data.userId, data.fullName)
                    _uiState.value = AuthUiState.Success(data.role)
                } else {
                    _uiState.value = AuthUiState.Error(response.body()?.message ?: "Đăng ký thất bại")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Không thể kết nối server")
            }
        }
    }

    fun logout() {
        viewModelScope.launch { tokenDataStore.clear() }
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
}

