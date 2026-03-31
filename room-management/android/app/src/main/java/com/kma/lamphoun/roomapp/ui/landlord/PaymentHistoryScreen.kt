package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun PaymentHistoryScreen(
    invoiceId: Long? = null,
    onNavigateBack: () -> Unit,
    onCreatePayment: ((Long) -> Unit)? = null
) {
    val payments = if (invoiceId != null) MockData.payments.filter { it.invoiceId == invoiceId }
                   else MockData.payments
    val invoice = if (invoiceId != null) MockData.invoices.find { it.id == invoiceId } else null

    // Create payment form state (shown when invoiceId provided and invoice not PAID)
    var showForm by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf(invoice?.let { (it.totalAmount - payments.sumOf { p -> p.amount }).toLong().toString() } ?: "") }
    var selectedMethod by remember { mutableStateOf("CASH") }
    var note by remember { mutableStateOf("") }
    val methods = listOf("CASH" to "Tiền mặt", "BANK_TRANSFER" to "Chuyển khoản", "MOMO" to "MoMo", "ZALOPAY" to "ZaloPay")

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = if (invoiceId != null) "Thanh toán hóa đơn" else "Lịch sử thanh toán", onBack = onNavigateBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Invoice summary if context
            if (invoice != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Hóa đơn tháng ${invoice.billingMonth}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            Text(invoice.totalAmount.toVnd(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            val paid = payments.sumOf { it.amount }
                            val remaining = invoice.totalAmount - paid
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Đã TT: ${paid.toVnd()}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                                Text("Còn: ${remaining.toVnd()}", style = MaterialTheme.typography.bodySmall, color = if (remaining > 0) Color(0xFFFFD54F) else Color.White)
                            }
                        }
                    }
                }

                // Payment form
                if (invoice.status != "PAID") {
                    item {
                        SectionCard(title = "Ghi nhận thanh toán") {
                            OutlinedTextField(
                                value = amount, onValueChange = { amount = it },
                                label = { Text("Số tiền (₫) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("Phương thức thanh toán", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                methods.forEach { (value, label) ->
                                    FilterChip(
                                        selected = selectedMethod == value,
                                        onClick = { selectedMethod = value },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer, selectedLabelColor = Primary)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = note, onValueChange = { note = it },
                                label = { Text("Ghi chú") },
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { invoiceId?.let { onCreatePayment?.invoke(it) } },
                                enabled = amount.isNotBlank() && (amount.toLongOrNull() ?: 0) > 0,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) { Text("Xác nhận thanh toán", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }

            // Payment list
            item { Text("Lịch sử", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }

            if (payments.isEmpty()) {
                item { EmptyState("Chưa có thanh toán nào") }
            } else {
                items(payments) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(StatusPaid.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.CheckCircle, null, tint = StatusPaid, modifier = Modifier.size(20.dp)) }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(methods.find { it.first == p.paymentMethod }?.second ?: p.paymentMethod, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(p.paidAt ?: "—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (!p.note.isNullOrBlank()) Text(p.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(p.amount.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusPaid)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
