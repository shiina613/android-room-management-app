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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.data.remote.dto.RoomResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val rooms = MockData.rooms
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Trống", "Đang thuê", "Bảo trì")

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
        containerColor = Background,
        topBar = { AppTopBar(title = "Danh sách phòng", onBack = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate, containerColor = Primary, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Thêm phòng")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = OutlineVariant)
                )
            }

            // Stats mini bento
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple("Tổng", rooms.size.toString(), Primary),
                        Triple("Trống", rooms.count { it.status == "AVAILABLE" }.toString(), StatusAvailable),
                        Triple("Đang thuê", rooms.count { it.status == "OCCUPIED" }.toString(), StatusOccupied),
                        Triple("Bảo trì", rooms.count { it.status == "MAINTENANCE" }.toString(), StatusMaintenance)
                    ).forEach { (label, count, color) ->
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
                                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer, selectedLabelColor = Primary)
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item { EmptyState("Không tìm thấy phòng nào") }
            } else {
                items(filtered) { room ->
                    RoomCard(room = room, onClick = { onNavigateToDetail(room.id) })
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun RoomCard(room: RoomResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            // TODO: Replace with AsyncImage when room.imageUrl is available from API
            RoomImagePlaceholder(modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)))

            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(room.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    StatusChip(room.status)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(room.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(room.price.toVnd(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Primary)
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SurfaceVariant).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(room.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
