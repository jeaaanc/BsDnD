package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;

import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * The main controller for the console application.
 * This class acts as the primary entry point for the user interface,
 * orchestrating the main menu and delegating actions to specialized handlers.
 */
@Component
public class ConsoleController {

    private final AuthenticationHandler authHandler;
    private final UserSessionHandler userSessionHandler;
    private final InputUtils inputUtils;

    private final Scanner sc;
    private final ConsoleUI ui;

    public ConsoleController(AuthenticationHandler authenticationHandler, 
                             UserSessionHandler userSessionHandler, 
                             InputUtils inputUtils,
                             Scanner sc, 
                             ConsoleUI ui) {
        this.authHandler = authenticationHandler;
        this.userSessionHandler = userSessionHandler;
        this.inputUtils = inputUtils;
        this.sc = sc;
        this.ui = ui;
    }


    /**
     * Starts the application's main entry loop.
     * Displays the primary menu, handles user choices for registration and login,
     * and seamlessly transitions the user into an active session (auto-login)
     * upon successful account creation or authentication.
     */
    public void display() {
        while (true) {
            ui.firstDisplayMenu();

            int choice = inputUtils.readInt(sc, ui.getMessage("prompt.choose_option"));

            switch (choice) {
                case 0 -> ui.clearScreen();
                case 1 -> {
                    BankUser user = authHandler.showCreate();

                    if (user != null) {
                        userSessionHandler.runUserSession(user);
                    }
                }
                case 2 -> {
                    BankUser user = authHandler.performLogin();
                    if (user != null) {
                        userSessionHandler.runUserSession(user);
                    }
                }
                case 3 -> {
                    ui.showMenuGoBack();
                    return;
                }
                default -> ui.showOptionInvalid();
            }
        }
    }
}


