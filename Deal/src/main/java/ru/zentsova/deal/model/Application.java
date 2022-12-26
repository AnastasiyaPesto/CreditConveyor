package ru.zentsova.deal.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "application")
public class Application {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ApplicationStatus status;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Type(type = "jsonb")
    @Column(name = "applied_offer")
    private String applied_offer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;

    @Column(name = "ses_code", length = 10)
    private String sesCode;

    @Type(type = "jsonb")
    @Column(name = "status_history")
    private String statusHistory;
}
