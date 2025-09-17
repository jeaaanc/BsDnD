package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.LoanLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LoanService {

    private final AccountService accountService;

    //constantes regras de negócios
    private static final BigDecimal POINTS_DIVISOR = new BigDecimal("100.00");
    private static final BigDecimal LOAN_MULTIPLIER = new BigDecimal("250.00");

    public LoanService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Calcula o limite de empréstimo disponível para um cliente com base em seu saldo total.
     * Esta é uma regra de negócio pura, sem interação com o usuário.
     *
     * @param user O cliente para o qual o limite será calculado.
     * @return O valor do limite de empréstimo em BigDecimal.
     */

    public BigDecimal calculateLoanLimit(BankUser user) {

        List<Account> accounts = accountService.searchClientAccount(user.getCpf());
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Regra: Limite = (Saldo / 100) * 250
        return totalBalance.divide(POINTS_DIVISOR, 2, RoundingMode.HALF_UP).multiply(LOAN_MULTIPLIER);
    }

    /**
     * Concede um empréstimo a um cliente, depositando o valor em sua primeira conta encontrada.
     * Este método é transacional para garantir a consistência dos dados.
     *
     * @param user            O cliente que receberá o empréstimo.
     * @param requestedAmount O valor solicitado para o empréstimo.
     * @return A conta que recebeu o depósito do empréstimo.
     */

    @Transactional
    public Account grantLoan(BankUser user, BigDecimal requestedAmount) {

        BigDecimal limit = calculateLoanLimit(user);
        if (requestedAmount.compareTo(limit) > 0) {
            throw new LoanLimitExceededException("O valor solicitado excede seu limite de empréstimo de R$ " + limit);
        }
        if (requestedAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O valor do empréstimo deve ser positivo.");
        }
        // Econtra a primeira conta do usuário para deposito o valor
        Account primaryAccount = accountService.searchClientAccount(user.getCpf()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cliente não possui conta para receber o empréstimo."));
        // Efetua a operação
        primaryAccount.deposit(requestedAmount);
        return primaryAccount;
    }
}
