package com.kma.lamphoun.roomapp.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.theme.*
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
            delay(1800)
            onNavigate(if (isLoggedIn == true) role else null)
        }
    }

    // Animated dot indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        // Subtle background gradient blob top-right
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(listOf(Primary.copy(alpha = 0.06f), Color.Transparent))
                )
        )
        // Bottom-left blob
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    Brush.radialGradient(listOf(Secondary.copy(alpha = 0.05f), Color.Transparent))
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo — rounded square with gradient
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(88f, 88f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏠", fontSize = 40.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Quản Lý Phòng Trọ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Smart Room Manager",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(8.dp))

            // Animated loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    val dotAlpha by rememberInfiniteTransition(label = "dot$i").animateFloat(
                        initialValue = 0.2f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(600, delayMillis = i * 200),
                            RepeatMode.Reverse
                        ),
                        label = "dot$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Primary.copy(alpha = dotAlpha))
                    )
                }
            }
        }

        // Bottom badge
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Primary.copy(alpha = alpha))
                )
                Text(
                    "Hệ thống bảo mật JWT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Primary
                )
            }
        }
    }
}

