package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import com.kma.lamphoun.roomapp.data.remote.dto.DebtReportResponse
import com.kma.lamphoun.roomapp.data.remote.dto.RevenueReportResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Báo cáo", onBack = onNavigateBack) }
    ) { padding ->
        when (val s = uiState) {
            is ReportUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            is ReportUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.message) }
            is ReportUiState.Success -> ReportContent(
                modifier = Modifier.padding(padding),
                dashboard = s.dashboard,
                yearlyRevenue = s.yearlyRevenue,
                debtReport = s.debtReport
            )
        }
    }
}

@Composable
private fun ReportContent(
    modifier: Modifier = Modifier,
    dashboard: DashboardResponse,
    yearlyRevenue: RevenueReportResponse?,
    debtReport: DebtReportResponse?
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(modifier = Modifier.weight(1f), label = "Doanh thu tháng này", value = dashboard.revenueThisMonth.toVnd(), icon = "💰", color = Primary)
            SummaryCard(modifier = Modifier.weight(1f), label = "Công nợ", value = dashboard.debtThisMonth.toVnd(), icon = "⚠️", color = StatusUnpaid)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(modifier = Modifier.weight(1f), label = "Tỷ lệ lấp đầy", value = "${dashboard.occupancyRate}%", icon = "📊", color = StatusAvailable)
            SummaryCard(modifier = Modifier.weight(1f), label = "Tổng người thuê", value = "${dashboard.totalTenants} người", icon = "👥", color = Secondary)
        }

        // Monthly revenue bar chart
        if (yearlyRevenue?.monthly != null) {
            val monthly = yearlyRevenue.monthly
            val maxRevenue = monthly.maxOfOrNull { it.invoiced }?.toFloat() ?: 1f
            SectionCard(title = "Doanh thu 12 tháng (${yearlyRevenue.year})") {
                Row(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    monthly.forEach { m ->
                        val heightFraction = if (maxRevenue > 0) (m.invoiced / maxRevenue).toFloat() else 0f
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(heightFraction.coerceAtLeast(0.02f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (m.invoiced > 0) Primary else SurfaceVariant))
                            Spacer(Modifier.height(4.dp))
                            Text(m.month.takeLast(2), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Tổng năm: ${yearlyRevenue.totalInvoiced.toVnd()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Room status
        SectionCard(title = "Tình trạng phòng") {
            RoomStatusBar("Đang thuê", dashboard.occupiedRooms.toInt(), dashboard.totalRooms.toInt(), StatusOccupied)
            Spacer(Modifier.height(8.dp))
            RoomStatusBar("Phòng trống", dashboard.availableRooms.toInt(), dashboard.totalRooms.toInt(), StatusAvailable)
            Spacer(Modifier.height(8.dp))
            RoomStatusBar("Bảo trì", dashboard.maintenanceRooms.toInt(), dashboard.totalRooms.toInt(), StatusMaintenance)
        }

        // Debt report
        if (debtReport != null && debtReport.details.isNotEmpty()) {
            SectionCard(title = "Công nợ (${debtReport.debtorCount} người thuê)") {
                debtReport.details.take(5).forEach { detail ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(detail.roomTitle ?: "—", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(detail.tenantName ?: "—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(detail.remaining.toVnd(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = StatusUnpaid)
                    }
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                }
                if (debtReport.details.size > 5) {
                    Text("... và ${debtReport.details.size - 5} khoản khác", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SummaryCard(modifier: Modifier = Modifier, label: String, value: String, icon: String, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RoomStatusBar(label: String, count: Int, total: Int, color: Color) {
    val fraction = if (total > 0) count.toFloat() / total else 0f
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(80.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color, trackColor = SurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text("$count/$total", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, modifier = Modifier.width(36.dp))
    }
}

