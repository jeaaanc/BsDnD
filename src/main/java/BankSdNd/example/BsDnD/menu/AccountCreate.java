package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.model.Account;
import BankSdNd.example.BsDnD.repository.AccountRepository;
import BankSdNd.example.BsDnD.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Scanner;

public class AccountCreate {
    @Autowired
    private AccountService accountService;

    public AccountCreate(AccountService accountService) {
        this.accountService = accountService;
    }

    public void registerUserAccount(Scanner sc){

        System.out.println("=======Criar uma nova conta=======");

        System.out.println("Digite o CPF");
        String cpf = sc.nextLine();
        System.out.println("Digite o número da conta");
        String accountNumber = sc.nextLine();
        System.out.println("Digite o saldo inicial");
        BigDecimal balance = sc.nextBigDecimal();

        try {
            Account createdAccount = accountService.accountCreate(cpf, accountNumber, balance);
            System.out.println("Conta criada com SUCESSO: " + createdAccount.getTitular().getName());
        }catch (Exception e){
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
