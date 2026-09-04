package com.historicalrpg.research.exception;

public class ResearchItemCodeMismatchException extends RuntimeException {

    public ResearchItemCodeMismatchException(String requestCode, String itemCode) {
        super("Path code and request body code must match");
    }
}
