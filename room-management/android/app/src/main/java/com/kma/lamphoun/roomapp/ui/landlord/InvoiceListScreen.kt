package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun InvoiceListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.listState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Chưa TT", "Đã TT", "Quá hạn")

    val invoices = when (val s = uiState) {
        is InvoiceListUiState.Success -> s.invoices
        else -> emptyList()
    }

    val filtered = invoices.filter {
        when (selectedFilter) {
            "Chưa TT" -> it.status == "UNPAID"
            "Đã TT" -> it.status == "PAID"
            "Quá hạn" -> it.status == "OVERDUE"
            else -> true
        }
    }

    val totalUnpaid = invoices.filter { it.status == "UNPAID" }.sumOf { it.totalAmount }
    val totalPaid = invoices.filter { it.status == "PAID" }.sumOf { it.totalAmount }

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Hóa đơn", onBack = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 4.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Tạo hóa đơn") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Unpaid summary
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TertiaryContainer.copy(alpha = 0.3f))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("Chưa thanh toán", style = MaterialTheme.typography.labelSmall, color = Tertiary)
                            Text(totalUnpaid.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Tertiary)
                            Text("${invoices.count { it.status == "UNPAID" }} hóa đơn", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    // Paid summary
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SecondaryContainer.copy(alpha = 0.4f))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("Đã thanh toán", style = MaterialTheme.typography.labelSmall, color = StatusPaid)
                            Text(totalPaid.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusPaid)
                            Text("${invoices.count { it.status == "PAID" }} hóa đơn", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.forEach { f ->
                        FilterChip(
                            selected = selectedFilter == f,
                            onClick = { selectedFilter = f },
                            label = { Text(f, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SecondaryContainer,
                                selectedLabelColor = Primary
                            )
                        )
                    }
                }
            }

            when (val s = uiState) {
                is InvoiceListUiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is InvoiceListUiState.Error -> item { EmptyState(s.message) }
                is InvoiceListUiState.Success -> {
                    if (filtered.isEmpty()) {
                        item { EmptyState("Không có hóa đơn nào") }
                    } else {
                        items(filtered) { invoice ->
                            InvoiceCard(invoice = invoice, onClick = { onNavigateToDetail(invoice.id) })
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun InvoiceCard(invoice: InvoiceResponse, onClick: () -> Unit) {
    val statusColor = when (invoice.status) {
        "PAID" -> StatusPaid
        "OVERDUE" -> StatusOverdue
        else -> Tertiary
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (invoice.status == "PAID") Icons.Default.CheckCircle else Icons.Default.Receipt,
                    null, tint = statusColor, modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Tháng ${invoice.billingMonth}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OnBackground)
                Text("Hợp đồng #${invoice.contractId}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                if (invoice.dueDate != null) {
                    Text("Hạn: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(invoice.totalAmount.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(Modifier.height(4.dp))
                StatusChip(invoice.status)
            }
        }
    }
}

