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
fun InvoiceDetailScreen(
    invoiceId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: (Long) -> Unit
) {
    val invoice = MockData.invoices.find { it.id == invoiceId } ?: MockData.invoices.first()
    val contract = MockData.contracts.find { it.id == invoice.contractId }
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
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Primary)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hóa đơn tháng ${invoice.billingMonth}", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(8.dp))
                    Text(invoice.totalAmount.toVnd(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    StatusChip(invoice.status)
                    if (invoice.dueDate != null) {
                        Spacer(Modifier.height(8.dp))
                        Text("Hạn thanh toán: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            // Contract info
            if (contract != null) {
                SectionCard(title = "Thông tin hợp đồng") {
                    InfoRow("Phòng", contract.roomTitle ?: "—")
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Người thuê", contract.tenantName ?: "—")
                }
            }

            // Breakdown
            invoice.breakdown?.let { b ->
                SectionCard(title = "Chi tiết các khoản") {
                    BreakdownRow("Tiền thuê phòng", b.rentAmount)
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    BreakdownRow("Tiền điện (${b.electricUsage.toInt()} kWh)", b.electricAmount)
                    BreakdownRow("Tiền nước (${b.waterUsage.toInt()} m³)", b.waterAmount)
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    BreakdownRow("Phí dịch vụ", b.serviceAmount)
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng cộng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(invoice.totalAmount.toVnd(), fontWeight = FontWeight.Bold, color = Primary, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }

            // Payment status
            SectionCard(title = "Tình trạng thanh toán") {
                InfoRow("Đã thanh toán", totalPaid.toVnd())
                Spacer(Modifier.height(6.dp))
                InfoRow("Còn lại", remaining.toVnd())
                if (remaining > 0) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (totalPaid / invoice.totalAmount).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = Primary, trackColor = SurfaceVariant
                    )
                }
            }

            // Payment history
            if (payments.isNotEmpty()) {
                SectionCard(title = "Lịch sử thanh toán") {
                    payments.forEach { p ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(p.paymentMethod, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                Text(p.paidAt ?: "—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(p.amount.toVnd(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = StatusPaid)
                        }
                    }
                }
            }

            // Action button
            if (invoice.status != "PAID") {
                Button(
                    onClick = { onNavigateToPayment(invoice.id) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Payment, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ghi nhận thanh toán", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BreakdownRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(amount.toVnd(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
