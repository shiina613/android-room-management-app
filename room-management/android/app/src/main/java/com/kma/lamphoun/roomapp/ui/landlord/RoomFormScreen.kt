package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun RoomFormScreen(
    roomId: Long? = null,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit
) {
    val isEdit = roomId != null
    val existing = if (isEdit) MockData.rooms.find { it.id == roomId } else null

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var address by remember { mutableStateOf(existing?.address ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toLong()?.toString() ?: "") }
    var elecPrice by remember { mutableStateOf(existing?.elecPrice?.toLong()?.toString() ?: "3500") }
    var waterPrice by remember { mutableStateOf(existing?.waterPrice?.toLong()?.toString() ?: "15000") }
    var servicePrice by remember { mutableStateOf(existing?.servicePrice?.toLong()?.toString() ?: "200000") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(existing?.category ?: "STUDIO") }
    val categories = listOf("STUDIO", "SINGLE", "APARTMENT", "SHARED")
    val categoryLabels = mapOf("STUDIO" to "Studio", "SINGLE" to "Phòng đơn", "APARTMENT" to "Căn hộ", "SHARED" to "Phòng ghép")

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = if (isEdit) "Chỉnh sửa phòng" else "Thêm phòng mới", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero image placeholder (Figma: Hero Visual)
            // TODO: Add image picker here — replace Box with image upload component
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).background(SurfaceVariant, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 32.sp)
                    Text("Thêm ảnh phòng", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Text("(TODO: image picker)", fontSize = 10.sp, color = OnSurfaceVariant)
                }
            }

            // Basic info
            SectionCard(title = "Thông tin cơ bản") {
                FormField("Tên phòng *", title, { title = it }, "VD: Phòng 101")
                Spacer(Modifier.height(12.dp))
                FormField("Địa chỉ *", address, { address = it }, "VD: 15 Nguyễn Trãi, Q1")
                Spacer(Modifier.height(12.dp))
                Text("Loại phòng", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(categoryLabels[cat] ?: cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer, selectedLabelColor = Primary)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                FormField("Mô tả", description, { description = it }, "Mô tả ngắn về phòng", maxLines = 3)
            }

            // Pricing
            SectionCard(title = "Giá cả") {
                FormField("Giá thuê/tháng (₫) *", price, { price = it }, "VD: 3500000", keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormField("Giá điện (₫/kWh)", elecPrice, { elecPrice = it }, "3500", keyboardType = KeyboardType.Number)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormField("Giá nước (₫/m³)", waterPrice, { waterPrice = it }, "15000", keyboardType = KeyboardType.Number)
                    }
                }
                Spacer(Modifier.height(12.dp))
                FormField("Phí dịch vụ/tháng (₫)", servicePrice, { servicePrice = it }, "200000", keyboardType = KeyboardType.Number)
            }

            // Tip card (Figma: Tip Card with #F1F4F1)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, null, tint = Primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Giá điện, nước sẽ được dùng để tính hóa đơn hàng tháng. Bạn có thể cập nhật sau.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onSaved,
                enabled = title.isNotBlank() && address.isNotBlank() && price.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(if (isEdit) "Lưu thay đổi" else "Tạo phòng", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String = "", keyboardType: KeyboardType = KeyboardType.Text, maxLines: Int = 1
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) }, placeholder = { Text(placeholder, color = OutlineVariant) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
    )
}
