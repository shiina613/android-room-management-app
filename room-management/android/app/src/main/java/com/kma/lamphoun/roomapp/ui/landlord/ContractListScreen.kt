package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kma.lamphoun.roomapp.data.remote.dto.ContractResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun ContractListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: ContractViewModel = hiltViewModel()
) {
    val uiState by viewModel.listState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Hiệu lực", "Hết hạn", "Đã chấm dứt")

    val contracts = when (val s = uiState) {
        is ContractListUiState.Success -> s.contracts
        else -> emptyList()
    }

    val filtered = contracts.filter {
        when (selectedFilter) {
            "Hiệu lực" -> it.status == "ACTIVE"
            "Hết hạn" -> it.status == "EXPIRED"
            "Đã chấm dứt" -> it.status == "TERMINATED"
            else -> true
        }
    }

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Hợp đồng", onBack = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 4.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Tạo hợp đồng") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Triple("Hiệu lực", contracts.count { it.status == "ACTIVE" }, StatusAvailable),
                        Triple("Hết hạn", contracts.count { it.status == "EXPIRED" }, Tertiary),
                        Triple("Chấm dứt", contracts.count { it.status == "TERMINATED" }, StatusOverdue)
                    ).forEach { (label, count, color) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$count", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
                                Text(label, fontSize = 11.sp, color = OnSurfaceVariant)
                            }
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
                is ContractListUiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is ContractListUiState.Error -> item { EmptyState(s.message) }
                is ContractListUiState.Success -> {
                    if (filtered.isEmpty()) {
                        item { EmptyState("Không có hợp đồng nào") }
                    } else {
                        items(filtered) { contract ->
                            ContractCard(contract = contract, onClick = { onNavigateToDetail(contract.id) })
                        }
                    }
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
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    contract.roomTitle ?: "Phòng #${contract.roomId}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(contract.status)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondaryContainer),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Person, null, tint = Primary, modifier = Modifier.size(16.dp)) }
                Spacer(Modifier.width(8.dp))
                Text(contract.tenantName ?: "Người thuê #${contract.tenantId}", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
            }
            // Date + price row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Bắt đầu", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text(contract.startDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = OnSurface)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tiền thuê", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text(contract.monthlyRent.toVnd(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Kết thúc", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text(contract.endDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = OnSurface)
                }
            }
        }
    }
}

