package com.kma.lamphoun.roomapp.ui.tenant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun MyRoomScreen(
    onNavigateBack: () -> Unit,
    viewModel: TenantViewModel = hiltViewModel()
) {
    val contract by viewModel.activeContract.collectAsState()

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Phòng của tôi", onBack = onNavigateBack) }
    ) { padding ->
        if (contract == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có phòng đang thuê", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoomImagePlaceholder(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)))

                SectionCard(title = contract!!.roomTitle ?: "Phòng của tôi") {
                    if (!contract!!.roomAddress.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(contract!!.roomAddress!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    StatusChip("OCCUPIED")
                }

                SectionCard(title = "Thông tin hợp đồng") {
                    InfoRow("Tiền thuê/tháng", contract!!.monthlyRent.toVnd())
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Tiền cọc", contract!!.deposit.toVnd())
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Bắt đầu", contract!!.startDate)
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Kết thúc", contract!!.endDate)
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Chủ trọ", contract!!.landlordName ?: "—")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

