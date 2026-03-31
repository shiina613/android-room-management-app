package com.kma.lamphoun.room_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomStatusReportResponse {

    private long totalRooms;
    private long available;
    private long occupied;
    private long maintenance;

    /** Tỷ lệ lấp đầy (%) */
    private double occupancyRate;

    /** Chi tiết từng phòng đang OCCUPIED */
    private List<OccupiedRoomDetail> occupiedRooms;

    @Data
    @Builder
    public static class OccupiedRoomDetail {
        private Long roomId;
        private String roomTitle;
        private String address;
        private String tenantName;
        private String tenantPhone;
        private String contractStart;
        private String contractEnd;
    }
}
