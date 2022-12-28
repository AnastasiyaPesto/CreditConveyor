package ru.zentsova.deal.feign.exceptions;

public class EntityNotExistException extends RuntimeException {

    public EntityNotExistException(String message) {
        super(message);
    }
}