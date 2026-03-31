package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import com.kma.lamphoun.roomapp.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToRooms: () -> Unit,
    onNavigateToContracts: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Primary),
                            contentAlignment = Alignment.Center
                        ) { Text("🏠", fontSize = 14.sp) }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Quản Lý Phòng Trọ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo", tint = Primary)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp)) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
                listOf(
                    Triple("Tổng quan", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
                    Triple("Phòng", Icons.Filled.Home, Icons.Outlined.Home),
                    Triple("Hợp đồng", Icons.Filled.Description, Icons.Outlined.Description),
                    Triple("Báo cáo", Icons.Filled.BarChart, Icons.Outlined.BarChart)
                ).forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                1 -> onNavigateToRooms()
                                2 -> onNavigateToContracts()
                                3 -> onNavigateToReports()
                            }
                        },
                        icon = { Icon(if (selectedTab == index) filledIcon else outlinedIcon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = PrimaryContainer
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRooms,
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = "Thêm") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome Hero
            WelcomeSection(name = viewModel.fullName)

            // Stats Bento Grid
            when (val s = uiState) {
                is DashboardUiState.Success -> StatsGrid(s.data)
                is DashboardUiState.Loading -> StatsGridSkeleton()
                else -> {}
            }

            // Shortcuts
            ShortcutsSection(
                onRooms = onNavigateToRooms,
                onContracts = onNavigateToContracts,
                onInvoices = onNavigateToInvoices,
                onReports = onNavigateToReports
            )

            // Recent Activity placeholder
            RecentActivitySection()

            Spacer(Modifier.height(80.dp)) // FAB clearance
        }
    }
}

@Composable
private fun WelcomeSection(name: String) {
    Column {
        Text(
            text = "Xin chào, ${name.ifBlank { "Chủ trọ" }} 👋",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Đây là tổng quan hệ thống của bạn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatsGrid(data: DashboardResponse) {
    val vnd = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Tổng phòng",
                value = data.totalRooms.toString(),
                icon = Icons.Filled.Home,
                iconBg = Primary.copy(alpha = 0.12f),
                iconTint = Primary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Đang thuê",
                value = data.occupiedRooms.toString(),
                icon = Icons.Filled.People,
                iconBg = StatusOccupied.copy(alpha = 0.12f),
                iconTint = StatusOccupied
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Phòng trống",
                value = data.availableRooms.toString(),
                icon = Icons.Filled.MeetingRoom,
                iconBg = StatusAvailable.copy(alpha = 0.12f),
                iconTint = StatusAvailable
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Chưa thanh toán",
                value = data.unpaidInvoices.toString(),
                icon = Icons.Filled.Receipt,
                iconBg = StatusUnpaid.copy(alpha = 0.12f),
                iconTint = StatusUnpaid
            )
        }
        // Revenue card full width
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Primary),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = Color.White) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Doanh thu tháng này", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        "${vnd.format(data.revenueThisMonth.toLong())} ₫",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatsGridSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Card(modifier = Modifier.weight(1f).height(131.dp), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer)) {}
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Card(modifier = Modifier.weight(1f).height(131.dp), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer)) {}
            }
        }
    }
}

@Composable
private fun ShortcutsSection(
    onRooms: () -> Unit,
    onContracts: () -> Unit,
    onInvoices: () -> Unit,
    onReports: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Truy cập nhanh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                Triple("Phòng", Icons.Outlined.Home, onRooms),
                Triple("Hợp đồng", Icons.Outlined.Description, onContracts),
                Triple("Hóa đơn", Icons.Outlined.Receipt, onInvoices),
                Triple("Báo cáo", Icons.Outlined.BarChart, onReports)
            ).forEach { (label, icon, onClick) ->
                ShortcutItem(modifier = Modifier.weight(1f), label = label, icon = icon, onClick = onClick)
            }
        }
    }
}

@Composable
private fun ShortcutItem(modifier: Modifier = Modifier, label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color.White)
            .clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = label, tint = Primary, modifier = Modifier.size(22.dp)) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun RecentActivitySection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Hoạt động gần đây", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF181C1B))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityItem(icon = Icons.Filled.Receipt, title = "Hóa đơn tháng 3/2026", subtitle = "Phòng 101 - Chờ thanh toán", tint = StatusUnpaid)
                HorizontalDivider(color = OutlineVariant)
                ActivityItem(icon = Icons.Filled.CheckCircle, title = "Thanh toán xác nhận", subtitle = "Phòng 202 - Đã thanh toán", tint = StatusPaid)
            }
        }
    }
}

@Composable
private fun ActivityItem(icon: ImageVector, title: String, subtitle: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
