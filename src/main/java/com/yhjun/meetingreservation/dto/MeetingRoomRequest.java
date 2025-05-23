package com.yhjun.meetingreservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MeetingRoomRequest(
        @Schema(example = "회의실 A")
        @NotBlank String name) {
}