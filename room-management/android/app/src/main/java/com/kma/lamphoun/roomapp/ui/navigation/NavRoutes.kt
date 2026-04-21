package com.kma.lamphoun.roomapp.ui.navigation

object NavRoutes {
    // Splash
    const val SPLASH = "splash"

    // Auth
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Landlord
    const val LANDLORD_DASHBOARD = "landlord/dashboard"
    const val ROOM_LIST = "landlord/rooms"
    const val ROOM_DETAIL = "landlord/rooms/{roomId}"
    const val ROOM_CREATE = "landlord/rooms/create"
    const val TENANT_LIST = "landlord/tenants"
    const val TENANT_DETAIL = "landlord/tenants/{tenantId}"
    const val CONTRACT_LIST = "landlord/contracts"
    const val CONTRACT_DETAIL = "landlord/contracts/{contractId}"
    const val CONTRACT_CREATE = "landlord/contracts/create"
    const val METER_READING = "landlord/meter-readings/{roomId}"
    const val INVOICE_LIST = "landlord/invoices"
    const val INVOICE_DETAIL = "landlord/invoices/{invoiceId}"
    const val INVOICE_CREATE = "landlord/invoices/create"
    const val PAYMENT_CREATE = "landlord/payments/create/{invoiceId}"
    const val REPORT = "landlord/reports"

    // Tenant
    const val TENANT_DASHBOARD = "tenant/dashboard"
    const val TENANT_MY_INVOICES = "tenant/invoices"
    const val TENANT_INVOICE_DETAIL = "tenant/invoices/{invoiceId}"
    const val TENANT_MY_PAYMENTS = "tenant/payments"
    const val TENANT_MY_CONTRACT = "tenant/contract"
    const val TENANT_MY_ROOM = "tenant/room"
    const val TENANT_NOTIFICATIONS = "tenant/notifications"

    // Shared
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    fun roomDetail(roomId: Long) = "landlord/rooms/$roomId"
    fun tenantDetail(tenantId: Long) = "landlord/tenants/$tenantId"
    fun contractDetail(contractId: Long) = "landlord/contracts/$contractId"
    fun meterReading(roomId: Long) = "landlord/meter-readings/$roomId"
    fun invoiceDetail(invoiceId: Long) = "landlord/invoices/$invoiceId"
    fun paymentCreate(invoiceId: Long) = "landlord/payments/create/$invoiceId"
    fun tenantInvoiceDetail(invoiceId: Long) = "tenant/invoices/$invoiceId"
}

