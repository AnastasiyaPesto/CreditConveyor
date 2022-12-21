package ru.zentsova.Deal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaritalStatus {
    MARRIED("MARRIED"),
    DIVORCED("DIVORCED"),
    SINGLE("SINGLE"),
    WIDOWER("WIDOWER");

    private String value;
}
