package BankSdNd.example.BsDnD.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankUserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "transaction_password")
    private String transactionPassword;

    @Column(name = "income", precision = 15, scale = 2)
    private BigDecimal income;
}
