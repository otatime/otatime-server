package com.otatime_server.post.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, PostSearchRequest> {

    @Override
    public boolean isValid(PostSearchRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value.start() == null || value.end() == null) {
            return true; // 둘 중 하나라도 없으면 비교 안함
        }

        try {
            LocalDate start = LocalDate.parse(value.start());
            LocalDate end = LocalDate.parse(value.end());
            return !start.isAfter(end);
        } catch (DateTimeParseException e) {
            return true; // 날짜 형식은 @Pattern이 이미 처리
        }
    }
}
