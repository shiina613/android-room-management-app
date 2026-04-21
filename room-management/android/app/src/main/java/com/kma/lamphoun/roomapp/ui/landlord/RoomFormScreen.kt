package com.kma.lamphoun.roomapp.ui.landlord

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kma.lamphoun.roomapp.data.remote.dto.RoomRequest
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFormScreen(
    roomId: Long? = null,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val isEdit = roomId != null
    val selectedRoom by viewModel.selectedRoom.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current

    // Ảnh đã chọn từ gallery (chưa upload)
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    // Launcher chọn ảnh từ gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> pendingImageUri = uri }

    LaunchedEffect(roomId) { if (roomId != null) viewModel.loadRoom(roomId) }
    LaunchedEffect(formState) {
        if (formState is RoomFormUiState.Success) {
            viewModel.resetFormState()
            onSaved()
        }
    }

    var title by remember(selectedRoom) { mutableStateOf(selectedRoom?.title ?: "") }
    var address by remember(selectedRoom) { mutableStateOf(selectedRoom?.address ?: "") }
    var price by remember(selectedRoom) { mutableStateOf(selectedRoom?.price?.toLong()?.toString() ?: "") }
    var elecPrice by remember(selectedRoom) { mutableStateOf(selectedRoom?.elecPrice?.toLong()?.toString() ?: "3500") }
    var waterPrice by remember(selectedRoom) { mutableStateOf(selectedRoom?.waterPrice?.toLong()?.toString() ?: "15000") }
    var servicePrice by remember(selectedRoom) { mutableStateOf(selectedRoom?.servicePrice?.toLong()?.toString() ?: "200000") }
    var description by remember(selectedRoom) { mutableStateOf(selectedRoom?.description ?: "") }
    var selectedCategory by remember(selectedRoom) { mutableStateOf(selectedRoom?.category ?: "STUDIO") }
    val categories = listOf("STUDIO", "SINGLE", "APARTMENT", "SHARED")
    val categoryLabels = mapOf("STUDIO" to "Studio", "SINGLE" to "Phòng đơn", "APARTMENT" to "Căn hộ", "SHARED" to "Phòng ghép")

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = if (isEdit) "Chỉnh sửa phòng" else "Thêm phòng mới", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ảnh phòng — click để chọn từ gallery
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val displayUrl = pendingImageUri?.toString() ?: selectedRoom?.imageUrl
                if (!displayUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = displayUrl,
                        contentDescription = "Ảnh phòng",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Overlay hint
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("Nhấn để đổi ảnh", color = Color.White, fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp))
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = OnSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Nhấn để thêm ảnh phòng", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                }
            }

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
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SecondaryContainer, selectedLabelColor = Primary)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                FormField("Mô tả", description, { description = it }, "Mô tả ngắn về phòng", maxLines = 3)
            }

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

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, null, tint = Primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Giá điện, nước sẽ được dùng để tính hóa đơn hàng tháng.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (formState is RoomFormUiState.Error) {
                Text((formState as RoomFormUiState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    val req = RoomRequest(
                        title = title, address = address,
                        price = price.toDoubleOrNull() ?: 0.0,
                        elecPrice = elecPrice.toDoubleOrNull() ?: 3500.0,
                        waterPrice = waterPrice.toDoubleOrNull() ?: 15000.0,
                        servicePrice = servicePrice.toDoubleOrNull() ?: 200000.0,
                        status = selectedRoom?.status ?: "AVAILABLE",
                        category = selectedCategory,
                        description = description.ifBlank { null }
                    )
                    // Helper: copy URI sang File tạm để upload
                    fun uriToTempFile(uri: Uri): File? = try {
                        val stream = context.contentResolver.openInputStream(uri) ?: return null
                        val ext = context.contentResolver.getType(uri)?.substringAfterLast("/") ?: "jpg"
                        val tmp = File.createTempFile("room_img_", ".$ext", context.cacheDir)
                        FileOutputStream(tmp).use { out -> stream.copyTo(out) }
                        tmp
                    } catch (e: Exception) { null }

                    if (isEdit && roomId != null) {
                        viewModel.updateRoom(roomId, req)
                        pendingImageUri?.let { uri ->
                            uriToTempFile(uri)?.let { file -> viewModel.uploadRoomImage(roomId, file) }
                        }
                    } else {
                        // Tạo phòng trước, sau đó upload ảnh khi có roomId
                        // Dùng LaunchedEffect theo dõi selectedRoom để upload
                        viewModel.createRoom(req)
                    }
                },
                enabled = title.isNotBlank() && address.isNotBlank() && price.isNotBlank() && formState !is RoomFormUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (formState is RoomFormUiState.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text(if (isEdit) "Lưu thay đổi" else "Tạo phòng", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
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

