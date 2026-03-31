package com.kma.lamphoun.roomapp.ui.tenant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.landlord.NotificationItem
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun TenantNotificationScreen(onNavigateBack: () -> Unit) {
    // Tenant sees only their own notifications (invoice, payment related)
    val notifications = MockData.notifications.filter {
        it.type in listOf("INVOICE_CREATED", "PAYMENT_RECEIVED", "INVOICE_OVERDUE", "CONTRACT_EXPIRING")
    }
    val unreadCount = notifications.count { !it.read }

    Scaffold(
        containerColor = Background,
        topBar = {
            AppTopBar(title = "Thông báo", onBack = onNavigateBack) {
                if (unreadCount > 0) {
                    TextButton(onClick = {}) {
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
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryContainer)) {
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
