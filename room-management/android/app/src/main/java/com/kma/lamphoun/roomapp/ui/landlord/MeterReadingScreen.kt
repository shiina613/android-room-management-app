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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*
import java.time.YearMonth

@Composable
fun MeterReadingScreen(
    roomId: Long,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: MeterReadingViewModel = hiltViewModel()
) {
    val saveState by viewModel.saveState.collectAsState()
    val history by viewModel.history.collectAsState()
    val latestReading by viewModel.latestReading.collectAsState()

    LaunchedEffect(roomId) { viewModel.loadHistory(roomId) }
    LaunchedEffect(saveState) {
        if (saveState is MeterReadingUiState.Success) {
            viewModel.resetState()
            onSaved()
        }
    }

    val currentMonth = YearMonth.now().toString()
    var billingMonth by remember { mutableStateOf(currentMonth) }
    var elecPrev by remember(latestReading) { mutableStateOf(latestReading?.electricCurrent?.toLong()?.toString() ?: "0") }
    var elecCurr by remember { mutableStateOf("") }
    var waterPrev by remember(latestReading) { mutableStateOf(latestReading?.waterCurrent?.toLong()?.toString() ?: "0") }
    var waterCurr by remember { mutableStateOf("") }

    val elecUsage = (elecCurr.toLongOrNull() ?: 0) - (elecPrev.toLongOrNull() ?: 0)
    val waterUsage = (waterCurr.toLongOrNull() ?: 0) - (waterPrev.toLongOrNull() ?: 0)

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Nhập chỉ số điện nước", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        Text("Tiêu thụ: $elecUsage kWh", style = MaterialTheme.typography.bodySmall,
                            color = if (elecUsage >= 0) StatusAvailable else MaterialTheme.colorScheme.error)
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
                    Text("Tiêu thụ: $waterUsage m³", style = MaterialTheme.typography.bodySmall,
                        color = if (waterUsage >= 0) StatusAvailable else MaterialTheme.colorScheme.error)
                }
            }

            // History
            if (history.isNotEmpty()) {
                SectionCard(title = "Lịch sử gần đây") {
                    history.take(3).forEach { mr ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(mr.billingMonth, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("⚡${mr.electricUsage.toInt()} kWh  💧${mr.waterUsage.toInt()} m³",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (mr != history.take(3).last()) HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            if (saveState is MeterReadingUiState.Error) {
                Text((saveState as MeterReadingUiState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    viewModel.recordReading(
                        roomId = roomId,
                        billingMonth = billingMonth,
                        electricPrevious = elecPrev.toDoubleOrNull() ?: 0.0,
                        electricCurrent = elecCurr.toDoubleOrNull() ?: 0.0,
                        waterPrevious = waterPrev.toDoubleOrNull() ?: 0.0,
                        waterCurrent = waterCurr.toDoubleOrNull() ?: 0.0
                    )
                },
                enabled = elecCurr.isNotBlank() && waterCurr.isNotBlank() && elecUsage >= 0 && waterUsage >= 0 && saveState !is MeterReadingUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (saveState is MeterReadingUiState.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("Lưu chỉ số", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

