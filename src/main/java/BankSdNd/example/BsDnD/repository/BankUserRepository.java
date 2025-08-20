package BankSdNd.example.BsDnD.repository;

import BankSdNd.example.BsDnD.domain.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankUserRepository extends JpaRepository <BankUser, Long> {
    boolean existsByCpf(String cpf);
    Optional <BankUser> findByCpf(String cpf);

    boolean existsByPhoneNumber(String phoneNumber);
}
