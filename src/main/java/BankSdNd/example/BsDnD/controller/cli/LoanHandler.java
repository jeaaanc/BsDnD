package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.LoanService;
import BankSdNd.example.BsDnD.util.AccountInputCollector;
import BankSdNd.example.BsDnD.util.CurrencyUtils;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

@Component
public class LoanHandler {

    private final LoanService loanService;
    private final AccountOperationHandler accountOperationHandler;
    private final AccountInputCollector inputCollector;
    private final Scanner sc;
    private final ConsoleUI ui;

    public LoanHandler(LoanService loanService,
                       AccountOperationHandler accountOperationHandler,
                       Scanner sc,
                       ConsoleUI ui) {
        this.loanService = loanService;
        this.accountOperationHandler = accountOperationHandler;
        this.inputCollector = new AccountInputCollector(sc, ui);
        this.sc = sc;
        this.ui = ui;
    }

    public void handleLoanRequest(BankUser currentUser) {
        if (!accountOperationHandler.requireActiveAccount(currentUser)) {
            return;
        }

        BigDecimal limit = loanService.calculateLoanLimit(currentUser);
        ui.showMoneyLoan();

        String formattedResult = CurrencyUtils.formatToBrazilianCurrency(limit);
        ui.loanShowLimitFormated(formattedResult);

        BigDecimal requesAmount = InputUtils.readBigDecimal(sc, "Digite o valor que deseja solicitar (ou 0 para cancelar): ");

        if (requesAmount.compareTo(BigDecimal.ZERO) == 0) {
            ui.loanRequestShowCanceled();
            return;
        }

        char[] capturedPassword = inputCollector.captureTransactionPassword();
        if (capturedPassword == null) {
            ui.showPasswordValidationError();
            return;
        }

        try {
            Account updateAccount = loanService.grantLoan(currentUser, requesAmount, capturedPassword);
            ui.showLoanSuccess(updateAccount, requesAmount);
        } catch (Exception e) {
            ui.showLoanRequestError(e.getMessage());
        } finally {
            Arrays.fill(capturedPassword, '\0');
        }
    }

    private char[] captureTransactionPassword() {
        return inputCollector.captureTransactionPassword();
    }
}
