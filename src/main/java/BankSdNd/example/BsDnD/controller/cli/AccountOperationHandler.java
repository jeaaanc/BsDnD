package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.AccountNotFoundException;
import BankSdNd.example.BsDnD.exception.business.BusinessException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class AccountOperationHandler {

    private final AccountService accountService;
    private final Scanner sc;
    private final ConsoleUI ui;

    public AccountOperationHandler(AccountService accountService, Scanner sc, ConsoleUI ui) {
        this.accountService = accountService;
        this.sc = sc;
        this.ui = ui;
    }

    public void registerUserAccount(BankUser currentUser) {
        ui.showCreateAccount();

        String cpf = InputUtils.readString(sc, "Seu CPF: ");

        String isPasswordConfirmed = captureTransactionPassword();
        if (isPasswordConfirmed == null) {
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

        String password = captureTransactionPassword();
        if (password == null) {
            ui.showTranferErroValidationPassword();
            return;
        }

        String accountOrigem = readAccountOrigem(currentUser);
        String accountDestination = readContaDestino();
        BigDecimal valor = InputUtils.readBigDecimal(sc, "Digite o valor da transferência: ");

        try {
            accountService.transfer(accountOrigem, accountDestination, valor, password);
            ui.showTranferSuccessfully();
        } catch (BusinessException | IllegalArgumentException e) {
            ui.showErroTransfer(e.getMessage());
        }
    }

    public void handleAccountDeletion(BankUser currentUser) {
        if (!requireActiveAccount(currentUser)) {
            return;
        }
        ui.showDeleteAccountMenu();
        balance(currentUser);

        int accountIndex = InputUtils.readInt(sc, "Digite o número da conta da lista acima" +
                " que deseja encerrar (0 para cancelar): ");
        if (accountIndex == 0) return;

        try {
            List<Account> accounts = accountService.searchClientAccount(currentUser.getCpf());
            if (accountIndex < 1 || accountIndex > accounts.size()) {
                ui.showError("Opção inválida.");
                return;
            }
            Long idToDelete = accounts.get(accountIndex - 1).getId();

            accountService.softDeleteAccount(idToDelete);
            ui.showSucess("Conta encerrada com sucesso!");
        } catch (Exception e) {
            ui.showError("Erro ao encerrar conta: " + e.getMessage());
        }
    }

    public boolean requireActiveAccount(BankUser currentUser) {
        try {
            List<Account> accounts = accountService.searchClientAccount(currentUser.getCpf());

            if (accounts == null || accounts.isEmpty()) {
                ui.showError("\n[Ação Negada] Você ainda não possui uma conta bancária ativa!");
                ui.print("-> Por favor, use a opção '1- Criar conta' no menu principal primeiro.\n");
                return false;
            }
            return true;
            // ! dar uma olhada depois em uma exception melhor !
        } catch (BusinessException e) {
            ui.showError("\n[Ação Negada] Você ainda não possui uma conta bancária ativa!");
            ui.print("-> Por favor, use a opção '1- Criar conta' no menu principal primeiro.\n");
            return false;
        }
    }

    private String captureTransactionPassword() {
        char[] rawPassword = null;
        try {
            ui.showConfimedPassword();
            rawPassword = PasswordUtils.catchPassword("Senha de transação: ");

            if (rawPassword == null || rawPassword.length == 0) {
                ui.showPasswordNull();
                return null;
            }

            return new String(rawPassword);
        } finally {
            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
        }
    }

    private String readAccountOrigem(BankUser currentUser) {
        String typedAccount = InputUtils.readString(sc, "Digite o número da sua conta(origem):");
        accountService.validateAccountOwnership(currentUser.getId(), typedAccount);
        return typedAccount;
    }

    private String readContaDestino() {
        return InputUtils.readString(sc, "Digite o número da conta de destino: ");
    }
}
