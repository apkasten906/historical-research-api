package com.historicalrpg.research.exception;

public class ResearchItemNotFoundException extends RuntimeException {

    public ResearchItemNotFoundException(String code) {
        super("Research item not found: " + code);
    }
}
