package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.InvoiceResponse
import com.kma.lamphoun.roomapp.data.remote.dto.MarkPaidRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class InvoiceListUiState {
    object Loading : InvoiceListUiState()
    data class Success(val invoices: List<InvoiceResponse>) : InvoiceListUiState()
    data class Error(val message: String) : InvoiceListUiState()
}

sealed class InvoiceDetailUiState {
    object Loading : InvoiceDetailUiState()
    data class Success(val invoice: InvoiceResponse) : InvoiceDetailUiState()
    data class Error(val message: String) : InvoiceDetailUiState()
}

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _listState = MutableStateFlow<InvoiceListUiState>(InvoiceListUiState.Loading)
    val listState: StateFlow<InvoiceListUiState> = _listState

    private val _detailState = MutableStateFlow<InvoiceDetailUiState>(InvoiceDetailUiState.Loading)
    val detailState: StateFlow<InvoiceDetailUiState> = _detailState

    private val _markPaidState = MutableStateFlow<Boolean>(false)
    val markPaidState: StateFlow<Boolean> = _markPaidState

    init { loadInvoices() }

    fun loadInvoices(status: String? = null) {
        viewModelScope.launch {
            _listState.value = InvoiceListUiState.Loading
            try {
                val response = api.getMyInvoices(status = status, page = 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    _listState.value = InvoiceListUiState.Success(response.body()!!.data!!.content)
                } else {
                    _listState.value = InvoiceListUiState.Error(response.body()?.message ?: "Không tải được hóa đơn")
                }
            } catch (e: Exception) {
                _listState.value = InvoiceListUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun loadInvoice(id: Long) {
        viewModelScope.launch {
            _detailState.value = InvoiceDetailUiState.Loading
            try {
                val response = api.getInvoiceById(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _detailState.value = InvoiceDetailUiState.Success(response.body()!!.data!!)
                } else {
                    _detailState.value = InvoiceDetailUiState.Error(response.body()?.message ?: "Không tải được hóa đơn")
                }
            } catch (e: Exception) {
                _detailState.value = InvoiceDetailUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun markPaid(id: Long) {
        viewModelScope.launch {
            try {
                val response = api.markInvoicePaid(id, MarkPaidRequest())
                if (response.isSuccessful && response.body()?.success == true) {
                    _markPaidState.value = true
                    loadInvoice(id)
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun resetMarkPaid() { _markPaidState.value = false }
}

