package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.LoanService;
import BankSdNd.example.BsDnD.service.PersonService;
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
    private final PersonService personService;

    private final Scanner sc;
    private final ConsoleUI ui;

    private BankUser currentUser;

    public UserSessionHandler(AccountService accountService,
                              PersonService personService,
                              LoanService loanService,
                              AuthService authService,
                              Scanner scanner,
                              ConsoleUI ui) {
        this.accountService = accountService;
        this.personService = personService;
        this.loanService = loanService;
        this.authService = authService;
        this.sc = scanner;
        this.ui = ui;
    }

    public void runUserSession(BankUser loggedInUser) {
        this.currentUser = loggedInUser;
        boolean loggedIn = true;

        while (loggedIn) {

            ui.personChecked(this.currentUser);
            int choice = InputUtils.readInt(sc, "Escolha uma opção: ");

            switch (choice) {
                case 1 -> registerUserAccount();
                case 2 -> balance();
                case 3 -> showTransferForm();
                case 4 -> handleLoanRequest();
                case 5 -> loggedIn = showUserProfile();
                case 9 -> loggedIn = false;
                case 0 -> ui.clearScreen();
                default -> ui.showChoseOptions();
            }
        }
        this.currentUser = null;
        ui.showUserSessionExpired();
    }


    private boolean showUserProfile() {
        while (true) {

            ui.showProfileHeader(this.currentUser);
            ui.displayProfileMenu();
            int choice = InputUtils.readInt(sc, "Escolha uma opção: ");

            switch (choice) {
                case 1 -> viewPersonalData();
                case 2 -> viewAccountBalance();
                case 3 -> this.currentUser = handleChangeName();
                case 4 -> {
                    boolean passwordChangedSuccessFully = handleChangePassword();
                    if (passwordChangedSuccessFully) {
                        return false;
                    }
                }
                case 5 -> this.currentUser = handleChangePhoneNumber();
                case 9 -> {
                    ui.showMenuGoBack();
                    return true;
                }
                case 0 -> ui.clearScreen();
                default -> ui.showChoseOptions();
            }
        }
    }

    public void registerUserAccount() {
        ui.showCreateAccount();

        String cpf = InputUtils.readString(sc, "Seu CPF: ");

        boolean isPasswordConfirmed = askConfirmTransactionPassword();
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

    public void handleLoanRequest() {

        BigDecimal limit = loanService.calculateLoanLimit(this.currentUser);

        ui.showMoneyLoan();

        String formattedResult = CurrencyUtils.formatToBrazilianCurrency(limit);
        ui.loanShowLimitFormated(formattedResult);

        BigDecimal requesAmount = InputUtils.readBigDecimal(sc, "Digite o valor que deseja solicitar" +
                "(ou 0 para cancelar): ");

        if (requesAmount.compareTo(BigDecimal.ZERO) == 0) {
            ui.loanRequestShowCanceled();
            return;
        }

        boolean isPasswordConfirmed = askConfirmTransactionPassword();
        if (!isPasswordConfirmed) {
            ui.showPasswordValidationError();
            return;
        }

        try {
            Account updateAccount = loanService.grantLoan(this.currentUser, requesAmount);
            ui.showLoanSucess(updateAccount, requesAmount);
        } catch (Exception e) {
            ui.showResquestLoanErro(e.getMessage());
        }
    }

    public void showTransferForm() {
        ui.showTransferMenu();

        boolean isPasswordIsConfirmed = askConfirmTransactionPassword();

        if (!isPasswordIsConfirmed) {
            ui.showTranferErroValidationPassword();
            return;
        }

        String accountOrigem = readAccountOrigem();

        String accountDestination = readContaDestino();

        BigDecimal valor = InputUtils.readBigDecimal(sc, "Digite o valor da transferência: ");

        try {

            accountService.transfer(accountOrigem, accountDestination, valor);
            ui.showTranferSuccessfully();

        } catch (InvalidPasswordException | IllegalArgumentException e) {
            ui.showErroTransfer(e.getMessage());
        }
    }


    public void balance() {
        List<Account> accounts = accountService.searchClientAccount(this.currentUser.getCpf());

        ui.displayAccountList(accounts);
    }

    private boolean askConfirmTransactionPassword() {

        char[] typedPassword = null;
        try {
            ui.showConfimedPassword();

            typedPassword = PasswordUtils.catchPassword("Senha: ");

            if (typedPassword == null || typedPassword.length == 0) {
                ui.showPasswordNull();
                return false;
            }

            authService.validatePassword(this.currentUser.getId(), typedPassword);
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


    private String readAccountOrigem() {
        String typedAccount = InputUtils.readString(sc, "Digite o número da sua conta(origem):");
        accountService.validateAccountOwnership(this.currentUser.getId(), typedAccount);

        return typedAccount;
    }

    private String readContaDestino() {
        return InputUtils.readString(sc, "Digite o número da conta de destino: ");
    }


    private boolean handleChangePassword() {
        ui.showChangePasswordScreen();

        char[] oldPassword = null;
        char[] newPassword = null;

        try {

            oldPassword = PasswordUtils.catchPassword("Digite sua senha ANTIGA: ");
            if (oldPassword == null) {
                ui.showPasswordNull();
                return false;
            }

            newPassword = askForNewConfirmedPassword();
            if (newPassword == null) {
                ui.showPasswordNull();
                return false;
            }

            authService.changePassword(this.currentUser.getId(), oldPassword, newPassword);
            ui.showProfilePasswordChangeSuccessfully();
            return true;

        } catch (Exception e) {

            ui.showProfilePasswordUpdateError(e.getMessage());
            return false;
        } finally {

            if (oldPassword != null) Arrays.fill(oldPassword, '\0');
            if (newPassword != null) Arrays.fill(newPassword, '\0');
        }
    }

    private BankUser handleChangePhoneNumber() {
        ui.showChangePhonenumberScreen();

        try {
            String newPhoneNumber = InputUtils.readString(sc, "Digite o novo número de telefone: ");

            BankUser updatedUser = personService.changePhoneNumber(this.currentUser.getId(), newPhoneNumber);
            ui.showProfilePhoneChangedSuccessfully();
            return updatedUser;

        } catch (Exception e) {

            ui.showProfilePhoneUpdateError(e.getMessage());
            return this.currentUser;
        }
    }

    private char[] askForNewConfirmedPassword() {
        char[] newPassword = null;
        char[] newPasswordConfirmation = null;

        try {
            while (true) {
                newPassword = PasswordUtils.catchPassword("Digite sua NOVA senha: ");
                if (newPassword == null) return null;

                newPasswordConfirmation = PasswordUtils.catchPassword("Confirme sua Nova senha: ");
                if (newPasswordConfirmation == null) {
                    Arrays.fill(newPassword, '\0');
                    return null;
                }

                if (Arrays.equals(newPassword, newPasswordConfirmation)) {
                    return newPassword;
                }

                ui.showProfilePasswordMismatch();
                Arrays.fill(newPassword, '\0');
                Arrays.fill(newPasswordConfirmation, '\0');

                int option = InputUtils.readInt(sc, "1- Tentar Novamente\n2- Cancelar\nEscolha uma opção:");
                if (option == 2) {
                    return null;
                }
            }

        } finally {
            if (newPasswordConfirmation != null) Arrays.fill(newPasswordConfirmation, '\0');
        }

    }

    private BankUser handleChangeName() {
        ui.showChangeNameScreen();

        try {
            String newFirstName = InputUtils.readString(sc, "Digite o novo primeiro Nome: ");
            String newLastName = InputUtils.readString(sc, "Digite o novo sobrenome: ");

            BankUser updatedUser = personService.changeName(this.currentUser.getId(), newFirstName, newLastName);

            ui.showNameChangedSuccessFully();

            return updatedUser;

        } catch (Exception e) {
            ui.showNameChangeError(e.getMessage());
            return this.currentUser;
        }
    }

    private void viewPersonalData() {
        ui.displayPersonalData(this.currentUser);
        InputUtils.readString(sc, "Pressione Enter para voltar ao menu");
    }

    private void viewAccountBalance() {
        balance();
        InputUtils.readString(sc, "Pressione Enter para voltar ao menu");
    }
}
