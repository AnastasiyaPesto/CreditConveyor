package ru.zentsova.Dossier.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class EmailMsgTemplate {

    private String from;
    private String to;
    private String subject;
    private String body;

    private LocalDateTime date;
}