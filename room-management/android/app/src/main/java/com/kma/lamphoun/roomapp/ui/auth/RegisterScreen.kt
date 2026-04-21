package com.kma.lamphoun.roomapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("ROLE_LANDLORD") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    val isFormValid = listOf(username, password, email, fullName, phone).all { it.isNotBlank() }

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = {
            TopAppBar(
                title = { Text("Tạo tài khoản", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Thông tin cá nhân",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )

                    RegisterField("Họ và tên *", fullName, { fullName = it }, "Nguyễn Văn A")
                    RegisterField("Tên đăng nhập *", username, { username = it }, "username")
                    RegisterField("Email *", email, { email = it }, "email@example.com", KeyboardType.Email)
                    RegisterField("Số điện thoại *", phone, { phone = it }, "0901234567", KeyboardType.Phone)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu *") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            unfocusedBorderColor = OutlineVariant
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Loại tài khoản",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("ROLE_LANDLORD" to "🏠 Chủ trọ", "ROLE_TENANT" to "👤 Người thuê").forEach { (value, label) ->
                            val selected = selectedRole == value
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                                        else Brush.linearGradient(listOf(SurfaceContainer, SurfaceContainer))
                                    )
                            ) {
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedRole = value },
                                    label = {
                                        Text(
                                            label,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (selected) Color.White else OnSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color.Transparent,
                                        containerColor = Color.Transparent,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }
            }

            if (uiState is AuthUiState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(
                    (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isFormValid && uiState !is AuthUiState.Loading)
                            Brush.linearGradient(listOf(GradientStart, GradientEnd))
                        else
                            Brush.linearGradient(listOf(OutlineVariant, OutlineVariant))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.register(username, password, email, fullName, phone, selectedRole) },
                    enabled = isFormValid && uiState !is AuthUiState.Loading,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                ) {
                    if (uiState is AuthUiState.Loading)
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    else
                        Text("Tạo tài khoản", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegisterField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String = "", keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = OutlineVariant) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            unfocusedBorderColor = OutlineVariant
        )
    )
}

