package com.kma.lamphoun.roomapp.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.ChangePasswordRequest
import com.kma.lamphoun.roomapp.data.remote.dto.UpdateProfileRequest
import com.kma.lamphoun.roomapp.data.remote.dto.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    object Success : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _profile = MutableStateFlow<UserResponse?>(null)
    val profile: StateFlow<UserResponse?> = _profile

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val response = api.getMyProfile()
                if (response.isSuccessful && response.body()?.success == true) {
                    _profile.value = response.body()!!.data
                }
            } catch (e: Exception) {
                // silently fail — profile is optional display
            }
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateState.value = ProfileUpdateState.Loading
            try {
                val response = api.updateProfile(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _profile.value = response.body()!!.data
                    _updateState.value = ProfileUpdateState.Success
                } else {
                    _updateState.value = ProfileUpdateState.Error(response.body()?.message ?: "Cập nhật thất bại")
                }
            } catch (e: Exception) {
                _updateState.value = ProfileUpdateState.Error("Lỗi kết nối")
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _updateState.value = ProfileUpdateState.Loading
            try {
                val response = api.changePassword(ChangePasswordRequest(currentPassword, newPassword))
                if (response.isSuccessful) {
                    _updateState.value = ProfileUpdateState.Success
                } else {
                    _updateState.value = ProfileUpdateState.Error(response.body()?.message ?: "Đổi mật khẩu thất bại")
                }
            } catch (e: Exception) {
                _updateState.value = ProfileUpdateState.Error("Lỗi kết nối")
            }
        }
    }
}

