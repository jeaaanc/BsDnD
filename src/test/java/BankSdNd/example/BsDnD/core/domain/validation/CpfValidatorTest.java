package BankSdNd.example.BsDnD.core.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfValidatorTest {

    @Test
    @DisplayName("Should return true for a valid CPF")
    void isValidWithValidCpf() {
        // CPF válido gerado aleatoriamente
        assertTrue(CpfValidator.isValid("52998224725"));
        assertTrue(CpfValidator.isValid("529.982.247-25")); // Com formatação
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11111111111", // Dígitos iguais
            "12345678901", // Inválido
            "123456789",   // Tamanho errado
            "123456789012", // Tamanho errado
            "",            // Vazio
            "abc",         // Letras
    })
    @DisplayName("Should return false for invalid CPFs")
    void isValidWithInvalidCpf(String cpf) {
        assertFalse(CpfValidator.isValid(cpf));
    }

    @Test
    @DisplayName("Should return false for null CPF")
    void isValidWithNullCpf() {
        assertFalse(CpfValidator.isValid(null));
    }
}
