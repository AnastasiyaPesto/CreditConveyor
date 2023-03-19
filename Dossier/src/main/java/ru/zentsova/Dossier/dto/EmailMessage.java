package ru.zentsova.Dossier.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailMessage {
    private String address;
    private Theme theme;
    private Long applicationId;
}