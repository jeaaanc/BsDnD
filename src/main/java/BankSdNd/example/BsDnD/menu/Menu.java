package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.bank.BankProducts;
import BankSdNd.example.BsDnD.controller.ConsoleController;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import BankSdNd.example.BsDnD.util.InputUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Scanner;

public class Menu {
    private final PersonService personService;
    private final AccountService accountService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public Menu(PersonService personService, AccountService accountService, AuthService authService, PasswordEncoder passwordEncoder) {
        this.personService = personService;
        this.accountService = accountService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    public void display(Scanner sc) {

        ConsoleController consoleController = new ConsoleController(personService, accountService, authService, passwordEncoder);
        ConsoleUI ui = new ConsoleUI();
        boolean running = true;

        while (running) {
            ui.firstDisplayMenu();

            int mainOption = InputUtils.readInt(sc, "Escolha uma Opção");

            switch (mainOption) {
                case 1:

                    ui.displayRegisterAll();
                    int subOption = InputUtils.readInt(sc, "Escolha uma opção");

                    switch (subOption) {
                        case 1 -> consoleController.registerUser(sc, ui);
                        case 2 -> consoleController.registerUserAccount(accountService, sc, ui);
                        default -> ui.showError("Opção invalida");
                    }
                    break;

                case 2:
                    BankUser client = consoleController.performLogin(sc, ui);

                    if (client == null) {
                        ui.showError("\nLogin falhou!!!!!. Tente novamente.\n");
                        break;
                    }

                    boolean loggedIn = true;

                    while (loggedIn) {
                        ui.personChecked(client);
                        int action = InputUtils.readInt(sc, "Escolha um opção");

                        switch (action) {
                            //Não para o processo caso o usuário não tenha conta ainda
                            case 1 -> consoleController.balance(client);
                            case 2 -> consoleController.showTransferForm(client, sc, ui);
                            case 3 -> new BankProducts(accountService).moneyLoan(client);
                            case 9 -> loggedIn = false;
                            default -> ui.showError("Escolha uma das opções acima");
                        }
                    }
                case 3:
                    running = false;
                    break;

                default:
                    ui.showError("Opção inváilida");
            }


        }
        System.out.println("\nProcesso Finalizado!!!\n");
    }

}
