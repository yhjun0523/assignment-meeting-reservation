package com.yhjun.meetingreservation.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotValidatorTest {

    private final TimeSlotValidator validator = new TimeSlotValidator();

    @Test
    void nullIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void hourOrHalfHour_valid() {
        assertTrue(validator.isValid(LocalDateTime.of(2025, 5, 13, 9, 0), null));
        assertTrue(validator.isValid(LocalDateTime.of(2025, 5, 13, 9, 30), null));
    }

    @Test
    void otherMinutes_invalid() {
        assertFalse(validator.isValid(LocalDateTime.of(2025, 5, 13, 9, 15), null));
        assertFalse(validator.isValid(LocalDateTime.of(2025, 5, 13, 9, 45), null));
    }
}
