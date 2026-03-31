package com.kma.lamphoun.roomapp.ui.tenant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.landlord.InvoiceCard
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun TenantInvoiceListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val invoices = MockData.invoices
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Chưa TT", "Đã TT")

    val filtered = invoices.filter {
        when (selectedFilter) {
            "Chưa TT" -> it.status == "UNPAID"
            "Đã TT" -> it.status == "PAID"
            else -> true
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Hóa đơn của tôi", onBack = onNavigateBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val unpaidTotal = invoices.filter { it.status == "UNPAID" }.sumOf { it.totalAmount }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusUnpaid.copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Cần thanh toán", style = MaterialTheme.typography.labelSmall, color = StatusUnpaid)
                            Text(unpaidTotal.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusUnpaid)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Tổng hóa đơn", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${invoices.size} hóa đơn", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.forEach { f ->
                        FilterChip(selected = selectedFilter == f, onClick = { selectedFilter = f },
                            label = { Text(f, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer, selectedLabelColor = Primary))
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
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
