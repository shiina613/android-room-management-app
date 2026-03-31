package com.kma.lamphoun.roomapp.ui.tenant

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
fun TenantInvoiceDetailScreen(invoiceId: Long, onNavigateBack: () -> Unit) {
    val invoice = MockData.invoices.find { it.id == invoiceId } ?: MockData.invoices.first()
    val payments = MockData.payments.filter { it.invoiceId == invoice.id }
    val totalPaid = payments.sumOf { it.amount }
    val remaining = invoice.totalAmount - totalPaid

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Chi tiết hóa đơn", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (invoice.status == "PAID") StatusPaid else Primary)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (invoice.status == "PAID") Icons.Default.CheckCircle else Icons.Default.Receipt,
                        null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Hóa đơn tháng ${invoice.billingMonth}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    Text(invoice.totalAmount.toVnd(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    StatusChip(invoice.status)
                    if (invoice.dueDate != null && invoice.status != "PAID") {
                        Spacer(Modifier.height(6.dp))
                        Text("Hạn: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                    if (invoice.paidAt != null) {
                        Spacer(Modifier.height(6.dp))
                        Text("Đã thanh toán: ${invoice.paidAt}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            // Breakdown
            invoice.breakdown?.let { b ->
                SectionCard(title = "Chi tiết các khoản") {
                    BreakdownRow("Tiền thuê phòng", b.rentAmount)
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    BreakdownRow("Tiền điện (${b.electricUsage.toInt()} kWh × ${MockData.rooms.first().elecPrice.toInt()}₫)", b.electricAmount)
                    BreakdownRow("Tiền nước (${b.waterUsage.toInt()} m³ × ${MockData.rooms.first().waterPrice.toInt()}₫)", b.waterAmount)
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    BreakdownRow("Phí dịch vụ", b.serviceAmount)
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng cộng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(invoice.totalAmount.toVnd(), fontWeight = FontWeight.Bold, color = Primary, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }

            // Payment progress
            if (invoice.status != "PAID") {
                SectionCard(title = "Tình trạng thanh toán") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Đã thanh toán", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(totalPaid.toVnd(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = StatusPaid)
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (totalPaid / invoice.totalAmount).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = StatusPaid, trackColor = SurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Còn lại", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(remaining.toVnd(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = StatusUnpaid)
                    }
                }
            }

            // Info note for tenant
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, null, tint = Secondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Vui lòng thanh toán đúng hạn. Liên hệ chủ trọ nếu có thắc mắc.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BreakdownRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(amount.toVnd(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
