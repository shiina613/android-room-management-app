package com.kma.lamphoun.roomapp.ui.tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantHomeScreen(
    onNavigateToMyRoom: () -> Unit,
    onNavigateToMyContract: () -> Unit,
    onNavigateToMyInvoices: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val contract = MockData.contracts.firstOrNull { it.status == "ACTIVE" }
    val room = contract?.let { MockData.rooms.find { r -> r.id == it.roomId } }
    val latestInvoice = MockData.invoices.firstOrNull { it.status == "UNPAID" }
    val notifications = MockData.notifications.filter { !it.read }.take(3)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Primary), contentAlignment = Alignment.Center) {
                            Text("🏠", fontSize = 14.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Phòng Trọ", style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(badge = {
                            if (notifications.isNotEmpty()) Badge(containerColor = MaterialTheme.colorScheme.error) { Text("${notifications.size}") }
                        }) { Icon(Icons.Outlined.Notifications, null, tint = Primary) }
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(SurfaceVariant), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = Primary, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
                listOf(
                    Triple("Trang chủ", Icons.Filled.Home, Icons.Outlined.Home),
                    Triple("Phòng tôi", Icons.Filled.MeetingRoom, Icons.Outlined.MeetingRoom),
                    Triple("Hợp đồng", Icons.Filled.Description, Icons.Outlined.Description),
                    Triple("Hóa đơn", Icons.Filled.Receipt, Icons.Outlined.Receipt)
                ).forEachIndexed { index, (label, filled, outlined) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) { 1 -> onNavigateToMyRoom(); 2 -> onNavigateToMyContract(); 3 -> onNavigateToMyInvoices() }
                        },
                        icon = { Icon(if (selectedTab == index) filled else outlined, label) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, selectedTextColor = Primary, indicatorColor = PrimaryContainer)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome
            Column {
                Text("Xin chào, Trần Thị Bình 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Chào mừng bạn trở về nhà", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Room info card (Figma: #005344 background)
            if (room != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Phòng của tôi", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                Text("Đang thuê", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(room.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(room.address, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            RoomInfoChip("💰", room.price.toVnd())
                            RoomInfoChip("⚡", "${room.elecPrice.toInt()}₫/kWh")
                            RoomInfoChip("💧", "${room.waterPrice.toInt()}₫/m³")
                        }
                    }
                }
            }

            // Latest invoice (Figma: glassmorphism section)
            if (latestInvoice != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Hóa đơn mới nhất", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            StatusChip(latestInvoice.status)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Tháng ${latestInvoice.billingMonth}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(latestInvoice.totalAmount.toVnd(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                        if (latestInvoice.dueDate != null) {
                            Spacer(Modifier.height(4.dp))
                            Text("Hạn thanh toán: ${latestInvoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = StatusUnpaid)
                        }
                    }
                }
            }

            // Recent notifications
            if (notifications.isNotEmpty()) {
                SectionCard(title = "Thông báo gần đây") {
                    notifications.forEach { notif ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(notif.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text(notif.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Support card (Figma: #35618F background)
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Secondary)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cần hỗ trợ?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Liên hệ chủ trọ ngay nếu có vấn đề", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("Liên hệ", color = Secondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun RoomInfoChip(emoji: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(value, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Medium)
    }
}
