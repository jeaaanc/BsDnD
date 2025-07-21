package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.model.Account;
import BankSdNd.example.BsDnD.model.BankUser;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.PersonService;

import java.util.List;
import java.util.Scanner;

public class LoginMenu {

    private final PersonService personService;

    public LoginMenu(PersonService personService) {
        this.personService = personService;
    }

    public BankUser displayLogin(Scanner sc){

        System.out.println("\n========Login========\n");

        System.out.println("\nCPF: ");
        String cpf = sc.nextLine();

        System.out.println("\nSenha: ");
        String password = sc.nextLine();

        LoginDto loginDto = new LoginDto(cpf, password);

        try {
            BankUser person = personService.login(loginDto);
            System.out.println("\nLogin Efetuado com sucesso.\n");
            return person;
        }catch (IllegalArgumentException e){
            System.out.println("\nErro: " + e.getMessage());
            return null;
        }


    }

    public void clientLoginConfirmedOptions(BankUser clientConfirmed, AccountService accountService, Scanner scanner) {
        System.out.println("\nBem vindo: " + clientConfirmed.getName());
        System.out.println("""
                1- Saldo.
                2- Transferência.
                3- extrato
                """);

        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());
        int option = 0;
        do {
            option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.println("Qual cartão ->");
                    accounts.forEach(a ->
                            System.out.println(
                                    "\nConta: " + a.getAccountNumber() +
                                            "\nSaldo: " + a.getBalance() + "\n------------------\n"
                            ));
                    break;
                case 2:
                    System.out.println("\n=====Transferência=====");
                    System.out.println("\nAparti de qual Conta deseja fazer a Transferencia");
                    for (int i = 0; i < accounts.size(); i++) {
                        Account acc = accounts.get(i);
                        System.out.printf("\nConta: %d: %s | Saldo: R$ %.2f%n",
                                i + 1,
                                acc.getAccountNumber(),
                                acc.getBalance()
                        );
                    }

                    System.out.println("@@@Escolhendo@@@");
                    option = scanner.nextInt();
                    if (option == 1){
                        System.out.println("-------" + clientConfirmed.getName() + "-------" );
                        System.out.println("Num: " + accounts.get(0).getAccountNumber());
                        System.out.println("Saldo: " + accounts.get(0).getBalance());
                        System.out.println("Obra em andamento... \nzzz\n  zzzzz\n   zzzzzzzz");
                    }
            }
        } while (option != 3);
    }

}
