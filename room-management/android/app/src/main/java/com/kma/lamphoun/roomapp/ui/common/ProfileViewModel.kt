package com.kma.lamphoun.roomapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.ChangePasswordRequest
import com.kma.lamphoun.roomapp.data.remote.dto.UpdateProfileRequest
import com.kma.lamphoun.roomapp.data.remote.dto.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserResponse? = null,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val isChangingPassword: Boolean = false,
    val error: String? = null,
    val passwordError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = apiService.getMyProfile()
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            fullName = user.fullName ?: "",
                            email = user.email ?: "",
                            phone = user.phone ?: ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.body()?.message ?: "Không thể tải thông tin hồ sơ"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Không thể kết nối server")
                }
            }
        }
    }

    fun onFullNameChanged(v: String) {
        _uiState.update { it.copy(fullName = v) }
    }

    fun onEmailChanged(v: String) {
        _uiState.update { it.copy(email = v) }
    }

    fun onPhoneChanged(v: String) {
        _uiState.update { it.copy(phone = v) }
    }

    fun updateProfile() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null, updateSuccess = false) }
            try {
                val response = apiService.updateProfile(
                    UpdateProfileRequest(
                        fullName = state.fullName,
                        email = state.email,
                        phone = state.phone
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data!!
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = true,
                            user = user,
                            fullName = user.fullName ?: "",
                            email = user.email ?: "",
                            phone = user.phone ?: ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            error = response.body()?.message ?: "Cập nhật thất bại"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isUpdating = false, error = "Không thể kết nối server")
                }
            }
        }
    }

    fun showPasswordDialog() {
        _uiState.update {
            it.copy(
                showPasswordDialog = true,
                currentPassword = "",
                newPassword = "",
                passwordError = null
            )
        }
    }

    fun dismissPasswordDialog() {
        _uiState.update {
            it.copy(
                showPasswordDialog = false,
                currentPassword = "",
                newPassword = "",
                passwordError = null
            )
        }
    }

    fun onCurrentPasswordChanged(v: String) {
        _uiState.update { it.copy(currentPassword = v, passwordError = null) }
    }

    fun onNewPasswordChanged(v: String) {
        _uiState.update { it.copy(newPassword = v, passwordError = null) }
    }

    fun changePassword() {
        val state = _uiState.value
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(passwordError = "Mật khẩu mới phải có ít nhất 6 ký tự") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, passwordError = null) }
            try {
                val response = apiService.changePassword(
                    ChangePasswordRequest(
                        currentPassword = state.currentPassword,
                        newPassword = state.newPassword
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.update {
                        it.copy(
                            isChangingPassword = false,
                            showPasswordDialog = false,
                            currentPassword = "",
                            newPassword = ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isChangingPassword = false,
                            passwordError = response.body()?.message ?: "Đổi mật khẩu thất bại"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isChangingPassword = false, passwordError = "Không thể kết nối server")
                }
            }
        }
    }
}
