package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.bank.BankProducts;
import BankSdNd.example.BsDnD.controller.ConsoleController;
import BankSdNd.example.BsDnD.domain.BankUser;
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

        while (true){
            ConsoleController consoleController = new ConsoleController(personService, accountService);
            AllMenu clientMenu = new AllMenu();
            //menu principal
            clientMenu.firstDisplayMenu();

            firstOption = sc.nextInt();
            sc.nextLine();

            switch (firstOption){
                case 1:
                    clientMenu.displayRegisterAll();

                    secondOption = sc.nextInt();
                    sc.nextLine();
                    switch (secondOption){
                        case 1:
                            new ConsoleController(personService,accountService).registerUser(sc);
                            break;
                        case 2:
                            //!!! preciso colocar user necessario para criar conta
                            new ConsoleController(personService,accountService).registerUserAccount(accountService,sc);
                            break;
                        case 3:
                            System.out.println("Teste2");
                            break;
                        default:
                            System.out.println("Opção invalida");
                    }

                    break;
                case 2:
                    BankUser confirmedClient = new ConsoleController(personService, accountService).displayLogin(sc);
                    do  {
                        clientMenu.personChecked(confirmedClient);
                        secondOption = sc.nextInt();
                        sc.nextLine();
                        switch (secondOption) {
                            case 1:
                                consoleController.balance(confirmedClient);
                                break;
                            case 2:
                                consoleController.transfer(confirmedClient, sc);
                                break;
                            case 3:
                                System.out.println("@@TESTE");
                                BankProducts bankProducts = new BankProducts(accountService);
                                bankProducts.moneyLoan(confirmedClient);
                                break;
                            default:
                                System.out.println("Escolha uma das opções acima");
                        }
                        System.out.println("\n3 para sair / 0 para continuar ");
                        firstOption = sc.nextInt();
                        sc.nextLine();
                    }while (firstOption != 3);

                    System.out.println("\nProcesso finalizado!!!\n");
                    break;
            }
        }
    }
}
