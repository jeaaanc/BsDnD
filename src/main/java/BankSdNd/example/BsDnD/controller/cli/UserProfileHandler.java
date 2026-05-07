package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Scanner;

@Component
public class UserProfileHandler {

    private final PersonService personService;
    private final AuthService authService;
    private final AccountOperationHandler accountOperationHandler;
    private final Scanner sc;
    private final ConsoleUI ui;

    public UserProfileHandler(PersonService personService,
                              AuthService authService,
                              AccountOperationHandler accountOperationHandler,
                              Scanner sc,
                              ConsoleUI ui) {
        this.personService = personService;
        this.authService = authService;
        this.accountOperationHandler = accountOperationHandler;
        this.sc = sc;
        this.ui = ui;
    }

    public BankUser showUserProfile(BankUser currentUser) {
        BankUser user = currentUser;
        while (true) {
            ui.showProfileHeader(user);
            ui.displayProfileMenu();
            int choice = InputUtils.readInt(sc, "Escolha uma opção: ");

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
        InputUtils.readString(sc, "Pressione Enter para voltar ao menu");
    }

    private void viewAccountBalance(BankUser user) {
        accountOperationHandler.balance(user);
        InputUtils.readString(sc, "Pressione Enter para voltar ao menu");
    }

    private BankUser handleChangeName(BankUser user) {
        ui.showChangeNameScreen();
        try {
            String newFirstName = InputUtils.readString(sc, "Digite o novo primeiro Nome: ");
            String newLastName = InputUtils.readString(sc, "Digite o novo sobrenome: ");
            BankUser updatedUser = personService.updateName(user.getId(), newFirstName, newLastName);
            ui.showNameChangeSuccess();
            return updatedUser;
        } catch (Exception e) {
            ui.showNameChangeError(e.getMessage());
            return user;
        }
    }

    private boolean handleChangePassword(BankUser user) {
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
            authService.updatePassword(user.getId(), oldPasswordString, newPassword);
            ui.showProfilePasswordChangeSuccess();
            return true;
        } catch (Exception e) {
            ui.showProfilePasswordUpdateError(e.getMessage());
            return false;
        } finally {
            if (rawOldPassword != null) Arrays.fill(rawOldPassword, '\0');
        }
    }

    private BankUser handleChangePhoneNumber(BankUser user) {
        ui.showChangePhonenumberScreen();
        try {
            String newPhoneNumber = InputUtils.readString(sc, "Digite o novo número de telefone: ");
            BankUser updatedUser = personService.updatePhoneNumber(user.getId(), newPhoneNumber);
            ui.showProfilePhoneChangeSuccess();
            return updatedUser;
        } catch (Exception e) {
            ui.showProfilePhoneUpdateError(e.getMessage());
            return user;
        }
    }

    private void handleChangeTransactionPassword(BankUser user) {
        ui.print("\n===== Alteração de Senha de Transação =====");
        char[] rawOldPassword = null;
        try {
            rawOldPassword = PasswordUtils.catchPassword("Digite sua senha de transação ATUAL: ");
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
            authService.updateTransactionPassword(user.getId(), oldPasswordString, newPassword);
            ui.print("\nSenha de transação alterada com sucesso!");
        } catch (Exception e) {
            ui.print("Não foi possível alterar a senha de transação: " + e.getMessage());
        } finally {
            if (rawOldPassword != null) Arrays.fill(rawOldPassword, '\0');
        }
    }

    private String askForNewConfirmedTransactionPassword() {
        char[] newPassword = null;
        char[] newPasswordConfirmation = null;
        try {
            while (true) {
                newPassword = PasswordUtils.catchPassword("Digite sua NOVA senha de transação: ");
                if (newPassword == null) return null;

                newPasswordConfirmation = PasswordUtils.catchPassword("Confirme sua NOVA senha de transação: ");
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            if (newPassword != null) Arrays.fill(newPassword, '\0');
            if (newPasswordConfirmation != null) Arrays.fill(newPasswordConfirmation, '\0');
            throw e;
        }
    }
}
