package BankSdNd.example.BsDnD.core.port.out;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankUserRepositoryPort {
    Optional<BankUser> findByCpf(String cpf);
    Optional<BankUser> findById(UUID id);
    boolean existsByCpf(String cpf);
    boolean existsByPhoneNumber(String phoneNumber);
    BankUser save(BankUser user);
    List<BankUser> findAll();
    void deleteAll();
}
