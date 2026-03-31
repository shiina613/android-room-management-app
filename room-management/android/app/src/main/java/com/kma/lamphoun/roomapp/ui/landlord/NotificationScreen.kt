package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.data.remote.dto.NotificationResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun NotificationScreen(onNavigateBack: () -> Unit) {
    val notifications = remember { MockData.notifications.toMutableStateList() }
    val unreadCount = notifications.count { !it.read }

    Scaffold(
        containerColor = Background,
        topBar = {
            AppTopBar(title = "Thông báo", onBack = onNavigateBack) {
                if (unreadCount > 0) {
                    TextButton(onClick = { /* mark all read */ }) {
                        Text("Đọc tất cả", color = Primary, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (unreadCount > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Bạn có $unreadCount thông báo chưa đọc", style = MaterialTheme.typography.bodySmall, color = Primary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            if (notifications.isEmpty()) {
                item { EmptyState("Không có thông báo nào") }
            } else {
                items(notifications) { notif ->
                    NotificationItem(notification = notif)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationResponse) {
    val (icon, tint) = when (notification.type) {
        "INVOICE_CREATED" -> Icons.Default.Receipt to Primary
        "PAYMENT_RECEIVED" -> Icons.Default.CheckCircle to StatusPaid
        "CONTRACT_EXPIRING" -> Icons.Default.Warning to StatusUnpaid
        "ROOM_STATUS_CHANGED" -> Icons.Default.Home to Secondary
        "INVOICE_OVERDUE" -> Icons.Default.Error to StatusOverdue
        else -> Icons.Default.Notifications to Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.read) PrimaryContainer.copy(alpha = 0.4f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (!notification.read) 2.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(notification.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    if (!notification.read) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(notification.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(notification.createdAt.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
