package com.procheck.intranet.exception;

public class TechnicalException extends ExceptionResponse{
    public TechnicalException(TechnicalExceptionType TechnicalExceptionType){
        this.code=TechnicalExceptionType.getCode();
        this.message=TechnicalExceptionType.getMessage();
        this.status=TechnicalExceptionType.getStatus();
    }
}
