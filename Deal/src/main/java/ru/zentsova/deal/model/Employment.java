package ru.zentsova.deal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class Employment {

    private EmploymentStatus status;

    @JsonProperty(value = "employer_inn")
    private String employerINN;
    private BigDecimal salary;
    private EmploymentPosition position;

    @JsonProperty(value = "work_experience_total")
    private int workExperienceTotal;

    @JsonProperty(value = "work_experience_current")
    private int workExperienceCurrent;
}
