package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;
import BankSdNd.example.BsDnD.util.PersonInputCollector;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Scanner;

public class AuthenticationHandler {
    private final AuthService authService;
    private final PersonService personService;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public AuthenticationHandler(AuthService authService, PersonService personService,
                                 PasswordEncoder passwordEncoder, AccountService accountService
                                 ) {
        this.authService = authService;
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    public void showCreate(Scanner sc, ConsoleUI ui) {

        while (true) {
            ui.displayRegisterAll();

            int choice = InputUtils.readInt(sc, "Escolha uma Opção: ");
            switch (choice){
                case 0 -> ui.clearScreen();
                case 1 -> registerUser(sc, ui);
                case 9 -> {
                    ui.showMenuGoBack();
                    return;
                }

                default -> ui.showChoseOptions();
            }
        }
    }

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
                  ui.showAttemptsRemaining (MAX_ATTEMPTS - attempts);
                }
            }
        }

        ui.showMaxAttemptsReached();
        return null;
    }

    public void registerUser(Scanner scanner, ConsoleUI ui) {
            ui.showCreateUser();

            PersonInputCollector collector = new PersonInputCollector(passwordEncoder);
            PersonDto dto = collector.collectUserInput(scanner);

            if (dto == null) {
                ui.showRegisterError();
                return;
            }

            try {
                BankUser person = personService.savePerson(dto);
                ui.showUserCreatedSuccessfully();
            } catch (IllegalArgumentException e) {
                ui.showValidationError(e.getMessage());
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

            LoginDto loginDto = new LoginDto(cpf, rawPassword);
            return authService.login(loginDto);
        } finally {

            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
        }
    }
}
