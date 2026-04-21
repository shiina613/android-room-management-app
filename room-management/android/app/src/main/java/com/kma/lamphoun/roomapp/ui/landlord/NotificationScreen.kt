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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.NotificationResponse
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.shared.NotificationViewModel
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = {
            AppTopBar(title = "Thông báo", onBack = onNavigateBack) {
                if (unreadCount > 0) {
                    TextButton(onClick = { viewModel.markAllRead() }) {
                        Text("Đọc tất cả", color = Primary, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (unreadCount > 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondaryContainer.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Bạn có $unreadCount thông báo chưa đọc",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            when {
                isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                notifications.isEmpty() -> item { EmptyState("Không có thông báo nào") }
                else -> items(notifications) { notif ->
                    NotificationItem(notification = notif, onRead = { viewModel.markRead(notif.id) })
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationResponse, onRead: (() -> Unit)? = null) {
    val (icon, tint) = when (notification.type) {
        "INVOICE_CREATED" -> Icons.Default.Receipt to Primary
        "PAYMENT_RECEIVED" -> Icons.Default.CheckCircle to StatusPaid
        "CONTRACT_EXPIRING" -> Icons.Default.Warning to Tertiary
        "ROOM_STATUS_CHANGED" -> Icons.Default.Home to Secondary
        "INVOICE_OVERDUE" -> Icons.Default.Error to StatusOverdue
        else -> Icons.Default.Notifications to Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.read) SecondaryContainer.copy(alpha = 0.25f) else SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = { if (!notification.read) onRead?.invoke() }
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.read) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(notification.content, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(notification.createdAt.take(10), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
        }
    }
}

