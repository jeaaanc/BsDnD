package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.util.InputUtils;
import java.util.Scanner;

/**
 * The main controller for the console application.
 * This class acts as the primary entry point for the user interface,
 * orchestrating the main menu and delegating actions to specialized handlers.
 */
public class ConsoleController {

    private final AuthenticationHandler authHandler;
    private final UserSessionHandler userSessionHandler;

    private final Scanner sc;
    private final ConsoleUI ui;

    public ConsoleController(AuthenticationHandler authenticationHandler, UserSessionHandler userSessionHandler, Scanner sc, ConsoleUI ui) {
        this.authHandler = authenticationHandler;
        this.userSessionHandler = userSessionHandler;
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

            int choice = InputUtils.readInt(sc, "Escolha uma Opção: ");

            switch (choice) {
                case 0 -> ui.clearScreen();
                case 1 -> {
                    BankUser user = authHandler.showCreate(sc, ui);

                    if (user != null){
                        userSessionHandler.runUserSession(user);
                    }
                }
                case 2 -> {
                    BankUser user = authHandler.performLogin(sc, ui);
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
    // testes: 	cpf:95741676073 senha 123456 , numero da conta:
    // Jorge cpf: 73512227031 / 123456


}


