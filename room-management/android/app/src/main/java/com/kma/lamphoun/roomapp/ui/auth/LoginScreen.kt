package com.kma.lamphoun.roomapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).role)
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceContainerLow)
    ) {
        // Decorative gradient blobs
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(Primary.copy(alpha = 0.07f), Color.Transparent))
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 50.dp)
                .background(
                    Brush.radialGradient(listOf(Secondary.copy(alpha = 0.05f), Color.Transparent))
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Logo + app name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientStart, GradientEnd),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(44f, 44f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("🏠", fontSize = 20.sp) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Quản Lý Phòng Trọ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        "Smart Room Manager",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Login card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Đăng nhập",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Text(
                        "Chào mừng bạn trở lại",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Tên đăng nhập") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            unfocusedBorderColor = OutlineVariant
                        )
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            unfocusedBorderColor = OutlineVariant
                        )
                    )

                    if (uiState is AuthUiState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Gradient primary button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (username.isNotBlank() && password.isNotBlank() && uiState !is AuthUiState.Loading)
                                    Brush.linearGradient(listOf(GradientStart, GradientEnd))
                                else
                                    Brush.linearGradient(listOf(OutlineVariant, OutlineVariant))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { viewModel.login(username, password) },
                            enabled = username.isNotBlank() && password.isNotBlank() && uiState !is AuthUiState.Loading,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                        ) {
                            if (uiState is AuthUiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Đăng nhập", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chưa có tài khoản?", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Đăng ký ngay", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

