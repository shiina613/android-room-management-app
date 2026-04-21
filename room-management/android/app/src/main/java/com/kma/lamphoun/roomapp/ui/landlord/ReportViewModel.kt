package com.kma.lamphoun.roomapp.ui.landlord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.lamphoun.roomapp.data.remote.api.ApiService
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import com.kma.lamphoun.roomapp.data.remote.dto.DebtReportResponse
import com.kma.lamphoun.roomapp.data.remote.dto.RevenueReportResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class ReportUiState {
    object Loading : ReportUiState()
    data class Success(
        val dashboard: DashboardResponse,
        val yearlyRevenue: RevenueReportResponse?,
        val debtReport: DebtReportResponse?
    ) : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState

    init { loadReports() }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                val dashboardResp = api.getDashboard()
                if (!dashboardResp.isSuccessful || dashboardResp.body()?.success != true) {
                    _uiState.value = ReportUiState.Error("Không tải được báo cáo")
                    return@launch
                }
                val dashboard = dashboardResp.body()!!.data!!

                val year = LocalDate.now().year
                val yearlyResp = api.getYearlyRevenue(year)
                val yearlyRevenue = if (yearlyResp.isSuccessful) yearlyResp.body()?.data else null

                val debtResp = api.getDebtReport()
                val debtReport = if (debtResp.isSuccessful) debtResp.body()?.data else null

                _uiState.value = ReportUiState.Success(dashboard, yearlyRevenue, debtReport)
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error("Lỗi kết nối")
            }
        }
    }
}

