package BankSdNd.example.BsDnD.util;

import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.domain.BankUser;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Arrays;

public class AccountInputCollector {

    private final Scanner sc;
    private final ConsoleUI ui;

    public AccountInputCollector(Scanner sc, ConsoleUI ui) {
        this.sc = sc;
        this.ui = ui;
    }

    public String collectCpf() {
        return InputUtils.readString(sc, "Seu CPF: ");
    }

    public String collectOriginAccount(AccountService accountService, BankUser currentUser) {
        while (true) {
            String typedAccount = InputUtils.readString(sc, "Digite o número da sua conta(origem):");
            if (accountService.isAccountNumberOwner(typedAccount, currentUser.getId())) {
                return typedAccount;
            }
            ui.showOwnershipError();
            
            ui.showRetryOrCancelMenu();
            int choice = InputUtils.readInt(sc, "Escolha uma opção: ");
            if (choice != 1) return null;
        }
    }

    public String collectDestinationAccount() {
        return InputUtils.readString(sc, "Digite o número da conta de destino: ");
    }

    public BigDecimal collectTransferAmount() {
        return InputUtils.readBigDecimal(sc, "Digite o valor da transferência: ");
    }

    public int collectAccountIndexForDeletion() {
        return InputUtils.readInt(sc, "Digite o número da conta da lista acima" +
                " que deseja encerrar (0 para cancelar): ");
    }

    public char[] captureTransactionPassword() {
        char[] rawPassword = null;
        try {
            ui.showConfirmPassword();
            rawPassword = PasswordUtils.catchPassword("Senha de transação: ");

            if (rawPassword == null || rawPassword.length == 0) {
                ui.showPasswordNull();
                return null;
            }

            return rawPassword;
        } catch (Exception e) {
            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
            throw e;
        }
    }
}
