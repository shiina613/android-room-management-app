package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.dto.request.CreateInvoiceRequest;
import com.kma.lamphoun.room_management.dto.request.MarkPaidRequest;
import com.kma.lamphoun.room_management.dto.response.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    /** Landlord tạo invoice từ contract + meter reading */
    InvoiceResponse create(String landlordUsername, CreateInvoiceRequest request);

    /** Chi tiết invoice — kiểm tra quyền xem */
    InvoiceResponse getById(Long id, String username);

    /** Landlord xem danh sách invoice của mình */
    Page<InvoiceResponse> getByLandlord(String landlordUsername, InvoiceStatus status,
                                        Long contractId, Pageable pageable);

    /** Tenant chỉ xem invoice của chính mình */
    Page<InvoiceResponse> getByTenant(String tenantUsername, InvoiceStatus status, Pageable pageable);

    /** Danh sách invoice theo contract */
    Page<InvoiceResponse> getByContract(Long contractId, String username, Pageable pageable);

    /** Đánh dấu đã thanh toán */
    InvoiceResponse markPaid(Long id, String landlordUsername, MarkPaidRequest request);
}
