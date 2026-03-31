package com.kma.lamphoun.roomapp.ui.tenant

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun MyContractScreen(onNavigateBack: () -> Unit) {
    val contract = MockData.contracts.firstOrNull { it.status == "ACTIVE" }

    Scaffold(
        containerColor = Background,
        topBar = { AppTopBar(title = "Hợp đồng của tôi", onBack = onNavigateBack) }
    ) { padding ->
        if (contract == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📄", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Bạn chưa có hợp đồng nào", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status header
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Hợp đồng thuê phòng", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                        Text(contract.roomTitle ?: "Phòng #${contract.roomId}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(12.dp))
                        StatusChip(contract.status)
                    }
                }

                // Contract details
                SectionCard(title = "Chi tiết hợp đồng") {
                    InfoRow("Phòng", contract.roomTitle ?: "—")
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Chủ trọ", "Nguyễn Văn A")
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = OutlineVariant)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Ngày bắt đầu", contract.startDate)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Ngày kết thúc", contract.endDate)
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = OutlineVariant)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Tiền thuê/tháng", contract.monthlyRent.toVnd())
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Tiền cọc", contract.deposit.toVnd())
                }

                // Duration progress
                SectionCard(title = "Thời hạn hợp đồng") {
                    val totalMonths = 12
                    val passedMonths = 3
                    val progress = passedMonths.toFloat() / totalMonths
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${contract.startDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${contract.endDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Primary, trackColor = SurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Đã qua $passedMonths/$totalMonths tháng", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
