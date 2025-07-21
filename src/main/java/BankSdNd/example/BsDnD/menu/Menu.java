package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.model.Account;
import BankSdNd.example.BsDnD.model.BankUser;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.PersonService;

import java.util.Scanner;

public class Menu {

    private final PersonService personService;
    private final  AccountService accountService;

    public Menu(PersonService personService, AccountService accountService) {
        this.personService = personService;
        this.accountService = accountService;
    }

    public void display(Scanner sc) {
        int firstOption = 0;
        int secondOption = 0;
        do {
            LoginMenu loginMenu = new LoginMenu(personService);
            ClientMenu clientMenu = new ClientMenu();
            clientMenu.firstDisplayMenu();

            firstOption = sc.nextInt();
            sc.nextLine();

            switch (firstOption){
                case 1:
                    System.out.println("A 1 escolhido\n");
                    clientMenu.displayRegisterAll();
                    secondOption = sc.nextInt();
                    sc.nextLine();
                    switch (secondOption){
                        case 1:
                            new Cadastro(personService).register(sc);
                            break;
                        case 2:
                            new AccountCreate(accountService).registerUserAccount(sc);
                            break;
                        case 3:
                            System.out.println("Teste2");
                            break;
                        default:
                            System.out.println("Opção invalida");
                    }

                    break;
                case 2:
                    BankUser confirmedClient = new LoginMenu(personService).displayLogin(sc);
                    System.out.println(confirmedClient.getName());
                    System.out.println("//escolhendo o cartao//");
                    loginMenu.clientLoginConfirmedOptions(confirmedClient, accountService, sc);
                    break;
            }
        }while (firstOption !=3);
    }
}
