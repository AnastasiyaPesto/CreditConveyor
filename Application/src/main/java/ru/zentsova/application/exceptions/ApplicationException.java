package ru.zentsova.application.exceptions;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}