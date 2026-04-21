package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.MeterReadingRequest
import com.kma.lamphoun.roomapp.data.remote.dto.MeterReadingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MeterReadingUiState {
    object Idle : MeterReadingUiState()
    object Loading : MeterReadingUiState()
    data class Success(val reading: MeterReadingResponse) : MeterReadingUiState()
    data class Error(val message: String) : MeterReadingUiState()
}

@HiltViewModel
class MeterReadingViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _saveState = MutableStateFlow<MeterReadingUiState>(MeterReadingUiState.Idle)
    val saveState: StateFlow<MeterReadingUiState> = _saveState

    private val _history = MutableStateFlow<List<MeterReadingResponse>>(emptyList())
    val history: StateFlow<List<MeterReadingResponse>> = _history

    private val _latestReading = MutableStateFlow<MeterReadingResponse?>(null)
    val latestReading: StateFlow<MeterReadingResponse?> = _latestReading

    fun loadHistory(roomId: Long) {
        viewModelScope.launch {
            try {
                val response = api.getMeterReadingHistory(roomId, page = 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    val readings = response.body()!!.data!!.content
                    _history.value = readings
                    _latestReading.value = readings.firstOrNull()
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun recordReading(roomId: Long, billingMonth: String,
                      electricPrevious: Double, electricCurrent: Double,
                      waterPrevious: Double, waterCurrent: Double) {
        viewModelScope.launch {
            _saveState.value = MeterReadingUiState.Loading
            try {
                val response = api.recordMeterReading(
                    MeterReadingRequest(
                        roomId = roomId,
                        billingMonth = billingMonth,
                        electricPrevious = electricPrevious,
                        electricCurrent = electricCurrent,
                        waterPrevious = waterPrevious,
                        waterCurrent = waterCurrent
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _saveState.value = MeterReadingUiState.Success(response.body()!!.data!!)
                    loadHistory(roomId)
                } else {
                    _saveState.value = MeterReadingUiState.Error(response.body()?.message ?: "Lưu chỉ số thất bại")
                }
            } catch (e: Exception) {
                _saveState.value = MeterReadingUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun resetState() { _saveState.value = MeterReadingUiState.Idle }
}

