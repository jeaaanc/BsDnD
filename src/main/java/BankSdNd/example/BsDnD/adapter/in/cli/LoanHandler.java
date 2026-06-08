package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.LoanLimitExceededException;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.support.AccountInputCollector;
import BankSdNd.example.BsDnD.adapter.in.cli.util.CurrencyUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.core.port.in.RequestLoanUseCase;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

@Component
public class LoanHandler {

    private final RequestLoanUseCase requestLoanUseCase;
    private final AccountOperationHandler accountOperationHandler;
    private final AccountInputCollector inputCollector;
    private final InputUtils inputUtils;
    private final Scanner sc;
    private final ConsoleUI ui;

    public LoanHandler(RequestLoanUseCase requestLoanUseCase,
                       AccountOperationHandler accountOperationHandler,
                       AccountInputCollector inputCollector,
                       InputUtils inputUtils,
                       Scanner sc,
                       ConsoleUI ui) {
        this.requestLoanUseCase = requestLoanUseCase;
        this.accountOperationHandler = accountOperationHandler;
        this.inputCollector = inputCollector;
        this.inputUtils = inputUtils;
        this.sc = sc;
        this.ui = ui;
    }

    public void handleLoanRequest(BankUser currentUser) {
        if (!accountOperationHandler.requireActiveAccount(currentUser)) {
            return;
        }

        BigDecimal limit = requestLoanUseCase.calculateLoanLimit(currentUser);
        ui.showMoneyLoan();

        String formattedResult = CurrencyUtils.formatCurrency(limit);
        ui.loanShowLimitFormated(formattedResult);

        BigDecimal requestedAmount = inputUtils.readBigDecimal(sc, ui.getMessage("prompt.loan_amount"));

        if (requestedAmount.compareTo(BigDecimal.ZERO) == 0) {
            ui.loanRequestShowCanceled();
            return;
        }

        char[] capturedPassword = inputCollector.captureTransactionPassword();
        if (capturedPassword == null) {
            ui.showPasswordValidationError();
            return;
        }

        try {
            Account updatedAccount = requestLoanUseCase.grantLoan(currentUser, requestedAmount, capturedPassword);
            ui.showLoanSuccess(updatedAccount, requestedAmount);
        } catch (LoanLimitExceededException | IllegalArgumentException e) {
            ui.showLoanRequestError(e.getMessage());
        } finally {
            if (capturedPassword != null) {
                Arrays.fill(capturedPassword, '\0');
            }
        }
    }
}
