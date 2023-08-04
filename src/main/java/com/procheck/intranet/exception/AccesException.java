package com.procheck.intranet.exception;

public class AccesException extends ExceptionResponse{
    public AccesException(AccesExceptionType TechnicalExceptionType){
        this.code=TechnicalExceptionType.getCode();
        this.message=TechnicalExceptionType.getMessage();
        this.status=TechnicalExceptionType.getStatus();
    }
}
