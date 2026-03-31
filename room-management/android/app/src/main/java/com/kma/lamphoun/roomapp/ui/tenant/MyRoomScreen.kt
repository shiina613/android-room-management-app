package com.kma.lamphoun.roomapp.ui.tenant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun MyRoomScreen(onNavigateBack: () -> Unit) {
    val contract = MockData.contracts.firstOrNull { it.status == "ACTIVE" }
    val room = contract?.let { MockData.rooms.find { r -> r.id == it.roomId } } ?: MockData.rooms.first()
    val meterHistory = MockData.meterReadings.filter { it.roomId == room.id }

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Phòng của tôi", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Room image
            // TODO: Replace with AsyncImage when room photo URL is available from API
            RoomImagePlaceholder(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)))

            // Room info
            SectionCard(title = room.title) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(room.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatusChip(room.status)
                    Text(room.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (!room.description.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(room.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Pricing
            SectionCard(title = "Bảng giá") {
                InfoRow("Tiền thuê/tháng", room.price.toVnd())
                Spacer(Modifier.height(6.dp))
                InfoRow("Giá điện", "${room.elecPrice.toInt()} ₫/kWh")
                Spacer(Modifier.height(6.dp))
                InfoRow("Giá nước", "${room.waterPrice.toInt()} ₫/m³")
                Spacer(Modifier.height(6.dp))
                InfoRow("Phí dịch vụ", room.servicePrice.toVnd())
            }

            // Meter reading history
            if (meterHistory.isNotEmpty()) {
                SectionCard(title = "Chỉ số điện nước gần đây") {
                    meterHistory.take(3).forEach { mr ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text(mr.billingMonth, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Primary)
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("⚡ ${mr.electricUsage.toInt()} kWh → ${mr.electricAmount.toVnd()}", style = MaterialTheme.typography.bodySmall)
                                Text("💧 ${mr.waterUsage.toInt()} m³ → ${mr.waterAmount.toVnd()}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        if (mr != meterHistory.take(3).last()) HorizontalDivider(color = OutlineVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
