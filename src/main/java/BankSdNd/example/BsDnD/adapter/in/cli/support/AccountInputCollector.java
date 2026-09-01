package BankSdNd.example.BsDnD.adapter.in.cli.support;

import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Arrays;

@Component
public class AccountInputCollector {

    private final Scanner sc;
    private final ConsoleUI ui;
    private final InputUtils inputUtils;

    public AccountInputCollector(Scanner sc, ConsoleUI ui, InputUtils inputUtils) {
        this.sc = sc;
        this.ui = ui;
        this.inputUtils = inputUtils;
    }

    public String collectCpf() {
        return inputUtils.readString(sc, ui.getMessage("prompt.cpf"));
    }

    public String collectOriginAccount(GetAccountUseCase getAccountUseCase, BankUser currentUser) {

        while (true) {

            String typedAccount = inputUtils.readString(sc, ui.getMessage("prompt.origin_account"));

            if (getAccountUseCase.isAccountNumberOwner(typedAccount, currentUser.getId())) {
                return typedAccount;
            }
            ui.showOwnershipError();
            
            ui.showRetryOrCancelMenu();
            int choice = inputUtils.readInt(sc, ui.getMessage("prompt.choose_option"));
            if (choice != 1) return null;
        }
    }

    public String collectDestinationAccount() {
        return inputUtils.readString(sc, ui.getMessage("prompt.destination_account"));
    }

    public BigDecimal collectTransferAmount() {
        return inputUtils.readBigDecimal(sc, ui.getMessage("prompt.transfer_amount"));
    }

    public int collectAccountIndexForDeletion() {
        return inputUtils.readInt(sc, ui.getMessage("prompt.account_deletion_index"));
    }

    public char[] captureTransactionPassword() {

        char[] rawPassword = null;

        try {
            ui.showConfirmPassword();
            rawPassword = PasswordUtils.catchPassword(ui.getMessage("prompt.transaction_password"));

            if (rawPassword == null || rawPassword.length == 0) {
                ui.showPasswordNull();
                return null;
            }

            return rawPassword;
        } catch (IllegalArgumentException e) {

            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
            throw e;
        }
    }
}
