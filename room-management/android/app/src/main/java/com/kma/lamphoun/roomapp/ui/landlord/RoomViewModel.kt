package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.RoomRequest
import com.kma.lamphoun.roomapp.data.remote.dto.RoomResponse
import com.kma.lamphoun.roomapp.data.remote.dto.UpdateRoomStatusRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

sealed class RoomListUiState {
    object Loading : RoomListUiState()
    data class Success(val rooms: List<RoomResponse>) : RoomListUiState()
    data class Error(val message: String) : RoomListUiState()
}

sealed class RoomFormUiState {
    object Idle : RoomFormUiState()
    object Loading : RoomFormUiState()
    object Success : RoomFormUiState()
    data class Error(val message: String) : RoomFormUiState()
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _listState = MutableStateFlow<RoomListUiState>(RoomListUiState.Loading)
    val listState: StateFlow<RoomListUiState> = _listState

    private val _formState = MutableStateFlow<RoomFormUiState>(RoomFormUiState.Idle)
    val formState: StateFlow<RoomFormUiState> = _formState

    private val _selectedRoom = MutableStateFlow<RoomResponse?>(null)
    val selectedRoom: StateFlow<RoomResponse?> = _selectedRoom

    init { loadRooms() }

    fun loadRooms(status: String? = null, keyword: String? = null) {
        viewModelScope.launch {
            _listState.value = RoomListUiState.Loading
            try {
                val response = api.getRooms(status = status, keyword = keyword, size = 50)
                if (response.isSuccessful && response.body()?.success == true) {
                    _listState.value = RoomListUiState.Success(response.body()!!.data!!.content)
                } else {
                    _listState.value = RoomListUiState.Error(response.body()?.message ?: "Không tải được danh sách phòng")
                }
            } catch (e: Exception) {
                _listState.value = RoomListUiState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun loadRoom(id: Long) {
        viewModelScope.launch {
            try {
                val response = api.getRoomById(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _selectedRoom.value = response.body()!!.data
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun createRoom(request: RoomRequest) {
        viewModelScope.launch {
            _formState.value = RoomFormUiState.Loading
            try {
                val response = api.createRoom(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _formState.value = RoomFormUiState.Success
                    loadRooms()
                } else {
                    _formState.value = RoomFormUiState.Error(response.body()?.message ?: "Tạo phòng thất bại")
                }
            } catch (e: Exception) {
                _formState.value = RoomFormUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun updateRoom(id: Long, request: RoomRequest) {
        viewModelScope.launch {
            _formState.value = RoomFormUiState.Loading
            try {
                val response = api.updateRoom(id, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _formState.value = RoomFormUiState.Success
                    loadRooms()
                } else {
                    _formState.value = RoomFormUiState.Error(response.body()?.message ?: "Cập nhật thất bại")
                }
            } catch (e: Exception) {
                _formState.value = RoomFormUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun updateStatus(id: Long, status: String) {
        viewModelScope.launch {
            try {
                api.updateRoomStatus(id, UpdateRoomStatusRequest(status))
                loadRooms()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun deleteRoom(id: Long) {
        viewModelScope.launch {
            try {
                api.deleteRoom(id)
                loadRooms()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun resetFormState() { _formState.value = RoomFormUiState.Idle }

    fun uploadRoomImage(roomId: Long, imageFile: File) {
        viewModelScope.launch {
            try {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)
                val response = api.uploadRoomImage(roomId, part)
                if (response.isSuccessful && response.body()?.success == true) {
                    _selectedRoom.value = response.body()!!.data
                    loadRooms()
                }
            } catch (e: Exception) {
                // ignore silently — ảnh không upload được không block flow chính
            }
        }
    }
}

