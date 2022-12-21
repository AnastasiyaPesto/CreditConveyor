package ru.zentsova.deal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE"),
    NON_BINARY("NOT BINARY");

    private String value;
}
