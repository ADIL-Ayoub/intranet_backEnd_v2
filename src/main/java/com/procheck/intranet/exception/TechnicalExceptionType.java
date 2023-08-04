package com.procheck.intranet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum TechnicalExceptionType {
    ACCESS_DENIED_EX("20002", HttpStatus.INTERNAL_SERVER_ERROR,"database access exception"),
    DATABASE_ERROR("20003", HttpStatus.INTERNAL_SERVER_ERROR,"access denied to database"),

    HOURLY_ERROR("20004", HttpStatus.INTERNAL_SERVER_ERROR,"une des période n'est pas remplise"),

    COUNTRY_DOES_NOT_EXIST("20005", HttpStatus.INTERNAL_SERVER_ERROR,"country does not exist"),

    PERSONNEL_DOES_NOT_EXIST("20006", HttpStatus.INTERNAL_SERVER_ERROR,"personnel does not exist"),

    TIME_FORMAT_ERROR("20007", HttpStatus.INTERNAL_SERVER_ERROR,"time format IS NOT supported"),

    DATE_FORMAT_ERROR("20008", HttpStatus.BAD_REQUEST,"date format is not supported");

    @Getter
    private String message;
    @Getter
    private HttpStatus status;
    @Getter
    private String code;
    private TechnicalExceptionType(String code, HttpStatus status, String message) {
        this.message = message;
        this.code=code;
        this.status=status;
    }
}
