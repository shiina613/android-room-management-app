package com.kma.lamphoun.room_management.websocket;

import com.kma.lamphoun.room_management.dto.response.ContractResponse;
import com.kma.lamphoun.room_management.dto.response.InvoiceResponse;
import com.kma.lamphoun.room_management.dto.response.RoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service trung tâm cho tất cả WebSocket push.
 * Các service khác inject và gọi sau khi hoàn thành business logic.
 *
 * Destinations:
 *   /user/{username}/queue/events  → per-user (tenant nhận invoice, landlord nhận room update)
 *   /topic/rooms                   → broadcast trạng thái phòng (ai subscribe đều nhận)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketEventService {

    private final SimpMessagingTemplate messaging;

    // ── Destinations ──────────────────────────────────────────────────────────

    /** Per-user queue — Android subscribe: /user/queue/events */
    private static final String USER_QUEUE = "/queue/events";

    /** Broadcast room status — Android subscribe: /topic/rooms */
    private static final String TOPIC_ROOMS = "/topic/rooms";

    /** Broadcast contract events — Android subscribe: /topic/contracts */
    private static final String TOPIC_CONTRACTS = "/topic/contracts";

    // ── Invoice ───────────────────────────────────────────────────────────────

    /**
     * Push invoice mới đến tenant.
     * Gọi sau InvoiceServiceImpl.create()
     */
    public void pushInvoiceCreated(String tenantUsername, InvoiceResponse invoice) {
        pushToUser(tenantUsername, WsEvent.INVOICE_CREATED, invoice);
        log.debug("WS push INVOICE_CREATED → user:{} invoiceId:{}", tenantUsername, invoice.getId());
    }

    /**
     * Push invoice đã thanh toán đến tenant.
     * Gọi sau InvoiceServiceImpl.markPaid() hoặc PaymentServiceImpl khi đủ tiền
     */
    public void pushInvoicePaid(String tenantUsername, InvoiceResponse invoice) {
        pushToUser(tenantUsername, WsEvent.INVOICE_PAID, invoice);
        log.debug("WS push INVOICE_PAID → user:{} invoiceId:{}", tenantUsername, invoice.getId());
    }

    // ── Room ──────────────────────────────────────────────────────────────────

    /**
     * Push cập nhật trạng thái phòng đến landlord + broadcast.
     * Gọi sau RoomServiceImpl.updateStatus() và ContractServiceImpl khi tạo/terminate contract
     */
    public void pushRoomStatusChanged(String landlordUsername, RoomResponse room) {
        // Push riêng cho landlord
        pushToUser(landlordUsername, WsEvent.ROOM_STATUS_CHANGED, room);
        // Broadcast cho tất cả (tenant đang xem danh sách phòng cũng nhận được)
        broadcast(TOPIC_ROOMS, WsEvent.ROOM_STATUS_CHANGED, room);
        log.debug("WS push ROOM_STATUS_CHANGED → roomId:{} status:{}", room.getId(), room.getStatus());
    }

    // ── Contract ──────────────────────────────────────────────────────────────

    /**
     * Push hợp đồng mới đến tenant.
     * Gọi sau ContractServiceImpl.create()
     */
    public void pushContractCreated(String tenantUsername, String landlordUsername,
                                    ContractResponse contract) {
        pushToUser(tenantUsername, WsEvent.CONTRACT_CREATED, contract);
        pushToUser(landlordUsername, WsEvent.CONTRACT_CREATED, contract);
        log.debug("WS push CONTRACT_CREATED → contractId:{}", contract.getId());
    }

    /**
     * Push terminate đến tenant.
     * Gọi sau ContractServiceImpl.terminate()
     */
    public void pushContractTerminated(String tenantUsername, ContractResponse contract) {
        pushToUser(tenantUsername, WsEvent.CONTRACT_TERMINATED, contract);
        log.debug("WS push CONTRACT_TERMINATED → contractId:{}", contract.getId());
    }

    // ── Payment ───────────────────────────────────────────────────────────────

    /**
     * Push xác nhận thanh toán đến tenant.
     * Gọi sau PaymentServiceImpl.create()
     */
    public void pushPaymentReceived(String tenantUsername, Object paymentResponse) {
        pushToUser(tenantUsername, WsEvent.PAYMENT_RECEIVED, paymentResponse);
        log.debug("WS push PAYMENT_RECEIVED → user:{}", tenantUsername);
    }

    // ── Core helpers ──────────────────────────────────────────────────────────

    /**
     * Push đến một user cụ thể.
     * SimpMessagingTemplate.convertAndSendToUser tự resolve:
     *   username + "/queue/events" → /user/{username}/queue/events
     */
    private void pushToUser(String username, String event, Object data) {
        try {
            messaging.convertAndSendToUser(username, USER_QUEUE,
                    WsPayload.builder().event(event).data(data).build());
        } catch (Exception e) {
            // User offline — không fail transaction
            log.warn("WS push failed [{}] → user:{}: {}", event, username, e.getMessage());
        }
    }

    /**
     * Broadcast đến tất cả subscriber của một topic.
     */
    private void broadcast(String destination, String event, Object data) {
        try {
            messaging.convertAndSend(destination,
                    WsPayload.builder().event(event).data(data).build());
        } catch (Exception e) {
            log.warn("WS broadcast failed [{}] → {}: {}", event, destination, e.getMessage());
        }
    }
}
