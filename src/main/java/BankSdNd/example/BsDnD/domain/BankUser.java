package BankSdNd.example.BsDnD.domain;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;


/**
 * Represents a user (customer) of the bank.
 * <p>
 * This is a JPA entity that maps to the {@code bank_user} table in the database.
 * It contains the user's personal identification data, income information, and security credentials.
 */
@Entity
@Table(name = "bank_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankUser implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getUsername() {
        return this.cpf;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
