package ru.zentsova.deal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode
public class StatusHistory {

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime time;

    @JsonProperty(value = "change_type")
    private ChangeType changeType;
}