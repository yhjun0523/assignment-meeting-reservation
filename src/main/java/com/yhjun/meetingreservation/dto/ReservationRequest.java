package com.yhjun.meetingreservation.dto;

import com.yhjun.meetingreservation.validation.ValidTimeSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @Schema(example = "1")
        @NotNull Long userId,

        @Schema(example = "1")
        @NotNull Long meetingRoomId,

        @Schema(example = "2025-05-13T10:00:00")
        @NotNull
        @ValidTimeSlot
        LocalDateTime startTime,

        @Schema(example = "2025-05-13T11:00:00")
        @NotNull
        @ValidTimeSlot
        LocalDateTime endTime
) {
}