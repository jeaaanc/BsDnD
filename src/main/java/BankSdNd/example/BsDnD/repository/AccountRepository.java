package BankSdNd.example.BsDnD.repository;

import BankSdNd.example.BsDnD.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    @Query("SELECT a FROM Account a WHERE a.titular.cpf = :cpf")
    List<Account> findAllByCpf(@Param("cpf")String cpf);
}

