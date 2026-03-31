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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.data.remote.dto.InvoiceResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun InvoiceListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val invoices = MockData.invoices
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Chưa TT", "Đã TT", "Quá hạn")

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
        containerColor = Background,
        topBar = { AppTopBar(title = "Hóa đơn", onBack = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate, containerColor = Primary, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Tạo hóa đơn")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary bento
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusUnpaid.copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Chưa thanh toán", style = MaterialTheme.typography.labelSmall, color = StatusUnpaid)
                            Text(totalUnpaid.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusUnpaid)
                            Text("${invoices.count { it.status == "UNPAID" }} hóa đơn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusPaid.copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Đã thanh toán", style = MaterialTheme.typography.labelSmall, color = StatusPaid)
                            Text(totalPaid.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusPaid)
                            Text("${invoices.count { it.status == "PAID" }} hóa đơn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Filter chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.forEach { f ->
                        FilterChip(
                            selected = selectedFilter == f,
                            onClick = { selectedFilter = f },
                            label = { Text(f, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer, selectedLabelColor = Primary)
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item { EmptyState("Không có hóa đơn nào") }
            } else {
                items(filtered) { invoice ->
                    InvoiceCard(invoice = invoice, onClick = { onNavigateToDetail(invoice.id) })
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun InvoiceCard(invoice: InvoiceResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(
                    when (invoice.status) {
                        "PAID" -> StatusPaid.copy(alpha = 0.12f)
                        "OVERDUE" -> StatusOverdue.copy(alpha = 0.12f)
                        else -> StatusUnpaid.copy(alpha = 0.12f)
                    }
                ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (invoice.status == "PAID") Icons.Default.CheckCircle else Icons.Default.Receipt,
                    null,
                    tint = when (invoice.status) { "PAID" -> StatusPaid; "OVERDUE" -> StatusOverdue; else -> StatusUnpaid },
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Hóa đơn tháng ${invoice.billingMonth}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("Hợp đồng #${invoice.contractId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (invoice.dueDate != null) Text("Hạn: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(invoice.totalAmount.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(Modifier.height(4.dp))
                StatusChip(invoice.status)
            }
        }
    }
}
