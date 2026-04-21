package com.kma.lamphoun.roomapp.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.data.remote.dto.UpdateProfileRequest
import com.kma.lamphoun.roomapp.ui.auth.AuthViewModel
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val role by authViewModel.role.collectAsState(initial = null)
    val profile by profileViewModel.profile.collectAsState()
    val updateState by profileViewModel.updateState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Edit profile state
    var editFullName by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var editEmail by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var editPhone by remember(profile) { mutableStateOf(profile?.phone ?: "") }

    // Password change state
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { profileViewModel.loadProfile() }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất") },
            text = { Text("Bạn có chắc muốn đăng xuất không?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Đăng xuất", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Hủy") } }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false; currentPassword = ""; newPassword = "" },
            title = { Text("Đổi mật khẩu") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPassword, onValueChange = { currentPassword = it },
                        label = { Text("Mật khẩu hiện tại") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                    OutlinedTextField(
                        value = newPassword, onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.changePassword(currentPassword, newPassword)
                        showPasswordDialog = false
                        currentPassword = ""; newPassword = ""
                    },
                    enabled = currentPassword.isNotBlank() && newPassword.length >= 6
                ) { Text("Xác nhận", color = Primary) }
            },
            dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Hủy") } }
        )
    }

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Hồ sơ cá nhân", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar hero card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) { Text("👤", fontSize = 32.sp) }
                    Text(
                        profile?.fullName ?: "Đang tải...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        when (role) {
                            "ROLE_LANDLORD" -> "Chủ trọ"
                            "ROLE_TENANT" -> "Người thuê"
                            "ROLE_ADMIN" -> "Quản trị viên"
                            else -> "Người dùng"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Profile info
            SectionCard(title = "Thông tin tài khoản") {
                if (profile != null) {
                    ProfileInfoRow(Icons.Default.Person, "Họ tên", profile!!.fullName ?: "—")
                    Spacer(Modifier.height(8.dp))
                    ProfileInfoRow(Icons.Default.Email, "Email", profile!!.email ?: "—")
                    Spacer(Modifier.height(8.dp))
                    ProfileInfoRow(Icons.Default.Phone, "Số điện thoại", profile!!.phone ?: "—")
                    Spacer(Modifier.height(8.dp))
                    ProfileInfoRow(Icons.Default.Badge, "Tên đăng nhập", profile!!.username ?: "—")
                } else {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Edit profile
            SectionCard(title = "Cập nhật thông tin") {
                OutlinedTextField(
                    value = editFullName, onValueChange = { editFullName = it },
                    label = { Text("Họ tên") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary, unfocusedBorderColor = OutlineVariant)
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = editEmail, onValueChange = { editEmail = it },
                    label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary, unfocusedBorderColor = OutlineVariant)
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = editPhone, onValueChange = { editPhone = it },
                    label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary, unfocusedBorderColor = OutlineVariant)
                )
                if (updateState is ProfileUpdateState.Error) {
                    Spacer(Modifier.height(6.dp))
                    Text((updateState as ProfileUpdateState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                if (updateState is ProfileUpdateState.Success) {
                    Spacer(Modifier.height(6.dp))
                    Text("Cập nhật thành công", color = StatusAvailable, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            profileViewModel.updateProfile(UpdateProfileRequest(editFullName, editEmail, editPhone))
                        },
                        enabled = editFullName.isNotBlank() && updateState !is ProfileUpdateState.Loading,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                    ) {
                        if (updateState is ProfileUpdateState.Loading)
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        else
                            Text("Lưu thay đổi", fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            // Actions
            OutlinedButton(
                onClick = { showPasswordDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đổi mật khẩu", fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SecondaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = Primary, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = OnBackground)
        }
    }
}

