package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.exception.InvalidPasswordException;
import BankSdNd.example.BsDnD.core.domain.exception.UserNotFoundException;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.core.port.in.AuthenticateUserUseCase;
import BankSdNd.example.BsDnD.core.port.in.CreatePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.support.PersonInputCollector;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Controller responsible for handling all pre-authentication user flows.
 */
@Component
public class AuthenticationHandler {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final CreatePersonUseCase createPersonUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final InputUtils inputUtils;
    private final PersonInputCollector personInputCollector;
    private final Scanner sc;
    private final ConsoleUI ui;

    public AuthenticationHandler(AuthenticateUserUseCase authenticateUserUseCase, 
                                 CreatePersonUseCase createPersonUseCase,
                                 GetAccountUseCase getAccountUseCase, 
                                 InputUtils inputUtils,
                                 PersonInputCollector personInputCollector,
                                 Scanner sc, 
                                 ConsoleUI ui
    ) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.createPersonUseCase = createPersonUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.inputUtils = inputUtils;
        this.personInputCollector = personInputCollector;
        this.sc = sc;
        this.ui = ui;
    }


    /**
     * Displays and manages the registration sub-menu.
     * @return The created {@code BankUser} if the registration is successful,
     * or {@code null} if the user chooses to go back without registering.
     */
    public BankUser showCreate() {

        while (true) {
            ui.displayRegisterAll();

            int choice = inputUtils.readInt(sc, ui.getMessage("prompt.choose_option"));
            switch (choice) {
                case 0 -> ui.clearScreen();
                case 1 -> {
                    BankUser newUser = registerUser();

                    if (newUser != null) {
                        return newUser;
                    }
                }
                case 9 -> {
                    ui.showMenuGoBack();
                    return null;
                }

                default -> ui.showChooseOptions();
            }
        }
    }

    public BankUser performLogin() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            ui.showDisplayLogin();
            try {
                BankUser loggedUser = attemptLoginOnce();

                if (loggedUser != null) {
                    var userDetails = new BankSdNd.example.BsDnD.config.UserDetailsAdapter(loggedUser);
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    ui.showLoginSuccessfully();
                    return loggedUser;
                } else {
                    ui.showLoginCancelled();
                    return null;
                }
            } catch (UserNotFoundException | InvalidPasswordException | IllegalArgumentException e) {

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

    public BankUser registerUser() {
        ui.showCreateUser();

        CreatePersonCommand command = personInputCollector.collectUserInput(sc);

        if (command == null) {
            ui.showRegisterError();
            return null;
        }

        try {

            BankUser person = createPersonUseCase.savePerson(command);

            var userDetails = new BankSdNd.example.BsDnD.config.UserDetailsAdapter(person);
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            ui.showUserCreatedSuccessfully();
            return person;

        } catch (IllegalArgumentException | DuplicateException e) {
            ui.showValidationError(e.getMessage());
            return null;
        }
    }


    private BankUser attemptLoginOnce() {
        String cpf = inputUtils.readString(sc, ui.getMessage("prompt.cpf"));

        if ("sair".equalsIgnoreCase(cpf)) {
            return null;
        }
        char[] rawPassword = null;

        try {

            rawPassword = PasswordUtils.catchPassword(ui.getMessage("prompt.password"));

            if (rawPassword == null) {
                return null;
            }

            String password = new String(rawPassword);

            LoginCommand loginCommand = new LoginCommand(cpf, password);
            return authenticateUserUseCase.login(loginCommand);
        } finally {

            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
        }
    }
}
