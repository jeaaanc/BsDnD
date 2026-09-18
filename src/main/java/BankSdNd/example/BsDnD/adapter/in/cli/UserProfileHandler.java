package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.exception.InvalidPasswordException;
import BankSdNd.example.BsDnD.core.domain.exception.UserNotFoundException;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.core.port.in.ManageCredentialsUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManagePersonUseCase;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Scanner;

@Component
public class UserProfileHandler {

    private final ManagePersonUseCase managePersonUseCase;
    private final ManageCredentialsUseCase manageCredentialsUseCase;
    private final AccountOperationHandler accountOperationHandler;
    private final InputUtils inputUtils;
    private final Scanner sc;
    private final ConsoleUI ui;

    public UserProfileHandler(ManagePersonUseCase managePersonUseCase,
                              ManageCredentialsUseCase manageCredentialsUseCase,
                              AccountOperationHandler accountOperationHandler,
                              InputUtils inputUtils,
                              Scanner sc,
                              ConsoleUI ui) {
        this.managePersonUseCase = managePersonUseCase;
        this.manageCredentialsUseCase = manageCredentialsUseCase;
        this.accountOperationHandler = accountOperationHandler;
        this.inputUtils = inputUtils;
        this.sc = sc;
        this.ui = ui;
    }

    public BankUser showUserProfile(BankUser currentUser) {
        BankUser user = currentUser;
        while (true) {
            ui.showProfileHeader(user);
            ui.displayProfileMenu();
            int choice = inputUtils.readInt(sc, ui.getMessage("prompt.choose_option"));

            switch (choice) {
                case 1 -> viewPersonalData(user);
                case 2 -> viewAccountBalance(user);
                case 3 -> user = handleChangeName(user);
                case 4 -> {
                    if (handleChangePassword(user)) {
                        return null;
                    }
                }
                case 5 -> handleChangeTransactionPassword(user);
                case 6 -> user = handleChangePhoneNumber(user);
                case 9 -> {
                    ui.showMenuGoBack();
                    return user;
                }
                case 0 -> ui.clearScreen();
                default -> ui.showChooseOptions();
            }
        }
    }

    private void viewPersonalData(BankUser user) {
        ui.displayPersonalData(user);
        inputUtils.readString(sc, ui.getMessage("generic.press_enter"));
    }

    private void viewAccountBalance(BankUser user) {
        accountOperationHandler.balance(user);
        inputUtils.readString(sc, ui.getMessage("generic.press_enter"));
    }

    private BankUser handleChangeName(BankUser user) {
        ui.showChangeNameScreen();
        try {
            String newFirstName = inputUtils.readString(sc, ui.getMessage("prompt.new_first_name"));
            String newLastName = inputUtils.readString(sc, ui.getMessage("prompt.new_last_name"));
            BankUser updatedUser = managePersonUseCase.updateName(user.getId(), newFirstName, newLastName);
            ui.showNameChangeSuccess();
            return updatedUser;
        } catch (UserNotFoundException | IllegalArgumentException e) {
            ui.showNameChangeError(e.getMessage());
            return user;
        }
    }

    private boolean handleChangePassword(BankUser user) {
        ui.showChangePasswordScreen();
        char[] rawOldPassword = null;
        try {
            rawOldPassword = PasswordUtils.catchPassword(ui.getMessage("prompt.old_password"));
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
            manageCredentialsUseCase.updatePassword(user.getId(), oldPasswordString, newPassword);
            ui.showProfilePasswordChangeSuccess();
            return true;
        } catch (UserNotFoundException | InvalidPasswordException | IllegalArgumentException e) {
            ui.showProfilePasswordUpdateError(e.getMessage());
            return false;
        } finally {
            if (rawOldPassword != null) Arrays.fill(rawOldPassword, '\0');
        }
    }

    private BankUser handleChangePhoneNumber(BankUser user) {
        ui.showChangePhonenumberScreen();
        try {
            String newPhoneNumber = inputUtils.readString(sc, ui.getMessage("prompt.new_phone"));
            BankUser updatedUser = managePersonUseCase.updatePhoneNumber(user.getId(), newPhoneNumber);
            ui.showProfilePhoneChangeSuccess();
            return updatedUser;
        } catch (UserNotFoundException | DuplicateException | IllegalArgumentException e) {
            ui.showProfilePhoneUpdateError(e.getMessage());
            return user;
        }
    }

    private void handleChangeTransactionPassword(BankUser user) {
        ui.print(ui.getMessage("profile.transaction_password.header"));
        char[] rawOldPassword = null;
        try {
            rawOldPassword = PasswordUtils.catchPassword(ui.getMessage("prompt.old_transaction_password"));
            if (rawOldPassword == null) {
                ui.showPasswordNull();
                return;
            }

            String newPassword = askForNewConfirmedTransactionPassword();
            if (newPassword == null) {
                ui.showPasswordNull();
                return;
            }

            String oldPasswordString = new String(rawOldPassword);
            manageCredentialsUseCase.updateTransactionPassword(user.getId(), oldPasswordString, newPassword);
            ui.print(ui.getMessage("profile.transaction_password.success"));
        } catch (UserNotFoundException | InvalidPasswordException | IllegalArgumentException e) {
            ui.print(ui.getMessage("profile.transaction_password.error", e.getMessage()));
        } finally {
            if (rawOldPassword != null) Arrays.fill(rawOldPassword, '\0');
        }
    }

    private String askForNewConfirmedTransactionPassword() {
        char[] newPassword = null;
        char[] newPasswordConfirmation = null;
        try {
            while (true) {
                newPassword = PasswordUtils.catchPassword(ui.getMessage("prompt.new_transaction_password"));
                if (newPassword == null) return null;

                newPasswordConfirmation = PasswordUtils.catchPassword(ui.getMessage("prompt.confirm_new_transaction_password"));
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

                int option = inputUtils.readInt(sc, ui.getMessage("prompt.retry_or_cancel_1") + "\n" +
                        ui.getMessage("prompt.retry_or_cancel_2") + "\n" +
                        ui.getMessage("prompt.choose_option"));
                if (option == 2) {
                    return null;
                }
            }
        } catch (IllegalArgumentException e) {
            if (newPassword != null) Arrays.fill(newPassword, '\0');
            if (newPasswordConfirmation != null) Arrays.fill(newPasswordConfirmation, '\0');
            throw e;
        }
    }

    private String askForNewConfirmedPassword() {
        char[] newPassword = null;
        char[] newPasswordConfirmation = null;
        try {
            while (true) {
                newPassword = PasswordUtils.catchPassword(ui.getMessage("prompt.new_password"));
                if (newPassword == null) return null;

                newPasswordConfirmation = PasswordUtils.catchPassword(ui.getMessage("prompt.confirm_new_password"));
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

                int option = inputUtils.readInt(sc, ui.getMessage("prompt.retry_or_cancel_1") + "\n" +
                        ui.getMessage("prompt.retry_or_cancel_2") + "\n" +
                        ui.getMessage("prompt.choose_option"));
                if (option == 2) {
                    return null;
                }
            }
        } catch (IllegalArgumentException e) {
            if (newPassword != null) Arrays.fill(newPassword, '\0');
            if (newPasswordConfirmation != null) Arrays.fill(newPasswordConfirmation, '\0');
            throw e;
        }
    }
}
