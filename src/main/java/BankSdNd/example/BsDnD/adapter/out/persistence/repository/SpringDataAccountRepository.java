package BankSdNd.example.BsDnD.adapter.out.persistence.repository;

import BankSdNd.example.BsDnD.adapter.out.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, Long> {
    @Query("SELECT a FROM AccountJpaEntity a WHERE a.holder.cpf = :cpf AND a.active = true")
    List<AccountJpaEntity> findAllByCpf(@Param("cpf") String cpf);
    
    Optional<AccountJpaEntity> findByAccountNumberAndActiveTrue(String accountNumber);
    List<AccountJpaEntity> findAllByActiveTrue();
    boolean existsByAccountNumberAndActiveTrue(String accountNumber);
}
