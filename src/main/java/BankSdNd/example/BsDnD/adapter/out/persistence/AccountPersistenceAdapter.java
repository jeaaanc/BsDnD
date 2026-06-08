package BankSdNd.example.BsDnD.adapter.out.persistence;

import BankSdNd.example.BsDnD.adapter.out.persistence.entity.AccountJpaEntity;
import BankSdNd.example.BsDnD.adapter.out.persistence.entity.BankUserJpaEntity;
import BankSdNd.example.BsDnD.adapter.out.persistence.repository.SpringDataAccountRepository;
import BankSdNd.example.BsDnD.adapter.out.persistence.repository.SpringDataBankUserRepository;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final SpringDataAccountRepository repository;
    private final SpringDataBankUserRepository userRepository;

    public AccountPersistenceAdapter(SpringDataAccountRepository repository, SpringDataBankUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Account> findAllByCpf(String cpf) {
        return repository.findAllByCpf(cpf).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Account> findByAccountNumberAndActiveTrue(String accountNumber) {
        return repository.findByAccountNumberAndActiveTrue(accountNumber).map(this::toDomain);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = toEntity(account);
        AccountJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Account> findAllByActiveTrue() {
        return repository.findAllByActiveTrue().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByAccountNumberAndActiveTrue(String accountNumber) {
        return repository.existsByAccountNumberAndActiveTrue(accountNumber);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private Account toDomain(AccountJpaEntity entity) {
        BankUser holder = BankUser.builder()
                .id(entity.getHolder().getId())
                .name(entity.getHolder().getName())
                .lastName(entity.getHolder().getLastName())
                .cpf(entity.getHolder().getCpf())
                .phoneNumber(entity.getHolder().getPhoneNumber())
                .password(entity.getHolder().getPassword())
                .transactionPassword(entity.getHolder().getTransactionPassword())
                .income(entity.getHolder().getIncome())
                .build();

        Account account = new Account(entity.getAccountNumber(), holder);
        account.setId(entity.getId());
        account.setBalance(entity.getBalance());
        account.setActive(entity.isActive());
        return account;
    }

    private AccountJpaEntity toEntity(Account account) {
        BankUserJpaEntity holderEntity = userRepository.findById(account.getHolder().getId())
                .orElseThrow(() -> new RuntimeException("User not found during account mapping"));

        return AccountJpaEntity.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .holder(holderEntity)
                .active(account.isActive())
                .build();
    }
}
