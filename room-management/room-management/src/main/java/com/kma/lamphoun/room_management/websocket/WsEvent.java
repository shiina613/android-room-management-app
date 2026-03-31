package com.kma.lamphoun.room_management.websocket;

/**
 * Các loại event WebSocket — Android dùng để route đến màn hình tương ứng.
 */
public final class WsEvent {
    private WsEvent() {}

    // Notification
    public static final String NOTIFICATION        = "NOTIFICATION";

    // Invoice
    public static final String INVOICE_CREATED     = "INVOICE_CREATED";
    public static final String INVOICE_PAID        = "INVOICE_PAID";
    public static final String INVOICE_OVERDUE     = "INVOICE_OVERDUE";

    // Room
    public static final String ROOM_STATUS_CHANGED = "ROOM_STATUS_CHANGED";

    // Contract
    public static final String CONTRACT_CREATED    = "CONTRACT_CREATED";
    public static final String CONTRACT_TERMINATED = "CONTRACT_TERMINATED";
    public static final String CONTRACT_EXPIRING   = "CONTRACT_EXPIRING";

    // Payment
    public static final String PAYMENT_RECEIVED    = "PAYMENT_RECEIVED";
}
