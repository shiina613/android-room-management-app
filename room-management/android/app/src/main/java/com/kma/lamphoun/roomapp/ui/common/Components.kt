package com.kma.lamphoun.roomapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.kma.lamphoun.roomapp.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

val vndFormat: NumberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
fun Long.toVnd() = "${vndFormat.format(this)} ₫"
fun Double.toVnd() = "${vndFormat.format(this.toLong())} ₫"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại") }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLow)
    )
}

@Composable
fun StatusChip(status: String) {
    val (label, bg, fg) = when (status.uppercase()) {
        "AVAILABLE" -> Triple("Trống", StatusAvailable.copy(alpha = 0.12f), StatusAvailable)
        "OCCUPIED" -> Triple("Đang thuê", StatusOccupied.copy(alpha = 0.12f), StatusOccupied)
        "MAINTENANCE" -> Triple("Bảo trì", StatusMaintenance.copy(alpha = 0.12f), StatusMaintenance)
        "ACTIVE" -> Triple("Hiệu lực", StatusAvailable.copy(alpha = 0.12f), StatusAvailable)
        "EXPIRED" -> Triple("Hết hạn", StatusOccupied.copy(alpha = 0.12f), StatusOccupied)
        "TERMINATED" -> Triple("Đã chấm dứt", Color(0xFF6A1B9A).copy(alpha = 0.12f), Color(0xFF6A1B9A))
        "PAID" -> Triple("Đã thanh toán", StatusPaid.copy(alpha = 0.12f), StatusPaid)
        "UNPAID" -> Triple("Chưa thanh toán", StatusUnpaid.copy(alpha = 0.12f), StatusUnpaid)
        "OVERDUE" -> Triple("Quá hạn", StatusOverdue.copy(alpha = 0.12f), StatusOverdue)
        else -> Triple(status, SurfaceVariant, OnSurfaceVariant)
    }
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title.isNotBlank()) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
            }
            content()
        }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Hiển thị ảnh phòng từ URL, fallback về placeholder nếu không có ảnh
@Composable
fun RoomImagePlaceholder(imageUrl: String? = null, modifier: Modifier = Modifier) {
    if (!imageUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Ảnh phòng",
            modifier = modifier,
            contentScale = ContentScale.Crop,
            loading = {
                Box(modifier = modifier.background(SurfaceVariant), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Primary, strokeWidth = 2.dp)
                }
            },
            error = {
                Box(modifier = modifier.background(SurfaceVariant), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.BrokenImage, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
                }
            }
        )
    } else {
        Box(
            modifier = modifier.background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("📷", fontSize = 28.sp, color = OnSurfaceVariant)
        }
    }
}

