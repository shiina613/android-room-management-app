package com.kma.lamphoun.roomapp.ui.landlord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kma.lamphoun.roomapp.ui.common.*
import com.kma.lamphoun.roomapp.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceFormScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    prefilledContractId: Long? = null,
    prefilledMeterReadingId: Long? = null,
    viewModel: InvoiceFormViewModel = hiltViewModel()
) {
    val activeContracts by viewModel.activeContracts.collectAsState()
    val selectedContract by viewModel.selectedContract.collectAsState()
    val meterCheckState by viewModel.meterCheckState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val isLoadingContracts by viewModel.isLoadingContracts.collectAsState()
    val prefilledBillingMonth by viewModel.prefilledBillingMonth.collectAsState()
    val latestMeterReading by viewModel.latestMeterReading.collectAsState()

    // Pre-fill from MeterReadingScreen when both params are provided
    LaunchedEffect(prefilledContractId, prefilledMeterReadingId) {
        if (prefilledContractId != null && prefilledMeterReadingId != null) {
            // prefilledContractId carries roomId when navigating from MeterReadingScreen
            // (the INVOICE_CREATE_FROM_METER route passes roomId in the contractId slot)
            viewModel.preloadFromMeterReading(prefilledContractId, prefilledMeterReadingId)
        }
    }

    // Handle pre-fill: once contracts are loaded, select the prefilled contract
    // Only used when navigating directly to INVOICE_CREATE with a contractId (not from meter reading)
    LaunchedEffect(activeContracts, prefilledContractId) {
        if (prefilledContractId != null && prefilledMeterReadingId == null && activeContracts.isNotEmpty()) {
            val contract = activeContracts.find { it.id == prefilledContractId }
            if (contract != null && selectedContract == null) {
                viewModel.selectContract(contract)
            }
        }
    }

    // Navigate back on success
    LaunchedEffect(submitState) {
        if (submitState is InvoiceFormSubmitState.Success) {
            onSaved()
        }
    }

    // Local UI state
    var contractDropdownExpanded by remember { mutableStateOf(false) }
    var billingMonth by remember { mutableStateOf("") }

    // Khi preload từ MeterReadingScreen, set billingMonth từ ViewModel
    LaunchedEffect(prefilledBillingMonth) {
        if (!prefilledBillingMonth.isNullOrBlank() && billingMonth.isBlank()) {
            billingMonth = prefilledBillingMonth!!
        }
    }

    // Inline meter input state (used when MeterCheckState.NotFound)
    var elecPrev by remember { mutableStateOf("") }
    var elecCurr by remember { mutableStateOf("") }
    var waterPrev by remember { mutableStateOf("") }
    var waterCurr by remember { mutableStateOf("") }

    // Khi NotFound, điền sẵn đầu kỳ từ chỉ số kỳ gần nhất
    LaunchedEffect(meterCheckState, latestMeterReading) {
        if (meterCheckState is MeterCheckState.NotFound && latestMeterReading != null) {
            if (elecPrev.isBlank()) elecPrev = latestMeterReading!!.electricCurrent.toLong().toString()
            if (waterPrev.isBlank()) waterPrev = latestMeterReading!!.waterCurrent.toLong().toString()
        }
    }

    // Debounce billingMonth → checkMeterReading
    LaunchedEffect(billingMonth, selectedContract) {
        val contract = selectedContract ?: return@LaunchedEffect
        val month = billingMonth.trim()
        // Validate YYYY-MM format (basic check)
        if (month.matches(Regex("\\d{4}-\\d{2}"))) {
            delay(500)
            viewModel.checkMeterReading(contract.roomId, month)
        }
    }

    // Compute preview totals
    val contract = selectedContract
    val electricUsage: Double
    val waterUsage: Double
    val hasPreviewData: Boolean

    when (val mcs = meterCheckState) {
        is MeterCheckState.Found -> {
            electricUsage = mcs.reading.electricUsage
            waterUsage = mcs.reading.waterUsage
            hasPreviewData = contract != null && billingMonth.isNotBlank()
        }
        is MeterCheckState.NotFound -> {
            val ep = elecPrev.toDoubleOrNull() ?: 0.0
            val ec = elecCurr.toDoubleOrNull() ?: 0.0
            val wp = waterPrev.toDoubleOrNull() ?: 0.0
            val wc = waterCurr.toDoubleOrNull() ?: 0.0
            electricUsage = (ec - ep).coerceAtLeast(0.0)
            waterUsage = (wc - wp).coerceAtLeast(0.0)
            hasPreviewData = contract != null && billingMonth.isNotBlank() &&
                elecCurr.isNotBlank() && waterCurr.isNotBlank() &&
                (elecCurr.toDoubleOrNull() ?: 0.0) >= (elecPrev.toDoubleOrNull() ?: 0.0) &&
                (waterCurr.toDoubleOrNull() ?: 0.0) >= (waterPrev.toDoubleOrNull() ?: 0.0)
        }
        else -> {
            electricUsage = 0.0
            waterUsage = 0.0
            hasPreviewData = false
        }
    }

    // Determine if submit is enabled
    val canSubmit = contract != null &&
        billingMonth.matches(Regex("\\d{4}-\\d{2}")) &&
        (meterCheckState is MeterCheckState.Found || hasPreviewData) &&
        submitState !is InvoiceFormSubmitState.Loading

    Scaffold(
        containerColor = SurfaceContainerLow,
        topBar = { AppTopBar(title = "Tạo hóa đơn", onBack = onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Section 1: Contract selection ──────────────────────────────
            SectionCard(title = "Thông tin hợp đồng") {
                if (isLoadingContracts) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Primary
                    )
                    Spacer(Modifier.height(8.dp))
                }

                ExposedDropdownMenuBox(
                    expanded = contractDropdownExpanded,
                    onExpandedChange = { contractDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = contract?.let { "${it.roomTitle ?: "Phòng #${it.roomId}"} — ${it.tenantName ?: "Người thuê #${it.tenantId}"}" }
                            ?: "Chọn hợp đồng",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hợp đồng *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = contractDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = contractDropdownExpanded,
                        onDismissRequest = { contractDropdownExpanded = false }
                    ) {
                        if (activeContracts.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Không có hợp đồng đang hoạt động") },
                                onClick = { contractDropdownExpanded = false }
                            )
                        }
                        activeContracts.forEach { c ->
                            DropdownMenuItem(
                                text = {
                                    Text("${c.roomTitle ?: "Phòng #${c.roomId}"} — ${c.tenantName ?: "Người thuê #${c.tenantId}"}")
                                },
                                onClick = {
                                    viewModel.selectContract(c)
                                    contractDropdownExpanded = false
                                    // Reset inline meter fields when contract changes
                                    elecPrev = ""
                                    elecCurr = ""
                                    waterPrev = ""
                                    waterCurr = ""
                                }
                            )
                        }
                    }
                }

                // Auto-fill info rows when contract is selected
                if (contract != null) {
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Phòng", contract.roomTitle ?: "Phòng #${contract.roomId}")
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Người thuê", contract.tenantName ?: "Người thuê #${contract.tenantId}")
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Tiền thuê/tháng", contract.monthlyRent.toVnd())
                }
            }

            // ── Section 2: Billing month ───────────────────────────────────
            SectionCard(title = "Kỳ tính tiền") {
                OutlinedTextField(
                    value = billingMonth,
                    onValueChange = { billingMonth = it },
                    label = { Text("Kỳ tính tiền (YYYY-MM) *") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Primary) },
                    placeholder = { Text("VD: 2025-06") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )
            }

            // ── Meter check states ─────────────────────────────────────────

            // Checking: show progress
            if (meterCheckState is MeterCheckState.Checking) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary
                )
            }

            // Found: read-only meter data
            if (meterCheckState is MeterCheckState.Found) {
                val reading = (meterCheckState as MeterCheckState.Found).reading
                SectionCard(title = "Chỉ số điện nước") {
                    // Electric row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "⚡ Điện",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Đầu kỳ: ${reading.electricPrevious.toInt()} kWh",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                "Cuối kỳ: ${reading.electricCurrent.toInt()} kWh",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                "Tiêu thụ: ${reading.electricUsage.toInt()} kWh",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = OnBackground
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "💧 Nước",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Đầu kỳ: ${reading.waterPrevious.toInt()} m³",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                "Cuối kỳ: ${reading.waterCurrent.toInt()} m³",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                "Tiêu thụ: ${reading.waterUsage.toInt()} m³",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = OnBackground
                            )
                        }
                    }
                }
            }

            // NotFound: inline meter input form
            if (meterCheckState is MeterCheckState.NotFound) {
                SectionCard(title = "Nhập chỉ số điện nước") {
                    // Electric inputs
                    Text(
                        "⚡ Điện (kWh)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = elecPrev,
                            onValueChange = { elecPrev = it },
                            label = { Text("Đầu kỳ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary
                            )
                        )
                        OutlinedTextField(
                            value = elecCurr,
                            onValueChange = { elecCurr = it },
                            label = { Text("Cuối kỳ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary
                            )
                        )
                    }
                    if (elecCurr.isNotBlank() && elecPrev.isNotBlank()) {
                        val usage = (elecCurr.toDoubleOrNull() ?: 0.0) - (elecPrev.toDoubleOrNull() ?: 0.0)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tiêu thụ: ${usage.toInt()} kWh",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usage >= 0) StatusAvailable else MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Water inputs
                    Text(
                        "💧 Nước (m³)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = waterPrev,
                            onValueChange = { waterPrev = it },
                            label = { Text("Đầu kỳ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary
                            )
                        )
                        OutlinedTextField(
                            value = waterCurr,
                            onValueChange = { waterCurr = it },
                            label = { Text("Cuối kỳ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary
                            )
                        )
                    }
                    if (waterCurr.isNotBlank() && waterPrev.isNotBlank()) {
                        val usage = (waterCurr.toDoubleOrNull() ?: 0.0) - (waterPrev.toDoubleOrNull() ?: 0.0)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tiêu thụ: ${usage.toInt()} m³",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usage >= 0) StatusAvailable else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // ── Section: Preview total ─────────────────────────────────────
            if (hasPreviewData && contract != null) {
                // We need room price data — it's embedded in the contract's room info.
                // ContractResponse doesn't carry elecPrice/waterPrice/servicePrice directly,
                // so we compute using MeterReadingResponse amounts when Found,
                // or estimate from usage when NotFound (prices not available from contract).
                // When Found, use the pre-computed amounts from the reading.
                val rentAmount = contract.monthlyRent
                val electricAmount: Double
                val waterAmount: Double
                val serviceAmount: Double

                when (val mcs = meterCheckState) {
                    is MeterCheckState.Found -> {
                        electricAmount = mcs.reading.electricAmount
                        waterAmount = mcs.reading.waterAmount
                        // serviceAmount not in MeterReadingResponse — show 0 as placeholder
                        serviceAmount = 0.0
                    }
                    is MeterCheckState.NotFound -> {
                        // Prices not available from ContractResponse — show usage only
                        // The actual amounts will be computed server-side
                        electricAmount = 0.0
                        waterAmount = 0.0
                        serviceAmount = 0.0
                    }
                    else -> {
                        electricAmount = 0.0
                        waterAmount = 0.0
                        serviceAmount = 0.0
                    }
                }

                val totalAmount = rentAmount + electricAmount + waterAmount + serviceAmount

                SectionCard(title = "Preview tổng tiền") {
                    InfoRow("Tiền thuê", rentAmount.toVnd())
                    Spacer(Modifier.height(6.dp))
                    if (electricAmount > 0) {
                        InfoRow(
                            "Tiền điện (${electricUsage.toInt()} kWh)",
                            electricAmount.toVnd()
                        )
                        Spacer(Modifier.height(6.dp))
                    } else if (meterCheckState is MeterCheckState.NotFound && electricUsage > 0) {
                        InfoRow("Tiêu thụ điện", "${electricUsage.toInt()} kWh")
                        Spacer(Modifier.height(6.dp))
                    }
                    if (waterAmount > 0) {
                        InfoRow(
                            "Tiền nước (${waterUsage.toInt()} m³)",
                            waterAmount.toVnd()
                        )
                        Spacer(Modifier.height(6.dp))
                    } else if (meterCheckState is MeterCheckState.NotFound && waterUsage > 0) {
                        InfoRow("Tiêu thụ nước", "${waterUsage.toInt()} m³")
                        Spacer(Modifier.height(6.dp))
                    }
                    if (serviceAmount > 0) {
                        InfoRow("Phí dịch vụ", serviceAmount.toVnd())
                        Spacer(Modifier.height(6.dp))
                    }
                    HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng cộng",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (meterCheckState is MeterCheckState.Found) totalAmount.toVnd()
                            else "${rentAmount.toVnd()} + phí điện/nước",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
            }

            // ── Error messages ─────────────────────────────────────────────
            if (meterCheckState is MeterCheckState.Error) {
                Text(
                    (meterCheckState as MeterCheckState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (submitState is InvoiceFormSubmitState.Error) {
                Text(
                    (submitState as InvoiceFormSubmitState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ── Submit button ──────────────────────────────────────────────
            Button(
                onClick = {
                    val c = contract ?: return@Button
                    when (val mcs = meterCheckState) {
                        is MeterCheckState.Found -> {
                            viewModel.submitInvoice(
                                contractId = c.id,
                                billingMonth = billingMonth,
                                meterReadingId = mcs.reading.id
                            )
                        }
                        is MeterCheckState.NotFound -> {
                            viewModel.submitInvoice(
                                contractId = c.id,
                                billingMonth = billingMonth,
                                inlineMeterData = InlineMeterData(
                                    electricPrevious = elecPrev.toDoubleOrNull() ?: 0.0,
                                    electricCurrent = elecCurr.toDoubleOrNull() ?: 0.0,
                                    waterPrevious = waterPrev.toDoubleOrNull() ?: 0.0,
                                    waterCurrent = waterCurr.toDoubleOrNull() ?: 0.0
                                )
                            )
                        }
                        else -> { /* no-op */ }
                    }
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (submitState is InvoiceFormSubmitState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Tạo hóa đơn", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
