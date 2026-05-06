package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.ContractResponse
import com.kma.lamphoun.roomapp.data.remote.dto.CreateInvoiceRequest
import com.kma.lamphoun.roomapp.data.remote.dto.MeterReadingRequest
import com.kma.lamphoun.roomapp.data.remote.dto.MeterReadingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// State for meter reading check
sealed class MeterCheckState {
    object Idle : MeterCheckState()
    object Checking : MeterCheckState()
    data class Found(val reading: MeterReadingResponse) : MeterCheckState()
    object NotFound : MeterCheckState()
    data class Error(val message: String) : MeterCheckState()
}

// State for invoice form submission
sealed class InvoiceFormSubmitState {
    object Idle : InvoiceFormSubmitState()
    object Loading : InvoiceFormSubmitState()
    object Success : InvoiceFormSubmitState()
    data class Error(val message: String) : InvoiceFormSubmitState()
}

// Inline meter data when user enters readings manually (MeterCheckState.NotFound)
data class InlineMeterData(
    val electricPrevious: Double,
    val electricCurrent: Double,
    val waterPrevious: Double,
    val waterCurrent: Double
)

@HiltViewModel
class InvoiceFormViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _activeContracts = MutableStateFlow<List<ContractResponse>>(emptyList())
    val activeContracts: StateFlow<List<ContractResponse>> = _activeContracts

    private val _meterCheckState = MutableStateFlow<MeterCheckState>(MeterCheckState.Idle)
    val meterCheckState: StateFlow<MeterCheckState> = _meterCheckState

    private val _submitState = MutableStateFlow<InvoiceFormSubmitState>(InvoiceFormSubmitState.Idle)
    val submitState: StateFlow<InvoiceFormSubmitState> = _submitState

    private val _isLoadingContracts = MutableStateFlow(false)
    val isLoadingContracts: StateFlow<Boolean> = _isLoadingContracts

    private val _selectedContract = MutableStateFlow<ContractResponse?>(null)
    val selectedContract: StateFlow<ContractResponse?> = _selectedContract

    // Billing month được preload từ MeterReadingScreen (chỉ dùng khi navigate từ meter reading)
    private val _prefilledBillingMonth = MutableStateFlow<String?>(null)
    val prefilledBillingMonth: StateFlow<String?> = _prefilledBillingMonth

    // Chỉ số kỳ gần nhất của phòng — dùng để điền sẵn đầu kỳ khi NotFound
    private val _latestMeterReading = MutableStateFlow<MeterReadingResponse?>(null)
    val latestMeterReading: StateFlow<MeterReadingResponse?> = _latestMeterReading

    init {
        loadActiveContracts()
    }

    /**
     * Load all ACTIVE contracts from GET /api/contracts?status=ACTIVE
     * Requirements: 1.2
     */
    fun loadActiveContracts() {
        viewModelScope.launch {
            _isLoadingContracts.value = true
            try {
                val response = api.getContracts(status = "ACTIVE", size = 100)
                if (response.isSuccessful && response.body()?.success == true) {
                    _activeContracts.value = response.body()!!.data!!.content
                } else {
                    // Non-fatal: keep empty list, UI can show error if needed
                    _activeContracts.value = emptyList()
                }
            } catch (e: Exception) {
                _activeContracts.value = emptyList()
            } finally {
                _isLoadingContracts.value = false
            }
        }
    }

    /**
     * Set the currently selected contract.
     * Requirements: 1.3
     */
    fun selectContract(contract: ContractResponse) {
        _selectedContract.value = contract
        // Reset meter check state when contract changes
        _meterCheckState.value = MeterCheckState.Idle
        // Load latest meter reading để điền sẵn đầu kỳ khi NotFound
        loadLatestMeterReading(contract.roomId)
    }

    private fun loadLatestMeterReading(roomId: Long) {
        viewModelScope.launch {
            try {
                val response = api.getMeterReadingHistory(roomId, page = 0)
                if (response.isSuccessful && response.body()?.success == true) {
                    _latestMeterReading.value = response.body()!!.data!!.content.firstOrNull()
                }
            } catch (e: Exception) { /* ignore */ }
        }
    }

    /**
     * Check if a MeterReading already exists for the given room and billing month.
     * GET /api/meter-readings/rooms/{roomId}/month/{billingMonth}
     * Requirements: 1.5
     */
    fun checkMeterReading(roomId: Long, billingMonth: String) {
        viewModelScope.launch {
            _meterCheckState.value = MeterCheckState.Checking
            try {
                val response = api.getMeterReadingByMonth(roomId, billingMonth)
                if (response.isSuccessful && response.body()?.success == true) {
                    val reading = response.body()!!.data
                    if (reading != null) {
                        _meterCheckState.value = MeterCheckState.Found(reading)
                    } else {
                        _meterCheckState.value = MeterCheckState.NotFound
                    }
                } else {
                    // 404 or no data means not found
                    val code = response.code()
                    if (code == 404) {
                        _meterCheckState.value = MeterCheckState.NotFound
                    } else {
                        _meterCheckState.value = MeterCheckState.Error(
                            response.body()?.message ?: "Không kiểm tra được chỉ số"
                        )
                    }
                }
            } catch (e: Exception) {
                _meterCheckState.value = MeterCheckState.Error("Lỗi kết nối")
            }
        }
    }

    /**
     * Submit the invoice:
     * - If MeterCheckState.NotFound and inlineMeterData provided: POST /api/meter-readings first
     * - Then POST /api/invoices { contractId, meterReadingId, billingMonth }
     *
     * Requirements: 1.8, 1.10
     */
    fun submitInvoice(
        contractId: Long,
        billingMonth: String,
        meterReadingId: Long? = null,
        inlineMeterData: InlineMeterData? = null
    ) {
        viewModelScope.launch {
            _submitState.value = InvoiceFormSubmitState.Loading
            try {
                val resolvedMeterReadingId: Long

                if (meterReadingId != null) {
                    // MeterReading already exists (Found state)
                    resolvedMeterReadingId = meterReadingId
                } else if (inlineMeterData != null) {
                    // Need to save MeterReading first (NotFound state)
                    val contract = _selectedContract.value
                    val roomId = contract?.roomId
                        ?: run {
                            _submitState.value = InvoiceFormSubmitState.Error("Không tìm thấy thông tin phòng")
                            return@launch
                        }

                    val meterResponse = api.recordMeterReading(
                        MeterReadingRequest(
                            roomId = roomId,
                            billingMonth = billingMonth,
                            electricPrevious = inlineMeterData.electricPrevious,
                            electricCurrent = inlineMeterData.electricCurrent,
                            waterPrevious = inlineMeterData.waterPrevious,
                            waterCurrent = inlineMeterData.waterCurrent
                        )
                    )

                    if (meterResponse.isSuccessful && meterResponse.body()?.success == true) {
                        resolvedMeterReadingId = meterResponse.body()!!.data!!.id
                    } else {
                        _submitState.value = InvoiceFormSubmitState.Error(
                            meterResponse.body()?.message ?: "Lưu chỉ số điện nước thất bại"
                        )
                        return@launch
                    }
                } else {
                    _submitState.value = InvoiceFormSubmitState.Error("Thiếu thông tin chỉ số điện nước")
                    return@launch
                }

                // POST /api/invoices
                val invoiceResponse = api.createInvoice(
                    CreateInvoiceRequest(
                        contractId = contractId,
                        meterReadingId = resolvedMeterReadingId,
                        billingMonth = billingMonth
                    )
                )

                if (invoiceResponse.isSuccessful && invoiceResponse.body()?.success == true) {
                    _submitState.value = InvoiceFormSubmitState.Success
                } else {
                    _submitState.value = InvoiceFormSubmitState.Error(
                        invoiceResponse.body()?.message ?: "Tạo hóa đơn thất bại"
                    )
                }
            } catch (e: Exception) {
                _submitState.value = InvoiceFormSubmitState.Error("Lỗi kết nối")
            }
        }
    }

    /**
     * Pre-fill the form when opened from MeterReadingScreen.
     * Finds the ACTIVE contract for the given roomId from already-loaded activeContracts,
     * then loads the MeterReading directly by ID to set MeterCheckState.Found.
     *
     * Requirements: 3.8
     */
    fun preloadFromMeterReading(roomId: Long, meterReadingId: Long) {
        viewModelScope.launch {
            // Ensure contracts are loaded first
            if (_activeContracts.value.isEmpty()) {
                _isLoadingContracts.value = true
                try {
                    val response = api.getContracts(status = "ACTIVE", size = 100)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _activeContracts.value = response.body()!!.data!!.content
                    }
                } catch (e: Exception) {
                    // Continue even if contracts fail to load
                } finally {
                    _isLoadingContracts.value = false
                }
            }

            // Find the ACTIVE contract for this room
            val contract = _activeContracts.value.find { it.roomId == roomId }
            if (contract != null) {
                _selectedContract.value = contract
            }

            // Load the MeterReading directly by ID (not by searching history page)
            _meterCheckState.value = MeterCheckState.Checking
            try {
                val response = api.getMeterReadingById(meterReadingId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val reading = response.body()!!.data
                    if (reading != null) {
                        _prefilledBillingMonth.value = reading.billingMonth
                        _meterCheckState.value = MeterCheckState.Found(reading)
                    } else {
                        _meterCheckState.value = MeterCheckState.Error("Không tải được chỉ số điện nước")
                    }
                } else {
                    _meterCheckState.value = MeterCheckState.Error(
                        response.body()?.message ?: "Không tải được chỉ số điện nước"
                    )
                }
            } catch (e: Exception) {
                _meterCheckState.value = MeterCheckState.Error("Lỗi kết nối")
            }
        }
    }

    /**
     * Reset submit state back to Idle (e.g., after showing error to user).
     */
    fun resetSubmitState() {
        _submitState.value = InvoiceFormSubmitState.Idle
    }
}
