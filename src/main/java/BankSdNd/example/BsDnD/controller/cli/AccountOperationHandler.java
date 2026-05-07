package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.BusinessException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.util.AccountInputCollector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class AccountOperationHandler {

    private final AccountService accountService;
    private final AccountInputCollector inputCollector;
    private final ConsoleUI ui;

    public AccountOperationHandler(AccountService accountService, Scanner sc, ConsoleUI ui) {
        this.accountService = accountService;
        this.ui = ui;
        this.inputCollector = new AccountInputCollector(sc, ui);
    }

    public void registerUserAccount(BankUser currentUser) {
        ui.showCreateAccount();

        String cpf = inputCollector.collectCpf();

        char[] capturedPassword = inputCollector.captureTransactionPassword();
        if (capturedPassword == null) {
            ui.accountShowPasswordValidation();
            return;
        }

        try {
            Account createdAccount = accountService.createAccount(cpf, capturedPassword);
            ui.accountCreatedSuccessfully(createdAccount);

        } catch (Exception e) {
            ui.showAccountValidationError(e.getMessage());
        } finally {
            Arrays.fill(capturedPassword, '\0');
        }
    }

    public void balance(BankUser currentUser) {
        if (!requireActiveAccount(currentUser)) {
            return;
        }

        List<Account> accounts = accountService.searchClientAccount(currentUser.getCpf());
        ui.displayAccountList(accounts);
    }

    public void showTransferForm(BankUser currentUser) {
        if (!requireActiveAccount(currentUser)) {
            return;
        }

        ui.showTransferMenu();

        char[] password = inputCollector.captureTransactionPassword();
        if (password == null) {
            ui.showTransferPasswordError();
            return;
        }

        try {
            String originAccount = inputCollector.collectOriginAccount(accountService, currentUser);
            if (originAccount == null) return;

            String destinationAccount = inputCollector.collectDestinationAccount();
            BigDecimal valor = inputCollector.collectTransferAmount();

            accountService.transfer(originAccount, destinationAccount, valor, password);
            ui.showTransferSuccess();
        } catch (BusinessException | IllegalArgumentException e) {
            ui.showTransferError(e.getMessage());
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public void handleAccountDeletion(BankUser currentUser) {
        if (!requireActiveAccount(currentUser)) {
            return;
        }
        ui.showDeleteAccountMenu();
        balance(currentUser);

        int accountIndex = inputCollector.collectAccountIndexForDeletion();
        if (accountIndex == 0) return;

        try {
            List<Account> accounts = accountService.searchClientAccount(currentUser.getCpf());
            if (accountIndex < 1 || accountIndex > accounts.size()) {
                ui.showInvalidOption();
                return;
            }
            Long idToDelete = accounts.get(accountIndex - 1).getId();

            accountService.softDeleteAccount(idToDelete);
            ui.showAccountClosedSuccess();
        } catch (Exception e) {
            ui.showAccountClosingError(e.getMessage());
        }
    }

    public boolean requireActiveAccount(BankUser currentUser) {
        List<Account> accounts = accountService.searchClientAccount(currentUser.getCpf());

        if (accounts == null || accounts.isEmpty()) {
            ui.showAccessDeniedNoActiveAccount();
            return false;
        }
        return true;
    }
}
