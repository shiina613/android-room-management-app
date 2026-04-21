package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.ContractResponse
import com.kma.lamphoun.roomapp.data.remote.dto.CreateContractRequest
import com.kma.lamphoun.roomapp.data.remote.dto.RoomResponse
import com.kma.lamphoun.roomapp.data.remote.dto.TenantResponse
import com.kma.lamphoun.roomapp.data.remote.dto.TerminateContractRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ContractListUiState {
    object Loading : ContractListUiState()
    data class Success(val contracts: List<ContractResponse>) : ContractListUiState()
    data class Error(val message: String) : ContractListUiState()
}

sealed class ContractFormUiState {
    object Idle : ContractFormUiState()
    object Loading : ContractFormUiState()
    object Success : ContractFormUiState()
    data class Error(val message: String) : ContractFormUiState()
}

@HiltViewModel
class ContractViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _listState = MutableStateFlow<ContractListUiState>(ContractListUiState.Loading)
    val listState: StateFlow<ContractListUiState> = _listState

    private val _formState = MutableStateFlow<ContractFormUiState>(ContractFormUiState.Idle)
    val formState: StateFlow<ContractFormUiState> = _formState

    private val _availableRooms = MutableStateFlow<List<RoomResponse>>(emptyList())
    val availableRooms: StateFlow<List<RoomResponse>> = _availableRooms

    private val _tenants = MutableStateFlow<List<TenantResponse>>(emptyList())
    val tenants: StateFlow<List<TenantResponse>> = _tenants

    init {
        loadContracts()
        loadAvailableRooms()
        loadTenants()
    }

    fun loadContracts(status: String? = null) {
        viewModelScope.launch {
            _listState.value = ContractListUiState.Loading
            try {
                val response = api.getContracts(status = status, size = 50)
                if (response.isSuccessful && response.body()?.success == true) {
                    _listState.value = ContractListUiState.Success(response.body()!!.data!!.content)
                } else {
                    _listState.value = ContractListUiState.Error(response.body()?.message ?: "Không tải được hợp đồng")
                }
            } catch (e: Exception) {
                _listState.value = ContractListUiState.Error("Lỗi kết nối")
            }
        }
    }

    private fun loadAvailableRooms() {
        viewModelScope.launch {
            try {
                val response = api.getRooms(status = "AVAILABLE", size = 100)
                if (response.isSuccessful && response.body()?.success == true) {
                    _availableRooms.value = response.body()!!.data!!.content
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    private fun loadTenants() {
        viewModelScope.launch {
            try {
                val response = api.getTenants(size = 100)
                if (response.isSuccessful && response.body()?.success == true) {
                    _tenants.value = response.body()!!.data!!.content
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun createContract(request: CreateContractRequest) {
        viewModelScope.launch {
            _formState.value = ContractFormUiState.Loading
            try {
                val response = api.createContract(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _formState.value = ContractFormUiState.Success
                    loadContracts()
                } else {
                    _formState.value = ContractFormUiState.Error(response.body()?.message ?: "Tạo hợp đồng thất bại")
                }
            } catch (e: Exception) {
                _formState.value = ContractFormUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun terminateContract(id: Long, reason: String) {
        viewModelScope.launch {
            try {
                api.terminateContract(id, TerminateContractRequest(
                    terminatedAt = java.time.LocalDate.now().toString(),
                    note = reason.ifBlank { null }
                ))
                loadContracts()
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun resetFormState() { _formState.value = ContractFormUiState.Idle }
}

