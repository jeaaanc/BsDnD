package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.LoanService;
import BankSdNd.example.BsDnD.util.CurrencyUtils;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserSessionHandler {

    private final AccountService accountService;
    private final LoanService loanService;
    private final AuthService authService;

    private final Scanner sc;
    private final ConsoleUI ui;
    public UserSessionHandler(AccountService accountService, LoanService loanService,
                              AuthService authService,
                              Scanner scanner,
                              ConsoleUI ui) {
        this.accountService = accountService;
        this.loanService = loanService;
        this.authService = authService;
        this.sc = scanner;
        this.ui = ui;
    }

    public void runUserSession(BankUser loggedInUser) {
        boolean loggedIn = true;

        while (loggedIn) {
            ui.personChecked(loggedInUser);
            int choice = InputUtils.readInt(sc, "Escolha uma opção: ");
            switch (choice) {
                case 1 -> registerUserAccount(loggedInUser, sc, ui);
                case 2 -> balance(loggedInUser);
                case 3 -> showTransferForm(loggedInUser, sc, ui);
                case 4 -> handleLoanRequest(loggedInUser, sc, ui);
                case 9 -> loggedIn = false;
                case 0 -> ui.clearScreen();
                default -> ui.showChoseOptions();
            }
        }
    }

    public void registerUserAccount(BankUser loggedInUser, Scanner sc, ConsoleUI ui) {
        ui.showCreateAccount();

        String cpf = InputUtils.readString(sc, "Seu CPF: ");

        boolean isPasswordConfirmed = askConfirmTransactionPassword(loggedInUser, ui);
        if (!isPasswordConfirmed) {
            ui.accountShowPasswordValidation();
            return;
        }

        try {
            Account createdAccount = accountService.accountCreate(cpf);
            ui.accountCreatedSuccessfully(createdAccount);

        } catch (Exception e) {
            ui.accountValidationShowError(e.getMessage());
        }
    }

    public void handleLoanRequest(BankUser loggedInUser, Scanner scanner, ConsoleUI ui) {

        BigDecimal limit = loanService.calculateLoanLimit(loggedInUser);

        ui.showMoneyLoan();

        String formattedResult = CurrencyUtils.formatToBrazilianCurrency(limit);
        ui.loanShowLimitFormated(formattedResult);

        BigDecimal requesAmount = InputUtils.readBigDecimal(scanner, "Digite o valor que deseja solicitar" +
                "(ou 0 para cancelar): ");

        if (requesAmount.compareTo(BigDecimal.ZERO) == 0) {
            ui.loanRequestShowCanceled();
            return;
        }

        boolean isPasswordConfirmed = askConfirmTransactionPassword(loggedInUser, ui);
        if (!isPasswordConfirmed) {
            ui.showPasswordValidationError();
            return;
        }

        try {
            Account updateAccount = loanService.grantLoan(loggedInUser, requesAmount);
            ui.showLoanSucess(updateAccount, requesAmount);
        } catch (Exception e) {
            ui.showResquestLoanErro(e.getMessage());
        }
    }

    public void showTransferForm(BankUser clientConfirmed, Scanner scanner, ConsoleUI ui) {
        ui.showTransferMenu();

        boolean isPasswordIsConfirmed = askConfirmTransactionPassword(clientConfirmed, ui);

        if (!isPasswordIsConfirmed) {
            ui.showTranferErroValidationPassword();
            return;
        }

        String accountOrigem = readAccountOrigem(clientConfirmed, scanner);

        String accountDestination = readContaDestino(scanner);

        BigDecimal valor = InputUtils.readBigDecimal(scanner, "Digite o valor da transferência: ");

        try {

            accountService.transfer(accountOrigem, accountDestination, valor);
            ui.showTranferSuccessfully();

        } catch (InvalidPasswordException | IllegalArgumentException e) {
            ui.showErroTransfer(e.getMessage());
        }
    }


    public void balance(BankUser clientConfirmed) {
        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());

        ui.displayAccountList(accounts);
    }

    private boolean askConfirmTransactionPassword(BankUser user, ConsoleUI ui) {

        char[] typedPassword = null;
        try {
            ui.showConfimedPassword();

            typedPassword = PasswordUtils.catchPassword("Senha: ");

            if (typedPassword == null || typedPassword.length == 0) {
                ui.showPasswordNull();
                return false;
            }

            authService.validatePassword(user.getId(), typedPassword);
            return true;

        } catch (InvalidPasswordException | UserNotFoundException e) {
            ui.showValidationError(e.getMessage());
            return false;
        } finally {

            if (typedPassword != null) {
                Arrays.fill(typedPassword, '\0');
            }
        }
    }


    private String readAccountOrigem(BankUser user, Scanner scanner) {
        String typedAccount = InputUtils.readString(scanner, "Digite o número da sua conta(origem):");
        accountService.validateAccountOwnership(user.getId(), typedAccount);

        return typedAccount;
    }

    private String readContaDestino(Scanner scanner) {
        return InputUtils.readString(scanner, "Digite o número da conta de destino: ");
    }
}
