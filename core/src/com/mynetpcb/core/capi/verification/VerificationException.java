package com.mynetpcb.core.capi.verification;


public class VerificationException extends Exception {

    public VerificationException(Throwable throwable) {
        super(throwable);
    }

    public VerificationException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public VerificationException(String string) {
        super(string);
    }

    public VerificationException() {
        super();
    }
}

