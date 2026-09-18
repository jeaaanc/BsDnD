package BankSdNd.example.BsDnD.core.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "529.982.247-25"
    })
    @DisplayName("Should return true for a valid CPF with or without formatting")
    void isValidWithValidCpf(String validCpf) {

        boolean isValid = CpfValidator.isValid(validCpf);

        assertTrue(isValid);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "11111111111",
            "12345678901",
            "123456789",
            "123456789012",
            "abc",
    })
    @DisplayName("Should return false for null, empty, or invalid CPFs")
    void isValidWithInvalidCpf(String invalidCpf) {

        boolean isValid = CpfValidator.isValid(invalidCpf);

        assertFalse(isValid);
    }
}
