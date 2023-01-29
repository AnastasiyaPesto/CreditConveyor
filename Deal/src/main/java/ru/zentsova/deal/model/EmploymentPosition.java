package ru.zentsova.deal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmploymentPosition {
    TOP_LEVEL_MANAGER("TOP LEVEL MANAGER"),
    MANAGER("MANAGER"),
    DEVELOPER("DEVELOPER"),
    TRAINEE("TRAINEE");

    private String value;
}
