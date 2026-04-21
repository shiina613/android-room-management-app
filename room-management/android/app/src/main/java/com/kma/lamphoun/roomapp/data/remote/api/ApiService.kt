package com.kma.lamphoun.roomapp.data.remote.api

import com.kma.lamphoun.roomapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @GET("api/auth/me")
    suspend fun getMe(): Response<ApiResponse<UserResponse>>

    @GET("api/users/me")
    suspend fun getMyProfile(): Response<ApiResponse<UserResponse>>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserResponse>>

    @PUT("api/users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Void>>

    // Rooms
    @GET("api/rooms")
    suspend fun getRooms(
        @Query("status") status: String? = null,
        @Query("category") category: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<RoomResponse>>>

    @GET("api/rooms/{id}")
    suspend fun getRoomById(@Path("id") id: Long): Response<ApiResponse<RoomResponse>>

    @POST("api/rooms")
    suspend fun createRoom(@Body request: RoomRequest): Response<ApiResponse<RoomResponse>>

    @PUT("api/rooms/{id}")
    suspend fun updateRoom(@Path("id") id: Long, @Body request: RoomRequest): Response<ApiResponse<RoomResponse>>

    @PATCH("api/rooms/{id}/status")
    suspend fun updateRoomStatus(@Path("id") id: Long, @Body request: UpdateRoomStatusRequest): Response<ApiResponse<RoomResponse>>

    @DELETE("api/rooms/{id}")
    suspend fun deleteRoom(@Path("id") id: Long): Response<ApiResponse<Void>>

    @Multipart
    @POST("api/rooms/{id}/image")
    suspend fun uploadRoomImage(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<RoomResponse>>

    // Tenants
    @GET("api/tenants")
    suspend fun getTenants(@Query("page") page: Int = 0, @Query("size") size: Int = 10): Response<ApiResponse<PageResponse<TenantResponse>>>

    @GET("api/tenants/{id}")
    suspend fun getTenantById(@Path("id") id: Long): Response<ApiResponse<TenantResponse>>

    @POST("api/tenants")
    suspend fun createTenant(@Body request: CreateTenantRequest): Response<ApiResponse<TenantResponse>>

    // Contracts
    @GET("api/contracts/my")
    suspend fun getMyContracts(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<ContractResponse>>>

    @GET("api/contracts")
    suspend fun getContracts(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<ContractResponse>>>

    @GET("api/contracts/{id}")
    suspend fun getContractById(@Path("id") id: Long): Response<ApiResponse<ContractResponse>>

    @POST("api/contracts")
    suspend fun createContract(@Body request: CreateContractRequest): Response<ApiResponse<ContractResponse>>

    @PATCH("api/contracts/{id}/terminate")
    suspend fun terminateContract(@Path("id") id: Long, @Body request: TerminateContractRequest): Response<ApiResponse<ContractResponse>>

    @PATCH("api/contracts/{id}/extend")
    suspend fun extendContract(@Path("id") id: Long, @Body request: ExtendContractRequest): Response<ApiResponse<ContractResponse>>

    // Meter Readings
    @POST("api/meter-readings")
    suspend fun recordMeterReading(@Body request: MeterReadingRequest): Response<ApiResponse<MeterReadingResponse>>

    @GET("api/meter-readings/rooms/{roomId}")
    suspend fun getMeterReadingHistory(@Path("roomId") roomId: Long, @Query("page") page: Int = 0): Response<ApiResponse<PageResponse<MeterReadingResponse>>>

    @GET("api/meter-readings/rooms/{roomId}/month/{billingMonth}")
    suspend fun getMeterReadingByMonth(@Path("roomId") roomId: Long, @Path("billingMonth") billingMonth: String): Response<ApiResponse<MeterReadingResponse>>

    // Invoices
    @POST("api/invoices")
    suspend fun createInvoice(@Body request: CreateInvoiceRequest): Response<ApiResponse<InvoiceResponse>>

    @GET("api/invoices/{id}")
    suspend fun getInvoiceById(@Path("id") id: Long): Response<ApiResponse<InvoiceResponse>>

    @GET("api/invoices/my")
    suspend fun getMyInvoices(@Query("status") status: String? = null, @Query("page") page: Int = 0): Response<ApiResponse<PageResponse<InvoiceResponse>>>

    @GET("api/invoices/tenant/me")
    suspend fun getMyTenantInvoices(@Query("status") status: String? = null, @Query("page") page: Int = 0): Response<ApiResponse<PageResponse<InvoiceResponse>>>

    @GET("api/invoices/contracts/{contractId}")
    suspend fun getInvoicesByContract(@Path("contractId") contractId: Long, @Query("page") page: Int = 0): Response<ApiResponse<PageResponse<InvoiceResponse>>>

    // Payments
    @POST("api/payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<ApiResponse<PaymentResponse>>

    @GET("api/payments/invoices/{invoiceId}")
    suspend fun getPaymentsByInvoice(@Path("invoiceId") invoiceId: Long, @Query("page") page: Int = 0): Response<ApiResponse<PageResponse<PaymentResponse>>>

    @GET("api/payments/my")
    suspend fun getMyPayments(@Query("page") page: Int = 0): Response<ApiResponse<PageResponse<PaymentResponse>>>

    @GET("api/payments/tenant/me")
    suspend fun getMyTenantPayments(@Query("page") page: Int = 0): Response<ApiResponse<PageResponse<PaymentResponse>>>

    // Reports
    @GET("api/reports/dashboard")
    suspend fun getDashboard(): Response<ApiResponse<DashboardResponse>>

    @GET("api/reports/revenue/monthly")
    suspend fun getMonthlyRevenue(@Query("year") year: Int, @Query("month") month: Int): Response<ApiResponse<RevenueReportResponse>>

    @GET("api/reports/revenue/yearly")
    suspend fun getYearlyRevenue(@Query("year") year: Int): Response<ApiResponse<RevenueReportResponse>>

    @GET("api/reports/debt")
    suspend fun getDebtReport(@Query("billingMonth") billingMonth: String? = null): Response<ApiResponse<DebtReportResponse>>

    @GET("api/reports/rooms")
    suspend fun getRoomStatusReport(): Response<ApiResponse<RoomStatusReportResponse>>

    // Notifications
    @GET("api/notifications")
    suspend fun getNotifications(@Query("unreadOnly") unreadOnly: Boolean? = null, @Query("page") page: Int = 0): Response<ApiResponse<PageResponse<NotificationResponse>>>

    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadCountResponse>>

    @PATCH("api/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): Response<ApiResponse<Void>>

    @PATCH("api/notifications/read-all")
    suspend fun markAllRead(): Response<ApiResponse<Void>>

    // Invoices - mark paid
    @PATCH("api/invoices/{id}/paid")
    suspend fun markInvoicePaid(@Path("id") id: Long, @Body request: MarkPaidRequest): Response<ApiResponse<InvoiceResponse>>
}