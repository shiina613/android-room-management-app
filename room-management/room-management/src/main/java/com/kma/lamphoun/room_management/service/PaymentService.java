package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.dto.request.CreatePaymentRequest;
import com.kma.lamphoun.room_management.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    /** Landlord ghi nhận thanh toán — tự động PAID khi đủ tiền */
    PaymentResponse create(String landlordUsername, CreatePaymentRequest request);

    /** Chi tiết một payment */
    PaymentResponse getById(Long id, String username);

    /** Danh sách payment theo invoice */
    Page<PaymentResponse> getByInvoice(Long invoiceId, String username, Pageable pageable);

    /** Tenant xem lịch sử thanh toán của mình */
    Page<PaymentResponse> getByTenant(String tenantUsername, Pageable pageable);

    /** Landlord xem lịch sử thanh toán của mình */
    Page<PaymentResponse> getByLandlord(String landlordUsername, Pageable pageable);
}
