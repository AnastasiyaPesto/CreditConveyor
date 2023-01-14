package ru.zentsova.deal.exceptions;

public class EntityNotExistException extends RuntimeException {

    public EntityNotExistException(String message) {
        super(message);
    }
}