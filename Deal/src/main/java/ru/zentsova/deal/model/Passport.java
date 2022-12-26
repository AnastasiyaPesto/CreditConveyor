package ru.zentsova.deal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class Passport {

    @JsonProperty(value = "series")
    private String passportSeries;

    @JsonProperty(value = "number")
    private String passportNumber;

    @JsonProperty(value = "issue_branch")
    private String issueBranch;

    @JsonProperty(value = "issue_date")
    private LocalDate issueDate;
}
