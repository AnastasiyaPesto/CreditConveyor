package ru.zentsova.deal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "applied_offer")
public class AppliedOffer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "application_id", unique = true, nullable = false)
    private long applicationId;

    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "term", nullable = false)
    private int term;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "is_insurance_enabled")
    private boolean isInsuranceEnabled;

    @Column(name = "is_salary_client")
    private boolean isSalaryClient;

    @OneToOne(mappedBy = "appliedOffer")
    private Application application;
}