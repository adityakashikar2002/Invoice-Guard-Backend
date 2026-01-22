package com.springboot.learning.invoiceguard.dto;


import org.jspecify.annotations.NonNull;

public class ErrorResponse {
    @NonNull
    private String statusCode;

    @NonNull
    private String errorMsg;

    public ErrorResponse(String statusCode, String errorMsg) {
        this.statusCode = statusCode;
        this.errorMsg = errorMsg;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
