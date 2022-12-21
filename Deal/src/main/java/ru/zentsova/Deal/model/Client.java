package ru.zentsova.Deal.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "client")
public class Client {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = false, length = 50)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount", nullable = false)
    private int dependentAmount;

    @Type(type = "jsonb")
    @Column(name = "passport_id", nullable = false)
    private String passportId;

    @Type(type = "jsonb")
    @Column(name = "employment_id", nullable = false)
    private String employmentId;

    @Column(name = "account", nullable = false, length = 50)
    private String account;

    @OneToOne(mappedBy = "client")
    private Application application;

}
