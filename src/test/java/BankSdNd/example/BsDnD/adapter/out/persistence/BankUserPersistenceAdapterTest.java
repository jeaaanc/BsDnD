package BankSdNd.example.BsDnD.adapter.out.persistence;

import BankSdNd.example.BsDnD.adapter.in.cli.ConsoleController;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(BankUserPersistenceAdapter.class)
class BankUserPersistenceAdapterTest {

    @Autowired
    private BankUserPersistenceAdapter adapter;

    @MockBean
    private ConsoleController consoleController;

    private final String DEFAULT_NAME = "John";
    private final String DEFAULT_CPF = "12345678900";
    private final String DEFAULT_PHONE = "11999999999";
    private final String INVALID_CPF = "00000000000";

    @Test
    @DisplayName("Should save BankUser and map to JPA entity correctly")
    void shouldSaveBankUserAndMapToJpaEntity() {

        BankUser user = buildBankUser(DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE);

        BankUser savedUser = adapter.save(user);

        assertNotNull(savedUser.getId());
        assertEquals(DEFAULT_NAME, savedUser.getName());
        assertEquals(DEFAULT_CPF, savedUser.getCpf());
    }

    @Test
    @DisplayName("Should retrieve existing BankUser by CPF")
    void shouldRetrieveExistingBankUserByCpf() {

        BankUser user = buildBankUser(DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE);
        adapter.save(user);

        Optional<BankUser> found = adapter.findByCpf(DEFAULT_CPF);

        assertTrue(found.isPresent());
        assertEquals(DEFAULT_NAME, found.get().getName());
    }

    @Test
    @DisplayName("Should return true if user exists by phone number")
    void shouldReturnTrueIfExistsByPhoneNumber() {

        BankUser user = buildBankUser(DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE);
        adapter.save(user);

        boolean exists = adapter.existsByPhoneNumber(DEFAULT_PHONE);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return true if user exists by CPF")
    void shouldReturnTrueIfExistsByCpf() {

        BankUser user = buildBankUser(DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE);
        adapter.save(user);

        boolean exists = adapter.existsByCpf(DEFAULT_CPF);
        boolean noExists = adapter.existsByCpf(INVALID_CPF);

        assertTrue(exists);
        assertFalse(noExists);
    }

    @Test
    @DisplayName("Should retrieve existing BankUser by ID")
    void shouldRetrieveExistingBankUserById() {

        BankUser user = buildBankUser(DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE);
        BankUser savedUser = adapter.save(user);

        Optional<BankUser> found = adapter.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals(DEFAULT_NAME, found.get().getName());
    }

    @Test
    @DisplayName("Should retrieve all BankUsers")
    void shouldRetrieveAllBankUsers() {

        BankUser user1 = buildBankUser(DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE);
        BankUser user2 = buildBankUser("Jane", "09876543211", "11988888888");
        adapter.save(user1);
        adapter.save(user2);

        var all = adapter.findAll();

        assertTrue(all.size() >= 2);
    }

    private BankUser buildBankUser(String name, String cpf, String phone) {
        return BankUser.builder()
                .name(name)
                .lastName("Doe")
                .cpf(cpf)
                .phoneNumber(phone)
                .password("encoded_pass")
                .transactionPassword("1234")
                .income(new BigDecimal("5000.00"))
                .build();
    }
}
