package ru.zentsova.deal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AppliedOffer {

    @JsonProperty(value = "application_id")
    private long applicationId;

    @JsonProperty(value = "requested_amount")
    private BigDecimal requestedAmount;

    @JsonProperty(value = "total_amount")
    private BigDecimal totalAmount;

    private int term;

    @JsonProperty(value = "monthly_payment")
    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    @JsonProperty(value = "is_insurance_enabled")
    private boolean isInsuranceEnabled;

    @JsonProperty(value = "is_salary_client")
    private boolean isSalaryClient;
}