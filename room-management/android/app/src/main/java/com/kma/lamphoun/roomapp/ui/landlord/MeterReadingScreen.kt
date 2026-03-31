package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun MeterReadingScreen(roomId: Long, onNavigateBack: () -> Unit, onSaved: () -> Unit) {
    val room = MockData.rooms.find { it.id == roomId } ?: MockData.rooms.first()
    val history = MockData.meterReadings.filter { it.roomId == roomId }
    val latest = history.firstOrNull()

    var billingMonth by remember { mutableStateOf("2026-04") }
    var elecPrev by remember { mutableStateOf(latest?.electricCurrent?.toLong()?.toString() ?: "0") }
    var elecCurr by remember { mutableStateOf("") }
    var waterPrev by remember { mutableStateOf(latest?.waterCurrent?.toLong()?.toString() ?: "0") }
    var waterCurr by remember { mutableStateOf("") }

    val elecUsage = (elecCurr.toLongOrNull() ?: 0) - (elecPrev.toLongOrNull() ?: 0)
    val waterUsage = (waterCurr.toLongOrNull() ?: 0) - (waterPrev.toLongOrNull() ?: 0)
    val elecAmount = elecUsage * room.elecPrice.toLong()
    val waterAmount = waterUsage * room.waterPrice.toLong()

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Nhập chỉ số điện nước", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Room info
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(room.title, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(room.address, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            // Billing month
            SectionCard(title = "") {
                OutlinedTextField(
                    value = billingMonth, onValueChange = { billingMonth = it },
                    label = { Text("Kỳ tính (YYYY-MM)") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Primary) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                )
            }

            // Electric
            SectionCard(title = "⚡ Điện") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = elecPrev, onValueChange = { elecPrev = it },
                        label = { Text("Chỉ số đầu (kWh)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                    OutlinedTextField(
                        value = elecCurr, onValueChange = { elecCurr = it },
                        label = { Text("Chỉ số cuối (kWh)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                }
                if (elecCurr.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tiêu thụ: ${elecUsage} kWh", style = MaterialTheme.typography.bodySmall, color = if (elecUsage >= 0) StatusAvailable else MaterialTheme.colorScheme.error)
                        Text("Thành tiền: ${elecAmount.toVnd()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Primary)
                    }
                }
            }

            // Water
            SectionCard(title = "💧 Nước") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = waterPrev, onValueChange = { waterPrev = it },
                        label = { Text("Chỉ số đầu (m³)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                    OutlinedTextField(
                        value = waterCurr, onValueChange = { waterCurr = it },
                        label = { Text("Chỉ số cuối (m³)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                }
                if (waterCurr.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tiêu thụ: ${waterUsage} m³", style = MaterialTheme.typography.bodySmall, color = if (waterUsage >= 0) StatusAvailable else MaterialTheme.colorScheme.error)
                        Text("Thành tiền: ${waterAmount.toVnd()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Primary)
                    }
                }
            }

            // History
            if (history.isNotEmpty()) {
                SectionCard(title = "Lịch sử gần đây") {
                    history.take(3).forEach { mr ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(mr.billingMonth, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("⚡${mr.electricUsage.toInt()} kWh  💧${mr.waterUsage.toInt()} m³", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (mr != history.take(3).last()) HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            Button(
                onClick = onSaved,
                enabled = elecCurr.isNotBlank() && waterCurr.isNotBlank() && elecUsage >= 0 && waterUsage >= 0,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Lưu chỉ số", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
