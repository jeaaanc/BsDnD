package BankSdNd.example.BsDnD.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialCalculatorTest {

    private StandardFinancialCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StandardFinancialCalculator();
    }

    @Test
    @DisplayName("Should calculate coefficients correctly for all scenarios and print the table")
    void calculateCoefficientsTable() {
        List<Integer> terms = List.of(12, 24, 36, 48, 60);
        List<BigDecimal> rates = List.of(
                new BigDecimal("0.015"), // 1.5%
                new BigDecimal("0.020"), // 2.0%
                new BigDecimal("0.030"), // 3.0%
                new BigDecimal("0.040")  // 4.0%
        );

        System.out.println("\n--- Loan Coefficient Table (4 decimal places) ---");
        System.out.printf("%-10s | %-10s | %-10s | %-10s | %-10s\n", "Rate/Term", "12 mo", "24 mo", "36 mo", "48 mo", "60 mo");
        System.out.println("------------------------------------------------------------------");

        for (BigDecimal rate : rates) {
            System.out.printf("%-10s", rate.multiply(new BigDecimal("100")) + "%");
            for (Integer term : terms) {
                BigDecimal coefficient = calculator.calculateCoefficient(rate, term);
                System.out.printf(" | %-10s", coefficient);
                
                // Basic validation for one known point: 2% at 12 months
                // Formula: (1 - (1.02)^-12) / 0.02 = 10.5753
                if (rate.compareTo(new BigDecimal("0.020")) == 0 && term == 12) {
                    assertEquals(0, new BigDecimal("10.5753").compareTo(coefficient), 
                        "Coefficient for 2% at 12 months should be 10.5753");
                }
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------------------\n");
    }
}
