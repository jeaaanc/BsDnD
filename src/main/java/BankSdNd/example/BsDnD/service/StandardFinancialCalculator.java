package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Standard implementation of the financial calculator.
 * <p>
 * Implements basic business rules for limit calculations.
 */
@Component
public class StandardFinancialCalculator implements FinancialCalculator {

    private static final BigDecimal MONTHLY_INTEREST_DEFAULT = new BigDecimal("0.02"); // 2.0%
    private static final int DEFAULT_TERM_MONTHS = 12;
    private static final BigDecimal INCOME_COMMITMENT_PERCENTAGE = new BigDecimal("0.30"); // 30% of income

    @Override
    public BigDecimal calculateLoanLimit(BankUser user, List<Account> accounts) {
        if (user.getIncome() == null || user.getIncome().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Max monthly installment (30% of income)
        BigDecimal maxInstallment = user.getIncome().multiply(INCOME_COMMITMENT_PERCENTAGE);

        // Coefficient for 2% interest over 12 months
        BigDecimal coefficient = calculateCoefficient(MONTHLY_INTEREST_DEFAULT, DEFAULT_TERM_MONTHS);

        // Present Value (Loan Limit) = Installment * Coefficient
        return maxInstallment.multiply(coefficient).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the financing coefficient based on the present value of an annuity formula.
     * Formula: Coefficient = (1 - (1 + i)^-n) / i
     *
     * @param monthlyInterest The monthly interest rate (decimal, e.g., 0.03 for 3%).
     * @param termMonths      The term in months (n).
     * @return The exact coefficient rounded to 4 decimal places.
     */
    public BigDecimal calculateCoefficient(BigDecimal monthlyInterest, int termMonths) {
        if (monthlyInterest.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal(termMonths);
        }

        // (1 + i)
        BigDecimal onePlusI = BigDecimal.ONE.add(monthlyInterest);

        // (1 + i)^-n = 1 / (1 + i)^n
        BigDecimal onePlusIPowN = onePlusI.pow(termMonths);
        BigDecimal onePlusIPowMinusN = BigDecimal.ONE.divide(onePlusIPowN, 10, RoundingMode.HALF_UP);

        // (1 - (1 + i)^-n)
        BigDecimal numerator = BigDecimal.ONE.subtract(onePlusIPowMinusN);

        // (1 - (1 + i)^-n) / i
        return numerator.divide(monthlyInterest, 4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateVipLoanLimit(BankUser user, List<Account> accounts) {
        // Example for VIP: 40% income commitment and 1.5% interest
        BigDecimal maxInstallment = user.getIncome().multiply(new BigDecimal("0.40"));
        BigDecimal coefficient = calculateCoefficient(new BigDecimal("0.015"), 24);
        return maxInstallment.multiply(coefficient).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateCreditLimit(BankUser user, List<Account> accounts) {
        // Future implementation for credit cards (e.g., percentage of income + balance)
        return BigDecimal.ZERO;
    }

    /**
     * Helper method to sum the balance of all provided accounts.
     *
     * @param accounts The list of accounts to sum.
     * @return The total balance.
     */
    private BigDecimal calculateTotalBalance(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
