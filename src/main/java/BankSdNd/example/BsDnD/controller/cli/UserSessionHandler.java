package BankSdNd.example.BsDnD.controller.cli;

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

/**
 * Controller responsible for managing the application's flow and logic for an authenticated user.
 * It handles the user session, displaying menus for logged-in actions and orchestrating
 * calls to various business services.
 */
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

    /**
     * Starts and manages the main loop for a logged-in user's session.
     * This method displays the main menu of actions and delegates tasks to other handlers based on user input.
     * The session ends when the user chooses to log out.
     *
     * @param loggedInUser The authenticated {@code BankUser} object representing the current user.
     */
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

    /**
     * Manages the user profile sub-menu, allowing the user to view their data or
     * navigate to data-update functionalities.
     *
     * @return {@code true} to continue the user session, or {@code false} to signal a logout is required (e.g., after a password change).
     */
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

    /**
     * Handles the flow for creating a new bank account for the currently logged-in user.
     * It orchestrates password re-authentication and calls the {@code AccountService}.
     */
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

    /**
     * Handles the business flow for requesting a loan. It calculates the user's limit,
     * prompts for the desired amount, re-authenticates the user, and calls the {@code LoanService}.
     */
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

    /**
     * Manages the user flow for transferring money between accounts. It ensures the user
     * re-authenticates and owns the source account before calling the {@code AccountService}.
     */
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

    /**
     * Fetches and orchestrates the display of all accounts and balances for the current user.
     */
    public void balance() {
        List<Account> accounts = accountService.searchClientAccount(this.currentUser.getCpf());

        ui.displayAccountList(accounts);
    }

    /**
     * Prompts the user to re-enter their password to confirm a sensitive operation.
     * Securely handles the password and calls the authentication service for validation.
     *
     * @return {@code true} if the password is correct, {@code false} otherwise.
     */
    private boolean askConfirmTransactionPassword() {

        char[] rawPassword = null;

        try {
            ui.showConfimedPassword();

            rawPassword = PasswordUtils.catchPassword("Senha: ");

            if (rawPassword == null || rawPassword.length == 0) {
                ui.showPasswordNull();
                return false;
            }

            String passwordString = new String(rawPassword);

            authService.validatePassword(this.currentUser.getId(), passwordString);
            return true;

        } catch (InvalidPasswordException | UserNotFoundException e) {
            ui.showValidationError(e.getMessage());
            return false;
        } finally {

            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
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

        char[] rawOldPassword = null;

        try {

            rawOldPassword = PasswordUtils.catchPassword("Digite sua senha ANTIGA: ");
            if (rawOldPassword == null) {
                ui.showPasswordNull();
                return false;
            }

            String newPassword = askForNewConfirmedPassword();

            if (newPassword == null) {
                ui.showPasswordNull();
                return false;
            }

            String oldPasswordString = new String(rawOldPassword);

            authService.changePassword(this.currentUser.getId(), oldPasswordString, newPassword);
            ui.showProfilePasswordChangeSuccessfully();
            return true;

        } catch (Exception e) {

            ui.showProfilePasswordUpdateError(e.getMessage());
            return false;
        } finally {

            if (rawOldPassword != null) Arrays.fill(rawOldPassword, '\0');
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

    private String askForNewConfirmedPassword() {
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

                    String finalPassword = new String(newPassword);

                    Arrays.fill(newPassword, '\0');
                    Arrays.fill(newPasswordConfirmation, '\0');

                    return finalPassword;
                }

                ui.showProfilePasswordMismatch();
                Arrays.fill(newPassword, '\0');
                Arrays.fill(newPasswordConfirmation, '\0');

                int option = InputUtils.readInt(sc, "1- Tentar Novamente\n2- Cancelar\nEscolha uma opção:");
                if (option == 2) {
                    return null;
                }
            }

        } catch (Exception e){
            if (newPassword != null) Arrays.fill(newPassword, '\0');
            if (newPasswordConfirmation != null) Arrays.fill(newPasswordConfirmation, '\0');

            throw  e;
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
