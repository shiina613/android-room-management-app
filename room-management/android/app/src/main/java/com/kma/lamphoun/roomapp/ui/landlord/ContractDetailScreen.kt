package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.InvoiceResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun ContractDetailScreen(
    contractId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToInvoice: ((invoiceId: Long) -> Unit)? = null,
    viewModel: ContractViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    val contractInvoices by viewModel.contractInvoices.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var showExtendDialog by remember { mutableStateOf(false) }
    var showTerminateDialog by remember { mutableStateOf(false) }
    var newEndDate by remember { mutableStateOf("") }
    var terminateNote by remember { mutableStateOf("") }

    LaunchedEffect(contractId) {
        viewModel.loadContract(contractId)
        viewModel.loadContractInvoices(contractId)
    }

    // Handle action success: reload contract and reset state
    LaunchedEffect(actionState) {
        if (actionState is ContractActionState.Success) {
            viewModel.resetActionState()
        }
    }

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Chi tiết hợp đồng", onBack = onNavigateBack) }
    ) { padding ->
        when (val s = detailState) {
            is ContractDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            is ContractDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is ContractDetailUiState.Success -> {
                val contract = s.contract

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ── Header Card ──────────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(GradientStart, GradientEnd)
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = contract.roomTitle ?: "Phòng #${contract.roomId}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                StatusChip(contract.status)
                                Text(
                                    text = contract.tenantName ?: "Người thuê #${contract.tenantId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // ── SectionCard: Thông tin hợp đồng ─────────────────────
                    SectionCard(title = "Thông tin hợp đồng") {
                        InfoRow("Ngày bắt đầu", contract.startDate)
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Ngày kết thúc", contract.endDate)
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Tiền thuê/tháng", contract.monthlyRent.toVnd())
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Tiền cọc", contract.deposit.toVnd())
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Chủ trọ", contract.landlordName ?: "—")
                    }

                    // ── SectionCard: Hóa đơn gần đây ────────────────────────
                    SectionCard(title = "Hóa đơn gần đây") {
                        val recentInvoices = contractInvoices.take(5)
                        if (recentInvoices.isEmpty()) {
                            Text(
                                "Chưa có hóa đơn nào",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        } else {
                            recentInvoices.forEachIndexed { index, invoice ->
                                MiniInvoiceRow(
                                    invoice = invoice,
                                    onClick = if (onNavigateToInvoice != null) {
                                        { onNavigateToInvoice(invoice.id) }
                                    } else null
                                )
                                if (index < recentInvoices.lastIndex) {
                                    HorizontalDivider(
                                        color = OutlineVariant,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ── Action buttons (ACTIVE only) ─────────────────────────
                    if (contract.status == "ACTIVE") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    newEndDate = ""
                                    showExtendDialog = true
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(Primary, Primary))
                                )
                            ) {
                                Text("Gia hạn", fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    terminateNote = ""
                                    showTerminateDialog = true
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Chấm dứt", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // ── Error from actionState ───────────────────────────────
                    if (actionState is ContractActionState.Error) {
                        val errorMsg = (actionState as ContractActionState.Error).message
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = errorMsg,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        LaunchedEffect(errorMsg) {
                            // Reset after showing error so it doesn't persist across recompositions
                            // User can dismiss by performing another action
                        }
                    }

                    // Loading indicator for actions
                    if (actionState is ContractActionState.Loading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary, modifier = Modifier.size(32.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    // ── Dialog: Gia hạn hợp đồng ────────────────────────────────────────────
    if (showExtendDialog) {
        AlertDialog(
            onDismissRequest = { showExtendDialog = false },
            title = { Text("Gia hạn hợp đồng", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Nhập ngày kết thúc mới cho hợp đồng.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newEndDate,
                        onValueChange = { newEndDate = it },
                        label = { Text("Ngày kết thúc mới (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExtendDialog = false
                        viewModel.extendContract(contractId, newEndDate)
                    },
                    enabled = newEndDate.isNotBlank()
                ) {
                    Text("Xác nhận", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExtendDialog = false }) {
                    Text("Hủy")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ── Dialog: Chấm dứt hợp đồng ───────────────────────────────────────────
    if (showTerminateDialog) {
        AlertDialog(
            onDismissRequest = { showTerminateDialog = false },
            title = { Text("Chấm dứt hợp đồng", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "⚠️ Hành động này sẽ chấm dứt hợp đồng ngay lập tức và không thể hoàn tác. Trạng thái hợp đồng sẽ chuyển sang \"Đã chấm dứt\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedTextField(
                        value = terminateNote,
                        onValueChange = { terminateNote = it },
                        label = { Text("Lý do (tùy chọn)") },
                        singleLine = false,
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTerminateDialog = false
                        viewModel.terminateContract(contractId, terminateNote)
                    }
                ) {
                    Text(
                        "Xác nhận",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTerminateDialog = false }) {
                    Text("Hủy")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun MiniInvoiceRow(
    invoice: InvoiceResponse,
    onClick: (() -> Unit)? = null
) {
    val statusColor = when (invoice.status.uppercase()) {
        "PAID" -> StatusPaid
        "OVERDUE" -> StatusOverdue
        else -> StatusUnpaid
    }
    val statusLabel = when (invoice.status.uppercase()) {
        "PAID" -> "Đã thanh toán"
        "OVERDUE" -> "Quá hạn"
        else -> "Chưa thanh toán"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = "Tháng ${invoice.billingMonth}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface
                )
                Text(
                    text = invoice.totalAmount.toVnd(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(statusColor.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = statusLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }
    }
}
