package BankSdNd.example.BsDnD.adapter.out.persistence;

import BankSdNd.example.BsDnD.adapter.out.persistence.entity.BankUserJpaEntity;
import BankSdNd.example.BsDnD.adapter.out.persistence.repository.SpringDataBankUserRepository;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BankUserPersistenceAdapter implements BankUserRepositoryPort {

    private final SpringDataBankUserRepository repository;

    public BankUserPersistenceAdapter(SpringDataBankUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BankUser> findByCpf(String cpf) {
        return repository.findByCpf(cpf).map(this::toDomain);
    }

    @Override
    public Optional<BankUser> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public BankUser save(BankUser user) {
        BankUserJpaEntity entity = toEntity(user);
        BankUserJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<BankUser> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private BankUser toDomain(BankUserJpaEntity entity) {
        return BankUser.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .cpf(entity.getCpf())
                .phoneNumber(entity.getPhoneNumber())
                .password(entity.getPassword())
                .transactionPassword(entity.getTransactionPassword())
                .income(entity.getIncome())
                .build();
    }

    private BankUserJpaEntity toEntity(BankUser user) {
        return BankUserJpaEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .cpf(user.getCpf())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .transactionPassword(user.getTransactionPassword())
                .income(user.getIncome())
                .build();
    }
}
