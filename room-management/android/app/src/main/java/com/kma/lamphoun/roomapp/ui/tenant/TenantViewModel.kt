package com.kma.lamphoun.roomapp.ui.tenant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.ContractResponse
import com.kma.lamphoun.roomapp.data.remote.dto.InvoiceResponse
import com.kma.lamphoun.roomapp.data.remote.dto.NotificationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TenantViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _activeContract = MutableStateFlow<ContractResponse?>(null)
    val activeContract: StateFlow<ContractResponse?> = _activeContract

    private val _latestInvoice = MutableStateFlow<InvoiceResponse?>(null)
    val latestInvoice: StateFlow<InvoiceResponse?> = _latestInvoice

    private val _invoices = MutableStateFlow<List<InvoiceResponse>>(emptyList())
    val invoices: StateFlow<List<InvoiceResponse>> = _invoices

    private val _isLoadingInvoices = MutableStateFlow(false)
    val isLoadingInvoices: StateFlow<Boolean> = _isLoadingInvoices

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    var fullName: String = ""
        private set

    init {
        viewModelScope.launch {
            fullName = tokenDataStore.fullName.firstOrNull() ?: ""
            loadAll()
        }
    }

    fun loadAll() {
        loadContract()
        loadInvoices()
        loadNotifications()
    }

    private fun loadContract() {
        viewModelScope.launch {
            try {
                val response = api.getMyContracts(status = "ACTIVE", size = 1)
                if (response.isSuccessful && response.body()?.success == true) {
                    _activeContract.value = response.body()!!.data!!.content.firstOrNull()
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun loadInvoices(status: String? = null) {
        viewModelScope.launch {
            _isLoadingInvoices.value = true
            try {
                val response = api.getMyTenantInvoices(status = status, page = 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()!!.data!!.content
                    _invoices.value = list
                    _latestInvoice.value = list.firstOrNull { it.status == "UNPAID" } ?: list.firstOrNull()
                }
            } catch (e: Exception) { /* ignore */ }
            finally { _isLoadingInvoices.value = false }
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val response = api.getNotifications(page = 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()!!.data!!.content
                    _notifications.value = list
                    _unreadCount.value = list.count { !it.read }
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                api.markAllRead()
                loadNotifications()
            } catch (e: Exception) { /* ignore */ }
        }
    }
}

