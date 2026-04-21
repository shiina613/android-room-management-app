package com.kma.lamphoun.roomapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kma.lamphoun.roomapp.ui.auth.*
import com.kma.lamphoun.roomapp.ui.landlord.*
import com.kma.lamphoun.roomapp.ui.shared.ProfileScreen
import com.kma.lamphoun.roomapp.ui.tenant.*

@Composable
fun RoomNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NavRoutes.SPLASH) {

        // ── Splash ──────────────────────────────────────────────────────────
        composable(NavRoutes.SPLASH) {
            SplashScreen(onNavigate = { role ->
                val dest = when (role) {
                    "ROLE_LANDLORD" -> NavRoutes.LANDLORD_DASHBOARD
                    "ROLE_TENANT" -> NavRoutes.TENANT_DASHBOARD
                    else -> NavRoutes.LOGIN
                }
                navController.navigate(dest) { popUpTo(NavRoutes.SPLASH) { inclusive = true } }
            })
        }

        // ── Auth ─────────────────────────────────────────────────────────────
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = if (role == "ROLE_LANDLORD") NavRoutes.LANDLORD_DASHBOARD else NavRoutes.TENANT_DASHBOARD
                    navController.navigate(dest) { popUpTo(NavRoutes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(NavRoutes.LOGIN) { popUpTo(NavRoutes.REGISTER) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ── Landlord ─────────────────────────────────────────────────────────
        composable(NavRoutes.LANDLORD_DASHBOARD) {
            DashboardScreen(
                onNavigateToRooms = { navController.navigate(NavRoutes.ROOM_LIST) },
                onNavigateToContracts = { navController.navigate(NavRoutes.CONTRACT_LIST) },
                onNavigateToInvoices = { navController.navigate(NavRoutes.INVOICE_LIST) },
                onNavigateToReports = { navController.navigate(NavRoutes.REPORT) },
                onNavigateToNotifications = { navController.navigate(NavRoutes.NOTIFICATIONS) },
                onNavigateToProfile = { navController.navigate(NavRoutes.PROFILE) }
            )
        }
        composable(NavRoutes.ROOM_LIST) {
            RoomListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate(NavRoutes.ROOM_CREATE) },
                onNavigateToDetail = { navController.navigate(NavRoutes.roomDetail(it)) },
                onNavigateToMeterReading = { navController.navigate(NavRoutes.meterReading(it)) }
            )
        }
        composable(NavRoutes.ROOM_CREATE) {
            RoomFormScreen(onNavigateBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
        }
        composable(
            NavRoutes.ROOM_DETAIL,
            arguments = listOf(navArgument("roomId") { type = NavType.LongType })
        ) { backStack ->
            val roomId = backStack.arguments?.getLong("roomId") ?: return@composable
            RoomFormScreen(roomId = roomId, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
        }
        composable(NavRoutes.CONTRACT_LIST) {
            ContractListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate(NavRoutes.CONTRACT_CREATE) },
                onNavigateToDetail = { navController.navigate(NavRoutes.contractDetail(it)) }
            )
        }
        composable(NavRoutes.CONTRACT_CREATE) {
            ContractFormScreen(onNavigateBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
        }
        composable(
            NavRoutes.CONTRACT_DETAIL,
            arguments = listOf(navArgument("contractId") { type = NavType.LongType })
        ) { backStack ->
            val contractId = backStack.arguments?.getLong("contractId") ?: return@composable
            // Reuse ContractFormScreen for detail/edit — or create ContractDetailScreen later
            ContractFormScreen(onNavigateBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
        }
        composable(
            NavRoutes.METER_READING,
            arguments = listOf(navArgument("roomId") { type = NavType.LongType })
        ) { backStack ->
            val roomId = backStack.arguments?.getLong("roomId") ?: 1L
            MeterReadingScreen(roomId = roomId, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
        }
        composable(NavRoutes.INVOICE_LIST) {
            InvoiceListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate(NavRoutes.INVOICE_CREATE) },
                onNavigateToDetail = { navController.navigate(NavRoutes.invoiceDetail(it)) }
            )
        }
        composable(NavRoutes.INVOICE_CREATE) {
            // Reuse InvoiceListScreen FAB flow — placeholder
            InvoiceListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = {},
                onNavigateToDetail = { navController.navigate(NavRoutes.invoiceDetail(it)) }
            )
        }
        composable(
            NavRoutes.INVOICE_DETAIL,
            arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
        ) { backStack ->
            val invoiceId = backStack.arguments?.getLong("invoiceId") ?: return@composable
            InvoiceDetailScreen(
                invoiceId = invoiceId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPayment = { navController.navigate(NavRoutes.paymentCreate(it)) }
            )
        }
        composable(
            NavRoutes.PAYMENT_CREATE,
            arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
        ) { backStack ->
            val invoiceId = backStack.arguments?.getLong("invoiceId") ?: return@composable
            PaymentHistoryScreen(
                invoiceId = invoiceId,
                onNavigateBack = { navController.popBackStack() },
                onCreatePayment = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.REPORT) {
            ReportScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavRoutes.NOTIFICATIONS) {
            NotificationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavRoutes.PROFILE) {
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                authViewModel = authViewModel
            )
        }

        // ── Tenant ───────────────────────────────────────────────────────────
        composable(NavRoutes.TENANT_DASHBOARD) {
            TenantHomeScreen(
                onNavigateToMyRoom = { navController.navigate(NavRoutes.TENANT_MY_ROOM) },
                onNavigateToMyContract = { navController.navigate(NavRoutes.TENANT_MY_CONTRACT) },
                onNavigateToMyInvoices = { navController.navigate(NavRoutes.TENANT_MY_INVOICES) },
                onNavigateToNotifications = { navController.navigate(NavRoutes.TENANT_NOTIFICATIONS) },
                onNavigateToProfile = { navController.navigate(NavRoutes.PROFILE) }
            )
        }
        composable(NavRoutes.TENANT_MY_ROOM) {
            MyRoomScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavRoutes.TENANT_MY_CONTRACT) {
            MyContractScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavRoutes.TENANT_MY_INVOICES) {
            TenantInvoiceListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { navController.navigate(NavRoutes.tenantInvoiceDetail(it)) }
            )
        }
        composable(
            NavRoutes.TENANT_INVOICE_DETAIL,
            arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
        ) { backStack ->
            val invoiceId = backStack.arguments?.getLong("invoiceId") ?: return@composable
            TenantInvoiceDetailScreen(invoiceId = invoiceId, onNavigateBack = { navController.popBackStack() })
        }
        composable(NavRoutes.TENANT_NOTIFICATIONS) {
            TenantNotificationScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

