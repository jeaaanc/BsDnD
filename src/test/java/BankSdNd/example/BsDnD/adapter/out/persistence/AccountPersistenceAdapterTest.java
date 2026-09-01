package BankSdNd.example.BsDnD.adapter.out.persistence;

import BankSdNd.example.BsDnD.adapter.in.cli.ConsoleController;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import({AccountPersistenceAdapter.class, BankUserPersistenceAdapter.class})
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter accountAdapter;

    @Autowired
    private BankUserPersistenceAdapter userAdapter;

    @MockBean
    private ConsoleController consoleController;

    private final String DEFAULT_ACCOUNT_NUMBER = "12345-6";

    @Test
    @DisplayName("Should save Account and map to JPA entity correctly")
    void shouldSaveAccountAndMapToJpaEntity() {

        BankUser savedUser = createAndSaveDefaultUser("12345678900");
        Account account = new Account(DEFAULT_ACCOUNT_NUMBER, savedUser);

        Account savedAccount = accountAdapter.save(account);

        assertNotNull(savedAccount.getId());
        assertEquals(DEFAULT_ACCOUNT_NUMBER, savedAccount.getAccountNumber());
        assertEquals(savedUser.getId(), savedAccount.getHolder().getId());
        assertTrue(savedAccount.isActive());
        assertEquals(BigDecimal.ZERO, savedAccount.getBalance());
    }

    @Test
    @DisplayName("Should retrieve active Account by Account Number")
    void shouldRetrieveActiveAccountByAccountNumber() {

        BankUser savedUser = createAndSaveDefaultUser("09876543211");
        Account account = new Account(DEFAULT_ACCOUNT_NUMBER, savedUser);
        accountAdapter.save(account);

        Optional<Account> found = accountAdapter.findByAccountNumberAndActiveTrue(DEFAULT_ACCOUNT_NUMBER);

        assertTrue(found.isPresent());
        assertEquals(DEFAULT_ACCOUNT_NUMBER, found.get().getAccountNumber());
    }

    @Test
    @DisplayName("Should retrieve all accounts by CPF")
    void shouldRetrieveAllAccountsByCpf() {

        String userCpf = "11122233344";
        String firstAccount = "11111-1";
        String secondAccount = "22222-2";
        BankUser savedUser = createAndSaveDefaultUser(userCpf);
        accountAdapter.save(new Account(firstAccount, savedUser));
        accountAdapter.save(new Account(secondAccount, savedUser));

        List<Account> accounts = accountAdapter.findAllByCpf(userCpf);

        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(a -> a.getAccountNumber().equals(firstAccount)));
        assertTrue(accounts.stream().anyMatch(a -> a.getAccountNumber().equals(secondAccount)));
    }

    @Test
    @DisplayName("Should retrieve all active accounts")
    void shouldRetrieveAllActiveAccounts() {

        String activeAccountNumber = "33333-3";
        String inactiveAccountNumber = "44444-4";
        BankUser savedUser = createAndSaveDefaultUser("33344455566");

        Account activeAccount = new Account(activeAccountNumber, savedUser);
        Account inactiveAccount = new Account(inactiveAccountNumber, savedUser);
        inactiveAccount.setActive(false);

        accountAdapter.save(activeAccount);
        accountAdapter.save(inactiveAccount);

        List<Account> activeAccounts = accountAdapter.findAllByActiveTrue();

        assertTrue(activeAccounts.stream().anyMatch(a -> a.getAccountNumber().equals(activeAccountNumber)));
        assertFalse(activeAccounts.stream().anyMatch(a -> a.getAccountNumber().equals(inactiveAccountNumber)));
    }

    @Test
    @DisplayName("Should retrieve account by ID")
    void shouldRetrieveAccountById() {

        String numberAccount = "55555-5";
        BankUser savedUser = createAndSaveDefaultUser("55566677788");
        Account account = new Account(numberAccount, savedUser);
        Account savedAccount = accountAdapter.save(account);

        Optional<Account> found = accountAdapter.findById(savedAccount.getId());

        assertTrue(found.isPresent());
        assertEquals(numberAccount, found.get().getAccountNumber());
    }

    private BankUser createAndSaveDefaultUser(String cpf) {
        BankUser user = BankUser.builder()
                .name("Default")
                .lastName("User")
                .cpf(cpf)
                .phoneNumber("11999999999")
                .password("encoded")
                .transactionPassword("1234")
                .income(BigDecimal.TEN)
                .build();
        return userAdapter.save(user);
    }
}
