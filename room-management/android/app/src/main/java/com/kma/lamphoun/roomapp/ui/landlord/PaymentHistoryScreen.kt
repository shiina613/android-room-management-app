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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun PaymentHistoryScreen(
    invoiceId: Long? = null,
    onNavigateBack: () -> Unit,
    onCreatePayment: ((Long) -> Unit)? = null,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val payments by viewModel.payments.collectAsState()
    val invoice by viewModel.invoice.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(invoiceId) {
        if (invoiceId != null) viewModel.loadInvoiceAndPayments(invoiceId)
    }
    LaunchedEffect(saveState) {
        if (saveState is PaymentUiState.Success) {
            viewModel.resetState()
            if (invoiceId != null) onCreatePayment?.invoke(invoiceId)
        }
    }

    var amount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("CASH") }
    var note by remember { mutableStateOf("") }
    val methods = listOf("CASH" to "Tiền mặt", "BANK_TRANSFER" to "Chuyển khoản", "MOMO" to "MoMo")

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = if (invoiceId != null) "Thanh toán hóa đơn" else "Lịch sử thanh toán", onBack = onNavigateBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (invoice != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Hóa đơn tháng ${invoice!!.billingMonth}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            Text(invoice!!.totalAmount.toVnd(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            val paid = payments.sumOf { it.amount }
                            val remaining = invoice!!.totalAmount - paid
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Đã TT: ${paid.toVnd()}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                                Text("Còn: ${remaining.toVnd()}", style = MaterialTheme.typography.bodySmall, color = if (remaining > 0) Color(0xFFFFD54F) else Color.White)
                            }
                        }
                    }
                }

                if (invoice!!.status != "PAID") {
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
                            Text("Phương thức", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                methods.forEach { (value, label) ->
                                    FilterChip(
                                        selected = selectedMethod == value,
                                        onClick = { selectedMethod = value },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SecondaryContainer, selectedLabelColor = Primary)
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
                            if (saveState is PaymentUiState.Error) {
                                Spacer(Modifier.height(8.dp))
                                Text((saveState as PaymentUiState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (invoiceId != null) {
                                        viewModel.createPayment(invoiceId, amount.toDoubleOrNull() ?: 0.0, selectedMethod, note)
                                    }
                                },
                                enabled = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0 && saveState !is PaymentUiState.Loading,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                if (saveState is PaymentUiState.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                else Text("Xác nhận thanh toán", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            item { Text("Lịch sử", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }

            if (payments.isEmpty()) {
                item { EmptyState("Chưa có thanh toán nào") }
            } else {
                items(payments) { p ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(StatusPaid.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CheckCircle, null, tint = StatusPaid, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(methods.find { it.first == p.paymentMethod }?.second ?: p.paymentMethod, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(p.paidAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

