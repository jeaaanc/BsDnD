package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.domain.BankUser;

public class ConsoleUI {

    public void displayRegisterAll() {
        System.out.println("=====Cadastros=====");
        System.out.println("""
                1- Cadastrar nova Pessoa.
                2- Cadastrar nova Conta.
                3- Fazer novo Pedido.
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

    public void personChecked(BankUser clientConfirmed){
        System.out.println("\nBem vindo: " + clientConfirmed.getName());
        System.out.println("""
                1- Saldo.
                2- Transferência.
                3- Empréstimo
                """);
    }

    public void showMoneyLoan(){
        System.out.println("\n==== Empréstimo ====\n");
    }
    public void showTransferMenu(){
        System.out.println("==== Transferência ====");
    }

    public void showCreateAccount(){
        System.out.println("=======Criar Nova Conta=======");
    }

    public void showDisplayLogin(){
        System.out.println("\n========Login========\n");
    }

    public void showCreateUser(){
        System.out.println("==== Criar novo usuário ====");
    }
    public void showSucess(String message){
        System.out.println("" + message);
    }

    public void showError(String message){
        System.out.println("" + message);
    }

}