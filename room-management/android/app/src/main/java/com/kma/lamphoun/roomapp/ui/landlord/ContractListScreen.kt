package com.kma.lamphoun.roomapp.ui.landlord

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.data.remote.dto.ContractResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun ContractListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val contracts = MockData.contracts
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Hiệu lực", "Hết hạn", "Đã chấm dứt")

    val filtered = contracts.filter {
        when (selectedFilter) {
            "Hiệu lực" -> it.status == "ACTIVE"
            "Hết hạn" -> it.status == "EXPIRED"
            "Đã chấm dứt" -> it.status == "TERMINATED"
            else -> true
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Hợp đồng", onBack = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate, containerColor = Primary, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Tạo hợp đồng")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                item { EmptyState("Không có hợp đồng nào") }
            } else {
                items(filtered) { contract ->
                    ContractCard(contract = contract, onClick = { onNavigateToDetail(contract.id) })
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ContractCard(contract: ContractResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(contract.roomTitle ?: "Phòng #${contract.roomId}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                StatusChip(contract.status)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(contract.tenantName ?: "Người thuê #${contract.tenantId}", style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = OutlineVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Bắt đầu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(contract.startDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tiền thuê", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(contract.monthlyRent.toVnd(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Kết thúc", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(contract.endDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
