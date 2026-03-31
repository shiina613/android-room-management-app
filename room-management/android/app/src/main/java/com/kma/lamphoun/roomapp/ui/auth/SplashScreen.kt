package com.kma.lamphoun.roomapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.theme.Background
import com.kma.lamphoun.roomapp.ui.theme.Primary
import com.kma.lamphoun.roomapp.ui.theme.Secondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigate: (role: String?) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = null)
    val role by viewModel.role.collectAsState(initial = null)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            delay(1500)
            onNavigate(if (isLoggedIn == true) role else null)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background
        Box(
            modifier = Modifier.size(320.dp).align(Alignment.TopStart).offset((-80).dp, (-80).dp)
                .background(Brush.radialGradient(listOf(Primary.copy(alpha = 0.08f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier.size(240.dp).align(Alignment.BottomEnd).offset(60.dp, 60.dp)
                .background(Brush.radialGradient(listOf(Secondary.copy(alpha = 0.06f), Color.Transparent)), CircleShape)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Logo
            Box(
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)).background(Primary),
                contentAlignment = Alignment.Center
            ) { Text("🏠", fontSize = 44.sp) }

            Text(
                "Quản Lý Phòng Trọ",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Text(
                "Hệ thống quản lý thông minh",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = Primary, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
        }

        // Security badge (Figma: Feedback Orb)
        Card(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                Spacer(Modifier.width(8.dp))
                Text("Hệ thống bảo mật", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
            }
        }
    }
}
