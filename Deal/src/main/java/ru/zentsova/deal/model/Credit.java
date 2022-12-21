package ru.zentsova.Deal.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import liquibase.pro.packaged.B;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "credit")
public class Credit {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "term", nullable = false)
    private int term;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "psk", nullable = false)
    private BigDecimal psk;

    @Type(type = "jsonb")
    @Column(name = "payment_schedule")
    private String paymentSchedule;

    @Column(name = "insurance_enable")
    private boolean insuranceEnable;

    @Column(name = "salary_client")
    private boolean salaryClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status")
    private CreditStatus creditStatus;

    @OneToOne(mappedBy = "credit")
    private Application application;
}
