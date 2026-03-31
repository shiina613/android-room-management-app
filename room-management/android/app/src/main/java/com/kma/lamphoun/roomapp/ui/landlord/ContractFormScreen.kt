package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractFormScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit
) {
    var selectedRoomId by remember { mutableStateOf<Long?>(null) }
    var selectedTenantId by remember { mutableStateOf<Long?>(null) }
    var startDate by remember { mutableStateOf("2026-04-01") }
    var endDate by remember { mutableStateOf("2027-03-31") }
    var deposit by remember { mutableStateOf("7000000") }
    var monthlyRent by remember { mutableStateOf("") }
    var roomDropdownExpanded by remember { mutableStateOf(false) }
    var tenantDropdownExpanded by remember { mutableStateOf(false) }

    val availableRooms = MockData.rooms.filter { it.status == "AVAILABLE" }
    val tenants = MockData.tenants
    val selectedRoom = availableRooms.find { it.id == selectedRoomId }
    val selectedTenant = tenants.find { it.id == selectedTenantId }

    // Auto-fill rent from room price
    LaunchedEffect(selectedRoomId) {
        selectedRoom?.let { monthlyRent = it.price.toLong().toString() }
    }

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Tạo hợp đồng", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Thông tin hợp đồng") {
                // Room picker
                ExposedDropdownMenuBox(expanded = roomDropdownExpanded, onExpandedChange = { roomDropdownExpanded = it }) {
                    OutlinedTextField(
                        value = selectedRoom?.title ?: "Chọn phòng",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Phòng *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roomDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                    ExposedDropdownMenu(expanded = roomDropdownExpanded, onDismissRequest = { roomDropdownExpanded = false }) {
                        availableRooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text("${room.title} - ${room.price.toVnd()}") },
                                onClick = { selectedRoomId = room.id; roomDropdownExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Tenant picker
                ExposedDropdownMenuBox(expanded = tenantDropdownExpanded, onExpandedChange = { tenantDropdownExpanded = it }) {
                    OutlinedTextField(
                        value = selectedTenant?.fullName ?: "Chọn người thuê",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Người thuê *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tenantDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                    ExposedDropdownMenu(expanded = tenantDropdownExpanded, onDismissRequest = { tenantDropdownExpanded = false }) {
                        tenants.forEach { tenant ->
                            DropdownMenuItem(
                                text = { Text("${tenant.fullName} - ${tenant.phone}") },
                                onClick = { selectedTenantId = tenant.id; tenantDropdownExpanded = false }
                            )
                        }
                    }
                }
            }

            SectionCard(title = "Thời hạn & Tài chính") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = startDate, onValueChange = { startDate = it },
                        label = { Text("Ngày bắt đầu") }, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                    OutlinedTextField(
                        value = endDate, onValueChange = { endDate = it },
                        label = { Text("Ngày kết thúc") }, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = monthlyRent, onValueChange = { monthlyRent = it },
                    label = { Text("Tiền thuê/tháng (₫) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = deposit, onValueChange = { deposit = it },
                    label = { Text("Tiền cọc (₫)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                )
            }

            // Summary preview
            if (selectedRoom != null && selectedTenant != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Xem trước hợp đồng", fontWeight = FontWeight.Bold, color = Primary)
                        InfoRow("Phòng", selectedRoom.title)
                        InfoRow("Người thuê", selectedTenant.fullName)
                        InfoRow("Tiền thuê", monthlyRent.toLongOrNull()?.toVnd() ?: "—")
                        InfoRow("Tiền cọc", deposit.toLongOrNull()?.toVnd() ?: "—")
                    }
                }
            }

            Button(
                onClick = onSaved,
                enabled = selectedRoomId != null && selectedTenantId != null && monthlyRent.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Tạo hợp đồng", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
