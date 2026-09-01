package BankSdNd.example.BsDnD.adapter;

import BankSdNd.example.BsDnD.config.UserDetailsAdapter;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import BankSdNd.example.BsDnD.core.application.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepositoryPort accountRepository;

    @Autowired
    private BankUserRepositoryPort bankUserRepository;

    @Autowired
    private PasswordEncoderPort passwordEncoder;

    @MockBean
    private CommandLineRunner commandLineRunner;

    private BankUser user1;
    private BankUser user2;

    private final String USER1_CPF = "11111111111";
    private final String USER2_CPF = "22222222222";
    private final String NEW_USER_CPF = "33333333333";

    private final String ACCOUNT1_NUM = "10001";
    private final String ACCOUNT2_NUM = "20001";

    private final String DEFAULT_PASSWORD = "123456";
    private final String TX_PASSWORD_USER1 = "1234";
    private final String TX_PASSWORD_USER2 = "5678";
    private final String WRONG_TX_PASSWORD = "wrong";


    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        bankUserRepository.deleteAll();

        user1 = BankUser.builder()
                .name("User 1")
                .cpf(USER1_CPF)
                .income(new BigDecimal("2000"))
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .transactionPassword(passwordEncoder.encode(TX_PASSWORD_USER1))
                .build();

        user2 = BankUser.builder()
                .name("User 2")
                .cpf(USER2_CPF)
                .income(new BigDecimal("3000"))
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .transactionPassword(passwordEncoder.encode(TX_PASSWORD_USER2))
                .build();

        user1 = bankUserRepository.save(user1);
        user2 = bankUserRepository.save(user2);

        Account account1 = new Account(ACCOUNT1_NUM, user1);
        account1.deposit(new BigDecimal("1000"));

        Account account2 = new Account(ACCOUNT2_NUM, user2);
        account2.deposit(new BigDecimal("500"));

        accountRepository.save(account1);
        accountRepository.save(account2);
    }

    @Test
    @DisplayName("Should transfer money between accounts correctly")
    void transferSuccessfully() {

        authenticateAs(user1);
        BigDecimal tranferAmount = new BigDecimal("300");

        accountService.transfer(ACCOUNT1_NUM, ACCOUNT2_NUM, tranferAmount, TX_PASSWORD_USER1.toCharArray());

        Account updatedOrigin = accountRepository.findByAccountNumberAndActiveTrue(ACCOUNT1_NUM).get();
        Account updatedDest = accountRepository.findByAccountNumberAndActiveTrue(ACCOUNT2_NUM).get();

        assertEquals(0, new BigDecimal("700.00").compareTo(updatedOrigin.getBalance()), "Origin balance mismatch");
        assertEquals(0, new BigDecimal("800.00").compareTo(updatedDest.getBalance()), "Destination balance mismatch");
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void transferWithInsufficientBalance() {

        authenticateAs(user1);
        BigDecimal transferAmount = new BigDecimal("1500");

        assertThrows(InsufficientBalanceException.class, () -> {
            accountService.transfer(ACCOUNT1_NUM, ACCOUNT2_NUM, transferAmount, TX_PASSWORD_USER1.toCharArray());
        });
    }

    // Arrumar exception
    @Test
    @DisplayName("Should throw exception when transaction password is wrong")
    void transferWithWrongPassword() {

        authenticateAs(user1);
        BigDecimal transferAmount = new BigDecimal("100");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.transfer(ACCOUNT1_NUM, ACCOUNT2_NUM,transferAmount, WRONG_TX_PASSWORD.toCharArray());
        });

        assertTrue(exception.getMessage().contains("Invalid transaction password") ||
                exception.getMessage().equals("error.password_incorrect"));
    }

    @Test
    @DisplayName("Should create a new account with zero balance")
    void accountCreateWithZeroBalance() {

        BankUser newUser = BankUser.builder()
                .name("New User")
                .cpf(NEW_USER_CPF)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .transactionPassword(passwordEncoder.encode(TX_PASSWORD_USER1))
                .build();
        newUser = bankUserRepository.save(newUser);
        authenticateAs(newUser);

        Account account = accountService.createAccount(NEW_USER_CPF, TX_PASSWORD_USER1.toCharArray());

        assertNotNull(account);
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }

    private void authenticateAs(BankUser user) {

        UserDetailsAdapter userDetailsAdapter = new UserDetailsAdapter(user);
        var auth = new UsernamePasswordAuthenticationToken(userDetailsAdapter, null, userDetailsAdapter.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
