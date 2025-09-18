package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;

import java.util.Arrays;
import java.util.Scanner;

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

    /////                   vv--MENU--Principal--vv
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
    //Alberto	Cunha	14521128009 / marcos	silva	12345678909


}


