package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.model.Account;
import BankSdNd.example.BsDnD.model.BankUser;
import BankSdNd.example.BsDnD.service.AccountService;

import java.util.List;
import java.util.Scanner;

public class ClientMenu {

    public void displayRegisterAll() {
        System.out.println("=====Cadastros=====");
        System.out.println("""
                1- Cadastrar nova Pessoa.
                2- Cadastrar nova Conta.
                3- Cadastrar nova @@@
                """);
    }

    public void firstDisplayMenu() {
        System.out.println("-----x-----x-----x-----x-----x-----x");
        System.out.println("----------MENU BANCO BSDND----------");
        System.out.println("-----x-----x-----x-----x-----x-----x");
        System.out.println("1- Cadastros");
        System.out.println("2- Login");
        System.out.println("3- sair");
    }

}