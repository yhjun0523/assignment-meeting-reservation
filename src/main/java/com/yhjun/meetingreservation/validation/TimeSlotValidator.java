package com.yhjun.meetingreservation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TimeSlotValidator implements ConstraintValidator<ValidTimeSlot, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int minute = value.getMinute();
        return minute == 0 || minute == 30;
    }
}