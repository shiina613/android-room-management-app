package com.kma.lamphoun.roomapp.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.DashboardResponse
import com.kma.lamphoun.roomapp.ui.common.NotificationBadgeViewModel
import com.kma.lamphoun.roomapp.ui.common.toVnd
import com.kma.lamphoun.roomapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel(),
    notifBadgeViewModel: NotificationBadgeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val unreadCount by notifBadgeViewModel.unreadCount.collectAsState()

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quản Trị Hệ Thống",
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        BadgedBox(badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("$unreadCount")
                                }
                            }
                        }) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Thông báo",
                                tint = OnSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onProfileClick) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLow)
            )
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
            // Greeting
            Column {
                Text(
                    "Xin chào, ${uiState.adminName.ifBlank { "Admin" }} 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    "Tổng quan hệ thống",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { viewModel.loadDashboard() },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Thử lại")
                        }
                    }
                }

                uiState.dashboard != null -> {
                    AdminStatsGrid(uiState.dashboard!!)
                }
            }
        }
    }
}

@Composable
private fun AdminStatsGrid(data: DashboardResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "Tổng phòng",
                value = data.totalRooms.toString(),
                icon = Icons.Filled.Home,
                color = Primary
            )
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "Đang thuê",
                value = data.occupiedRooms.toString(),
                icon = Icons.Filled.People,
                color = Tertiary
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "Phòng trống",
                value = data.availableRooms.toString(),
                icon = Icons.Filled.MeetingRoom,
                color = StatusAvailable
            )
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "Tổng người thuê",
                value = data.totalTenants.toString(),
                icon = Icons.Filled.Group,
                color = Secondary
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "HĐ hiệu lực",
                value = data.activeContracts.toString(),
                icon = Icons.Filled.Description,
                color = Primary
            )
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "HĐ chưa TT",
                value = data.unpaidInvoices.toString(),
                icon = Icons.Filled.Receipt,
                color = StatusUnpaid
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "Doanh thu",
                value = data.revenueThisMonth.toVnd(),
                icon = Icons.Filled.TrendingUp,
                color = StatusPaid
            )
            AdminStatCard(
                modifier = Modifier.weight(1f),
                label = "Công nợ",
                value = data.debtThisMonth.toVnd(),
                icon = Icons.Filled.Warning,
                color = StatusOverdue
            )
        }
    }
}

@Composable
private fun AdminStatCard(
    modifier: Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
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
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                maxLines = 1
            )
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}
