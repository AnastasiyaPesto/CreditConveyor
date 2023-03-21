package ru.zentsova.Dossier.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EmailMessageDto {
    private String address;
    private Theme theme;
    private Long applicationId;
}