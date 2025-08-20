package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.PersonService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleController {
    private final PersonService personService;
    private final AccountService accountService;

    public ConsoleController(PersonService personService, AccountService accountService) {
        this.personService = personService;
        this.accountService = accountService;
    }


    //-
    public BankUser displayLogin(Scanner sc){

        System.out.println("\n========Login========\n");

        System.out.println("\nCPF: ");
        String cpf = sc.nextLine();

        System.out.println("\nSenha: ");
        String rawPassword = sc.nextLine();

        LoginDto loginDto = new LoginDto(cpf, rawPassword);

        try {
            BankUser person = personService.login(loginDto);
            System.out.println("\nLogin Efetuado com sucesso.\n");
            return person;
        }catch (IllegalArgumentException e){
            System.out.println("\nErro: " + e.getMessage());
            return null;
        }


    }

    //--
    public void youBalance(BankUser clientConfirmed){
        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());
        System.out.println("\nContas");

        for (int i = 0; i < accounts.size(); i++) {
            Account ac = accounts.get(i);
            System.out.printf("\nConta: %d: %s: | Saldo: R$ %.2f%n", i + 1, ac.getAccountNumber(), ac.getBalance());
        }

    }
    //---
    public void balance(BankUser clientConfirmed){
        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());
        System.out.println("\nContas");

        for (int i = 0; i < accounts.size(); i++) {
            Account ac = accounts.get(i);
            System.out.printf("\nConta: %d: %s: | Saldo: R$ %.2f%n", i + 1, ac.getAccountNumber(), ac.getBalance());
        }
    }
    //----
    public void transfer(BankUser clientConfirmed, Scanner scanner){
        System.out.println("==== Transferência ====");
        System.out.println("Digite sua senha para confirmar: ");

        String senhadigitada = scanner.nextLine();

        if (!senhadigitada.equals(clientConfirmed.getPassword())){
            System.out.println("Senha incorreta! Transferência cancelada.");
            return;
        }

        System.out.print("Digite o número da sua conta (origem): ");
        String contaOrigem = scanner.nextLine();

        System.out.print("Digite o número da conta de destino: ");
        String contaDestino = scanner.nextLine();

        System.out.print("Digite o valor da transferência: ");
        BigDecimal valor = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            accountService.transfer(contaOrigem, contaDestino, valor);
            System.out.println(" Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao transferir: " + e.getMessage());
        }

    }
    //-----
        public void registerUserAccount (AccountService accountService, Scanner sc){

            System.out.println("=======Create a new Account=======");

            System.out.println("enter your CPF");
            String cpf = sc.nextLine();
            System.out.println("enter the account number");
            String accountNumber = sc.nextLine();

            try {
                Account createdAccount = accountService.accountCreate(cpf, accountNumber);
                System.out.println("Account created successfully: " + createdAccount.getTitular().getName() +"\n");
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        public void registerUser(Scanner scanner){
            int opcao = 1;

            do {
                System.out.println("\n==== Create a new USER ====\n");
                System.out.println("Nome:");
                String name = scanner.nextLine();
                System.out.println("Sobrenome:");
                String lastName = scanner.nextLine();

                System.out.println("CPF:");
                String cpf = scanner.nextLine();

                System.out.println("Celular:");
                String phoneNumber = scanner.nextLine();

                System.out.println("password:");
                String rawPassword = scanner.nextLine();
                System.out.println("Confirm password:");
                String confirmedRawPassword = scanner.nextLine();

                System.out.println("income: ");
                BigDecimal income = scanner.nextBigDecimal();


                PersonDto dto = new PersonDto(name, lastName, cpf, income, phoneNumber, rawPassword, confirmedRawPassword);

                try {
                    BankUser person = personService.savePerson(dto);
                    System.out.println("\nNew user created successfully\n");
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro in validation: " + e.getMessage());
                }

                System.out.println("\n1/ Para fazer outro cadastro ou 2/ para Finalizar");
                opcao = scanner.nextInt();
                scanner.nextLine();

            } while (opcao != 2);
        }
    }


