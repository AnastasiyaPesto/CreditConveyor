package ru.zentsova.Deal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmploymentStatus {
    EMPLOYED("EMPLOYED"),
    UNEMPLOYED("UNEMPLOYED"),
    SELF_EMPLOYED("SELF-EMPLOYED"),
    BUSINESS_OWNER("BUSINESS OWNER");

    private String value;
}
