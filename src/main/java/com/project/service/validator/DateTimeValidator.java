package com.project.service.validator;

import com.project.exception.BadRequestException;
import com.project.payload.messages.ErrorMessages;
import org.apache.tomcat.jni.Error;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DateTimeValidator {
    public boolean checkTime(LocalTime start, LocalTime stop) {
        // start stoptan sonra ise veya start stop a eşitse
        return start.isAfter(stop) || start.equals(stop);


    }

    public void checkTimeWithException(LocalTime start, LocalTime stop) {
        if (checkTime(start, stop))
            throw new BadRequestException(ErrorMessages.TIME_NOT_VALID);


    }
}
