package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.BusinessException;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.core.port.in.CreateAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.TransferMoneyUseCase;
import BankSdNd.example.BsDnD.adapter.in.cli.support.AccountInputCollector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class AccountOperationHandler {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final ManageAccountUseCase manageAccountUseCase;
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final AccountInputCollector inputCollector;
    private final ConsoleUI ui;

    public AccountOperationHandler(CreateAccountUseCase createAccountUseCase, 
                                   GetAccountUseCase getAccountUseCase, 
                                   ManageAccountUseCase manageAccountUseCase, 
                                   TransferMoneyUseCase transferMoneyUseCase, 
                                   AccountInputCollector inputCollector,
                                   ConsoleUI ui) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.manageAccountUseCase = manageAccountUseCase;
        this.transferMoneyUseCase = transferMoneyUseCase;
        this.ui = ui;
        this.inputCollector = inputCollector;
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
            Account createdAccount = createAccountUseCase.createAccount(cpf, capturedPassword);
            ui.accountCreatedSuccessfully(createdAccount);

        } catch (BusinessException e) {
            ui.showAccountValidationError(e.getMessage());
        } finally {
            if (capturedPassword != null) {
                Arrays.fill(capturedPassword, '\0');
            }
        }
    }

    public void balance(BankUser currentUser) {
        if (!requireActiveAccount(currentUser)) {
            return;
        }

        List<Account> accounts = getAccountUseCase.searchClientAccount(currentUser.getCpf());
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
            String originAccount = inputCollector.collectOriginAccount(getAccountUseCase, currentUser);
            if (originAccount == null) return;

            String destinationAccount = inputCollector.collectDestinationAccount();
            BigDecimal valor = inputCollector.collectTransferAmount();

            transferMoneyUseCase.transfer(originAccount, destinationAccount, valor, password);
            ui.showTransferSuccess();
        } catch (BusinessException | IllegalArgumentException e) {
            ui.showTransferError(e.getMessage());
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
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
            List<Account> accounts = getAccountUseCase.searchClientAccount(currentUser.getCpf());
            if (accountIndex < 1 || accountIndex > accounts.size()) {
                ui.showInvalidOption();
                return;
            }
            Long idToDelete = accounts.get(accountIndex - 1).getId();

            manageAccountUseCase.softDeleteAccount(idToDelete);
            ui.showAccountClosedSuccess();
        } catch (BusinessException e) {
            ui.showAccountClosingError(e.getMessage());
        }
    }

    public boolean requireActiveAccount(BankUser currentUser) {
        List<Account> accounts = getAccountUseCase.searchClientAccount(currentUser.getCpf());

        if (accounts == null || accounts.isEmpty()) {
            ui.showAccessDeniedNoActiveAccount();
            return false;
        }
        return true;
    }
}
