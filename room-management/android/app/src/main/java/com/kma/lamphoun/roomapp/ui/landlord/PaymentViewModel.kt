package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.CreatePaymentRequest
import com.kma.lamphoun.roomapp.data.remote.dto.InvoiceResponse
import com.kma.lamphoun.roomapp.data.remote.dto.PaymentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    object Success : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _saveState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val saveState: StateFlow<PaymentUiState> = _saveState

    private val _payments = MutableStateFlow<List<PaymentResponse>>(emptyList())
    val payments: StateFlow<List<PaymentResponse>> = _payments

    private val _invoice = MutableStateFlow<InvoiceResponse?>(null)
    val invoice: StateFlow<InvoiceResponse?> = _invoice

    fun loadInvoiceAndPayments(invoiceId: Long) {
        viewModelScope.launch {
            try {
                val invoiceResp = api.getInvoiceById(invoiceId)
                if (invoiceResp.isSuccessful && invoiceResp.body()?.success == true) {
                    _invoice.value = invoiceResp.body()!!.data
                }
                val paymentsResp = api.getPaymentsByInvoice(invoiceId)
                if (paymentsResp.isSuccessful && paymentsResp.body()?.success == true) {
                    _payments.value = paymentsResp.body()!!.data!!.content
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun createPayment(invoiceId: Long, amount: Double, method: String, note: String) {
        viewModelScope.launch {
            _saveState.value = PaymentUiState.Loading
            try {
                val response = api.createPayment(
                    CreatePaymentRequest(
                        invoiceId = invoiceId,
                        amount = amount,
                        paymentMethod = method,
                        paidAt = LocalDate.now().toString(),
                        note = note.ifBlank { null }
                    )
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _saveState.value = PaymentUiState.Success
                    loadInvoiceAndPayments(invoiceId)
                } else {
                    _saveState.value = PaymentUiState.Error(response.body()?.message ?: "Thanh toán thất bại")
                }
            } catch (e: Exception) {
                _saveState.value = PaymentUiState.Error("Lỗi kết nối")
            }
        }
    }

    fun resetState() { _saveState.value = PaymentUiState.Idle }
}

