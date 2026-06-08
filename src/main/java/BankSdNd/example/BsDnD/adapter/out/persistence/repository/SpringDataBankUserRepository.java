package BankSdNd.example.BsDnD.adapter.out.persistence.repository;

import BankSdNd.example.BsDnD.adapter.out.persistence.entity.BankUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataBankUserRepository extends JpaRepository<BankUserJpaEntity, Long> {
    Optional<BankUserJpaEntity> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByPhoneNumber(String phoneNumber);
}
