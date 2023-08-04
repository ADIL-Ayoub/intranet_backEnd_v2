package com.procheck.intranet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum AccesExceptionType {
    ACCESS_DENIED_EX("20002", HttpStatus.INTERNAL_SERVER_ERROR,"database access exception"),

    TOKEN_NOT_VALID("20003", HttpStatus.UNAUTHORIZED,"token is not valid"),

    ID_PERSONNEL_NOT_VALID("20004",HttpStatus.INTERNAL_SERVER_ERROR,"personnel does not exist"),

    ID_WEEK_NOT_VALID("20005",HttpStatus.INTERNAL_SERVER_ERROR,"week is not valid");
    @Getter
    private String message;
    @Getter
    private HttpStatus status;
    @Getter
    private String code;
    private AccesExceptionType(String message, HttpStatus status, String code) {
        this.message = message;
        this.code = code;
        this.status = status;

    }

}
