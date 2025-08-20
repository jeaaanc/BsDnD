package BankSdNd.example.BsDnD.bank;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.service.AccountService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BankProducts {
    private final AccountService accountService;

    //Valor a dividir do Saldo total do cliente
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    //Valor adicional para Cada 1 pts do cliente
    private static final BigDecimal TWO_HUNDRED = new BigDecimal("250");

    public BankProducts(AccountService accountService) {
        this.accountService = accountService;
    }



    public void moneyLoan(BankUser clientConfirmed){
        //  for every 100R$ get 1 PTS and for every 1PTS get 250R$ money loan.

        System.out.println("\n==== Empréstimo ====\n");
        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());

        BigDecimal totalBalance = BigDecimal.ZERO;



        for (int i = 0; i < accounts.size() ; i++) {
            Account acc = accounts.get(i);


            System.out.printf("\nConta: %d: %s: | Saldo: R$ %.2f%n",
                    i+1, acc.getAccountNumber(), acc.getBalance());
            totalBalance = totalBalance.add(acc.getBalance());

        }
        BigDecimal result = totalBalance.divide(ONE_HUNDRED, 2, RoundingMode.UP).multiply(TWO_HUNDRED);
        System.out.println("\nTotal Disponivel para Empréstimo: " + result);

        //acc.   setBalance(); !!!!  **** !!!!!


//        BigDecimal porcentagem = new BigDecimal("30");
//        BigDecimal total = new BigDecimal("500");
//
//        BigDecimal resultado = total.multiply(porcentagem).divide(CEM, 2, RoundingMode.HALF_UP);
// Isso calcula 30% de 500 → resultado: 150.00

    }
}
