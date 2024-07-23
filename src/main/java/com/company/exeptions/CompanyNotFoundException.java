package com.company.exeptions;

public class CompanyNotFoundException extends RuntimeException{

    public CompanyNotFoundException(String message) {
        super(message);
    }
}
