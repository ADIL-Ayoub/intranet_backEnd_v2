package com.procheck.intranet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse extends Exception{
    protected String message;
    protected HttpStatus status;
    protected String code;

}
