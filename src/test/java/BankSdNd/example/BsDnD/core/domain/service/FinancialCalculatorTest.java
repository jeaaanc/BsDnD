package BankSdNd.example.BsDnD.core.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialCalculatorTest {

    private StandardFinancialCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StandardFinancialCalculator();
    }

    @ParameterizedTest(name = "Taxa:{0} | Prazo: {1} meses ==> Coeficiente Esperado: {2}")
    @CsvSource({
            "0.015, 12, 10.9075",
            "0.020, 12, 10.5753",
            "0.030, 24, 16.9355",
            "0.040, 36, 18.9083",
    })
    @DisplayName("Should calculate financial coefficients correctly for various rates and terms")
    void calculateCoefficientsTable(String rateStr, int term, String expectedStr) {

        BigDecimal rate = new BigDecimal(rateStr);
        BigDecimal expectedCoefficient = new BigDecimal(expectedStr);

        BigDecimal actualCoefficient = calculator.calculateCoefficient(rate, term);

        assertEquals(0, expectedCoefficient.compareTo(actualCoefficient),
                "Coefficient mismatch for rate " + rateStr);
    }
}
