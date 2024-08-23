package com.company.exception;

public class InvoiceProductNotFoundException extends RuntimeException{

    public InvoiceProductNotFoundException() {
    }

    public InvoiceProductNotFoundException(String message) {
        super(message);
    }

}
