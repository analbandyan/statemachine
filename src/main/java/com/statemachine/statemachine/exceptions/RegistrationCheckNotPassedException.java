package com.statemachine.statemachine.exceptions;

public class RegistrationCheckNotPassedException extends RuntimeException {

    public RegistrationCheckNotPassedException(String message) {
        super(message);
    }

}
