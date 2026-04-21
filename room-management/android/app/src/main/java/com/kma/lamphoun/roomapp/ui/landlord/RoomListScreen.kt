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
import androidx.compose.material.icons.outlined.*
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
import com.kma.lamphoun.roomapp.data.remote.dto.RoomResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*
@Composable
fun RoomListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToMeterReading: ((Long) -> Unit)? = null,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Trống", "Đang thuê", "Bảo trì")

    val rooms = when (val s = uiState) {
        is RoomListUiState.Success -> s.rooms
        else -> emptyList()
    }

    val filtered = rooms.filter { room ->
        val matchSearch = searchQuery.isBlank() || room.title.contains(searchQuery, true) || room.address.contains(searchQuery, true)
        val matchFilter = when (selectedFilter) {
            "Trống" -> room.status == "AVAILABLE"
            "Đang thuê" -> room.status == "OCCUPIED"
            "Bảo trì" -> room.status == "MAINTENANCE"
            else -> true
        }
        matchSearch && matchFilter
    }

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Danh sách phòng", onBack = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 4.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Thêm phòng") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm phòng...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedContainerColor = SurfaceContainerLowest
                    )
                )
            }

            // Stats mini row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple("Tổng", rooms.size.toString(), Primary),
                        Triple("Trống", rooms.count { it.status == "AVAILABLE" }.toString(), StatusAvailable),
                        Triple("Thuê", rooms.count { it.status == "OCCUPIED" }.toString(), Tertiary),
                        Triple("Bảo trì", rooms.count { it.status == "MAINTENANCE" }.toString(), StatusMaintenance)
                    ).forEach { (label, count, color) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
                                Text(label, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
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
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SecondaryContainer,
                                selectedLabelColor = Primary
                            )
                        )
                    }
                }
            }

            when (val s = uiState) {
                is RoomListUiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is RoomListUiState.Error -> item { EmptyState(s.message) }
                is RoomListUiState.Success -> {
                    if (filtered.isEmpty()) {
                        item { EmptyState("Không tìm thấy phòng nào") }
                    } else {
                        items(filtered) { room ->
                            RoomCard(
                                room = room,
                                onClick = { onNavigateToDetail(room.id) },
                                onMeterReading = if (room.status == "OCCUPIED") {
                                    { onNavigateToMeterReading?.invoke(room.id) }
                                } else null
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun RoomCard(room: RoomResponse, onClick: () -> Unit, onMeterReading: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Image with gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                RoomImagePlaceholder(
                    imageUrl = room.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                            )
                        )
                )
                // Status chip overlay
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
                    StatusChip(room.status)
                }
            }

            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(room.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnBackground)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(room.address, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, maxLines = 1)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(room.price.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) { Text(room.category, fontSize = 11.sp, color = OnSurfaceVariant) }
                        // Meter reading shortcut for occupied rooms
                        if (onMeterReading != null) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryContainer)
                                    .clickable { onMeterReading() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ElectricBolt, "Chỉ số điện nước", tint = Primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

