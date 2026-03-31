package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun ReportScreen(onNavigateBack: () -> Unit) {
    val dashboard = MockData.dashboard

    // Mock monthly revenue data
    val monthlyRevenue = listOf(
        "T1" to 28_000_000L, "T2" to 29_500_000L, "T3" to 31_500_000L,
        "T4" to 30_000_000L, "T5" to 32_000_000L, "T6" to 31_000_000L,
        "T7" to 33_500_000L, "T8" to 34_000_000L, "T9" to 31_500_000L,
        "T10" to 30_500_000L, "T11" to 29_000_000L, "T12" to 0L
    )
    val maxRevenue = monthlyRevenue.maxOf { it.second }.toFloat()

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Báo cáo", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary cards
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(modifier = Modifier.weight(1f), label = "Doanh thu tháng này", value = dashboard.revenueThisMonth.toVnd(), icon = "💰", color = Primary)
                SummaryCard(modifier = Modifier.weight(1f), label = "Công nợ", value = dashboard.unpaidAmount.toVnd(), icon = "⚠️", color = StatusUnpaid)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(modifier = Modifier.weight(1f), label = "Tỷ lệ lấp đầy", value = "${(dashboard.occupiedRooms * 100 / dashboard.totalRooms)}%", icon = "📊", color = StatusAvailable)
                SummaryCard(modifier = Modifier.weight(1f), label = "Tổng người thuê", value = "${dashboard.totalTenants} người", icon = "👥", color = Secondary)
            }

            // Monthly revenue bar chart (manual)
            SectionCard(title = "Doanh thu 12 tháng (2026)") {
                Row(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    monthlyRevenue.forEach { (month, revenue) ->
                        val heightFraction = if (maxRevenue > 0) revenue / maxRevenue else 0f
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(heightFraction.coerceAtLeast(0.02f))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (revenue > 0) Primary else SurfaceVariant)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(month, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Tổng năm: ${monthlyRevenue.sumOf { it.second }.toVnd()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Room status
            SectionCard(title = "Tình trạng phòng") {
                RoomStatusBar("Đang thuê", dashboard.occupiedRooms, dashboard.totalRooms, StatusOccupied)
                Spacer(Modifier.height(8.dp))
                RoomStatusBar("Phòng trống", dashboard.availableRooms, dashboard.totalRooms, StatusAvailable)
                Spacer(Modifier.height(8.dp))
                RoomStatusBar("Bảo trì", dashboard.totalRooms - dashboard.occupiedRooms - dashboard.availableRooms, dashboard.totalRooms, StatusMaintenance)
            }

            // Debt report
            SectionCard(title = "Công nợ tháng 3/2026") {
                MockData.invoices.filter { it.status == "UNPAID" }.forEach { inv ->
                    val contract = MockData.contracts.find { it.id == inv.contractId }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(contract?.roomTitle ?: "Phòng #${inv.contractId}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(contract?.tenantName ?: "—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(inv.totalAmount.toVnd(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = StatusUnpaid)
                    }
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
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
