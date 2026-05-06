package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.ContractResponse
import com.kma.lamphoun.roomapp.data.remote.dto.CreateContractRequest
import com.kma.lamphoun.roomapp.data.remote.dto.ExtendContractRequest
import com.kma.lamphoun.roomapp.data.remote.dto.InvoiceResponse
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

sealed class ContractDetailUiState {
    object Loading : ContractDetailUiState()
    data class Success(val contract: ContractResponse) : ContractDetailUiState()
    data class Error(val message: String) : ContractDetailUiState()
}

sealed class ContractActionState {
    object Idle : ContractActionState()
    object Loading : ContractActionState()
    object Success : ContractActionState()
    data class Error(val message: String) : ContractActionState()
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

    private val _detailState = MutableStateFlow<ContractDetailUiState>(ContractDetailUiState.Loading)
    val detailState: StateFlow<ContractDetailUiState> = _detailState

    private val _contractInvoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val contractInvoices: StateFlow<List<InvoiceResponse>> = _contractInvoices

    private val _actionState = MutableStateFlow<ContractActionState>(ContractActionState.Idle)
    val actionState: StateFlow<ContractActionState> = _actionState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        loadContracts()
        loadAvailableRooms()
        loadTenants()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadContracts()
            _isRefreshing.value = false
        }
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

    fun terminateContract(id: Long, note: String) {
        viewModelScope.launch {
            _actionState.value = ContractActionState.Loading
            try {
                val response = api.terminateContract(id, TerminateContractRequest(
                    terminatedAt = java.time.LocalDate.now().toString(),
                    note = note.ifBlank { null }
                ))
                if (response.isSuccessful && response.body()?.success == true) {
                    _actionState.value = ContractActionState.Success
                    loadContract(id)
                    loadContracts()
                } else {
                    _actionState.value = ContractActionState.Error(response.body()?.message ?: "Chấm dứt hợp đồng thất bại")
                }
            } catch (e: Exception) {
                _actionState.value = ContractActionState.Error("Lỗi kết nối")
            }
        }
    }

    fun loadContract(id: Long) {
        viewModelScope.launch {
            _detailState.value = ContractDetailUiState.Loading
            try {
                val response = api.getContractById(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _detailState.value = ContractDetailUiState.Success(response.body()!!.data!!)
                } else {
                    _detailState.value = ContractDetailUiState.Error(response.body()?.message ?: "Không tải được hợp đồng")
                }
            } catch (e: Exception) {
                _detailState.value = ContractDetailUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun loadContractInvoices(contractId: Long) {
        viewModelScope.launch {
            try {
                val response = api.getInvoicesByContract(contractId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _contractInvoices.value = response.body()!!.data!!.content
                } else {
                    _contractInvoices.value = emptyList()
                }
            } catch (e: Exception) {
                _contractInvoices.value = emptyList()
            }
        }
    }

    fun extendContract(id: Long, newEndDate: String) {
        viewModelScope.launch {
            _actionState.value = ContractActionState.Loading
            try {
                val response = api.extendContract(id, ExtendContractRequest(newEndDate = newEndDate))
                if (response.isSuccessful && response.body()?.success == true) {
                    _actionState.value = ContractActionState.Success
                    loadContract(id)
                } else {
                    _actionState.value = ContractActionState.Error(response.body()?.message ?: "Gia hạn hợp đồng thất bại")
                }
            } catch (e: Exception) {
                _actionState.value = ContractActionState.Error("Lỗi kết nối")
            }
        }
    }

    fun resetActionState() { _actionState.value = ContractActionState.Idle }

    fun resetFormState() { _formState.value = ContractFormUiState.Idle }
}

