package com.yhjun.meetingreservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @Schema(example = "양현준")
        @NotBlank String username,

        @Schema(example = "yhjun@example.com")
        @Email String email
) {
}