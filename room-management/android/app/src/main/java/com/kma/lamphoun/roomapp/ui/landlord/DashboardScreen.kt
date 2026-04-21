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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import com.kma.lamphoun.roomapp.ui.common.toVnd
import com.kma.lamphoun.roomapp.ui.theme.*

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
        containerColor = SurfaceContainerLow,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                            contentAlignment = Alignment.Center
                        ) { Text("🏠", fontSize = 14.sp) }
                        Spacer(Modifier.width(10.dp))
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
                        Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo", tint = OnSurfaceVariant)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp)) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLow)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceContainerLowest,
                tonalElevation = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
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
                        icon = { Icon(if (selectedTab == index) filledIcon else outlinedIcon, label) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = SecondaryContainer
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
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp
                )
            ) { Icon(Icons.Default.Add, contentDescription = "Thêm") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome
            Column {
                Text(
                    "Xin chào, ${viewModel.fullName.ifBlank { "Chủ trọ" }} 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    "Đây là tổng quan hệ thống của bạn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            // Stats
            when (val s = uiState) {
                is DashboardUiState.Success -> StatsGrid(s.data)
                is DashboardUiState.Loading -> StatsGridSkeleton()
                else -> {}
            }

            // Quick access
            ShortcutsSection(
                onRooms = onNavigateToRooms,
                onContracts = onNavigateToContracts,
                onInvoices = onNavigateToInvoices,
                onReports = onNavigateToReports
            )

            // Recent activity
            if (uiState is DashboardUiState.Success) {
                RecentActivitySection(uiState as DashboardUiState.Success)
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StatsGrid(data: DashboardResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Tổng phòng", data.totalRooms.toString(), Icons.Filled.Home, Primary)
            StatCard(Modifier.weight(1f), "Đang thuê", data.occupiedRooms.toString(), Icons.Filled.People, Tertiary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Phòng trống", data.availableRooms.toString(), Icons.Filled.MeetingRoom, StatusAvailable)
            StatCard(Modifier.weight(1f), "Chưa TT", data.unpaidInvoices.toString(), Icons.Filled.Receipt, StatusUnpaid)
        }
        // Revenue hero card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.TrendingUp, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Doanh thu tháng này", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        data.revenueThisMonth.toVnd(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (data.debtThisMonth > 0) {
                        Text(
                            "Công nợ: ${data.debtThisMonth.toVnd()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OnBackground)
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun StatsGridSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Card(modifier = Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer)) {}
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Card(modifier = Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer)) {}
            }
        }
        Card(modifier = Modifier.fillMaxWidth().height(88.dp), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer)) {}
    }
}

@Composable
private fun ShortcutsSection(onRooms: () -> Unit, onContracts: () -> Unit, onInvoices: () -> Unit, onReports: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Truy cập nhanh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBackground)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                Triple("Phòng", Icons.Outlined.Home, onRooms),
                Triple("Hợp đồng", Icons.Outlined.Description, onContracts),
                Triple("Hóa đơn", Icons.Outlined.Receipt, onInvoices),
                Triple("Báo cáo", Icons.Outlined.BarChart, onReports)
            ).forEach { (label, icon, onClick) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainerLowest)
                        .clickable(onClick = onClick)
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) { Icon(icon, label, tint = Primary, modifier = Modifier.size(20.dp)) }
                    Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurface)
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(state: DashboardUiState.Success) {
    val data = state.data
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Tổng quan nhanh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBackground)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityRow("Hợp đồng đang hiệu lực", "${data.activeContracts}", Icons.Filled.Description, Primary)
                ActivityRow("Hợp đồng sắp hết hạn (30 ngày)", "${data.expiringIn30Days}", Icons.Filled.Warning, Tertiary)
                ActivityRow("Hóa đơn quá hạn", "${data.overdueInvoices}", Icons.Filled.Error, StatusOverdue)
                ActivityRow("Tổng người thuê", "${data.totalTenants}", Icons.Filled.People, Secondary)
            }
        }
    }
}

@Composable
private fun ActivityRow(label: String, value: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(16.dp)) }
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
        }
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

