package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.exception.business.DuplicateException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;
import BankSdNd.example.BsDnD.util.PersonInputCollector;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Controller responsible for handling all pre-authentication user flows.
 * This includes user registration, login attempts, and the associated sub-menus.
 * It orchestrates calls to business services and the UI view.
 */
public class AuthenticationHandler {
    private final AuthService authService;
    private final PersonService personService;
    private final AccountService accountService;

    public AuthenticationHandler(AuthService authService, PersonService personService,
                                 AccountService accountService
    ) {
        this.authService = authService;
        this.personService = personService;
        this.accountService = accountService;
    }


    /**
     * Displays and manages the registration sub-menu.
     * Allows the user to navigate the creation process and returns the successfully
     * created user back to the main menu for automatic login.
     *
     * @param sc The {@code Scanner} instance for reading user input.
     * @param ui The {@code ConsoleUI} instance for displaying messages.
     * @return The created {@code BankUser} if the registration is successful,
     * or {@code null} if the user chooses to go back without registering.
     */
    public BankUser showCreate(Scanner sc, ConsoleUI ui) {

        while (true) {
            ui.displayRegisterAll();

            int choice = InputUtils.readInt(sc, "Escolha uma Opção: ");
            switch (choice) {
                case 0 -> ui.clearScreen();
                case 1 -> {
                    BankUser newUser = registerUser(sc, ui);

                    if (newUser != null) {
                        return newUser;
                    }
                }
                case 9 -> {
                    ui.showMenuGoBack();
                    return null;
                }

                default -> ui.showChoseOptions();
            }
        }
    }

    /**
     * Manages the complete user login flow, including handling multiple attempts.
     * It orchestrates the UI display, input collection via helper methods, and calls
     * the authentication service to validate credentials.
     *
     * @param sc The {@code Scanner} instance for user input.
     * @param ui The {@code ConsoleUI} instance for displaying all user-facing messages.
     * @return The authenticated {@code BankUser} if the login is successful, or {@code null} if the user
     * cancels or exceeds the maximum attempts.
     */
    public BankUser performLogin(Scanner sc, ConsoleUI ui) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            ui.showDisplayLogin();
            try {
                BankUser loggedUser = attemptLoginOnce(sc, ui);

                if (loggedUser != null) {
                    ui.showLoginSuccessfully();
                    return loggedUser;
                } else {
                    ui.showLoginCancelled();
                    return null;
                }
            } catch (Exception e) {

                attempts++;
                ui.showValidationError(e.getMessage());

                if (attempts < MAX_ATTEMPTS) {
                    ui.showAttemptsRemaining(MAX_ATTEMPTS - attempts);
                }
            }
        }

        ui.showMaxAttemptsReached();
        return null;
    }

    /**
     * Handles the step-by-step user registration flow.
     * Orchestrates data collection and calls the service layer to persist the new user.
     *
     * @param scanner The {@code Scanner} instance for reading user input.
     * @param ui      The {@code ConsoleUI} instance for displaying messages.
     * @return The newly created and persisted {@code BankUser}, or {@code null}
     * if the user cancels the operation or if data validation fails.
     */
    public BankUser registerUser(Scanner scanner, ConsoleUI ui) {
        ui.showCreateUser();

        PersonInputCollector collector = new PersonInputCollector(ui);
        PersonDto dto = collector.collectUserInput(scanner);

        if (dto == null) {
            ui.showRegisterError();
            return null;
        }

        try {

            BankUser person = personService.savePerson(dto);
            ui.showUserCreatedSuccessfully();
            return person;

        } catch (IllegalArgumentException | DuplicateException e) {
            ui.showValidationError(e.getMessage());
            return null;
        }
    }


    private BankUser attemptLoginOnce(Scanner sc, ConsoleUI ui) {
        String cpf = InputUtils.readString(sc, "CPF: (ou digite ´sair´ para cancelar): ");

        if ("sair".equalsIgnoreCase(cpf)) {
            return null;
        }
        char[] rawPassword = null;

        try {

            rawPassword = PasswordUtils.catchPassword("Senha:");

            if (rawPassword == null) {
                return null;
            }

            String password = new String(rawPassword);

            LoginDto loginDto = new LoginDto(cpf, password);
            return authService.login(loginDto);
        } finally {

            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
        }
    }
}
