package BankSdNd.example.BsDnD.controller;

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
     * Starts and manages the main application loop.
     * It displays the initial menu (e.g., Register, Login, Exit) and waits for user input,
     * dispatching the chosen action to the appropriate handler. This loop runs until the
     * user chooses to exit the application.
     */
    public void display() {
        while (true) {
            ui.firstDisplayMenu();

            int choice = InputUtils.readInt(sc, "Escolha uma Opção: ");

            switch (choice) {
                case 0 -> ui.clearScreen();
                case 1 -> authHandler.showCreate(sc, ui);
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
    //Alberto	Cunha	14521128009 /444  / marcos	silva	12345678909


}


