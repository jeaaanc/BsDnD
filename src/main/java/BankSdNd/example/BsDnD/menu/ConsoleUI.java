package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.util.CurrencyUtils;
import BankSdNd.example.BsDnD.util.InputUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    public void displayRegisterAll() {
        System.out.println("=====Cadastros=====");
        System.out.println("""
                1- Cadastrar nova Pessoa.
                2- Fazer novo Pedido.
                9- sair.
                0- Limpar a tela.
                """);
    }

    public void firstDisplayMenu() {
        System.out.println("-----x-----x-----x-----x-----x-----x");
        System.out.println("----------MENU BANCO BSDND----------");
        System.out.println("-----x-----x-----x-----x-----x-----x");
        System.out.println("1- Cadastros");
        System.out.println("2- Login");
        System.out.println("3- sair");
        System.out.println("0- Limpar a tela.");
    }

    public void personChecked(BankUser clientConfirmed) {
        System.out.println("\nBem vindo: " + clientConfirmed.getName() + "\n");
        System.out.println("""
                1- Criar conta.
                2- Saldo.
                3- Transferência.
                4- Empréstimo.
                5- Meu Perfil.
                9- Sair.
                0- Limpar a tela.
                """);
    }

    public void displayProfileMenu() {
        System.out.println("""
                1- Dados Pessoais
                2- Exibir contas
                3- Alterar Nome
                4- Alterar Senha
                5- Alterar Telefone
                9- Voltar ao menu principal
                0- Limpar tela
                ===========================
                """);
    }


    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void displayAccountList(List<Account> accounts) {
        System.out.println("\n===== Suas Contas =====\n");
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("Você ainda não possui contas bancárias.");

        } else {
            for (int i = 0; i < accounts.size(); i++) {
                Account acc = accounts.get(i);

                String formattedBalance = CurrencyUtils.formatToBrazilianCurrency(acc.getBalance());
                System.out.printf(" %d: Conta %s | Saldo: %s%n",
                        i + 1,
                        acc.getAccountNumber(),
                        formattedBalance);
            }
        }
        System.out.println("==================");
    }

    public void showMoneyLoan() {
        System.out.println("\n==== Empréstimo ====\n");
    }

    public void showTransferMenu() {
        System.out.println("==== Transferência ====");
    }

    public void showCreateAccount() {
        System.out.println("=======Criar Nova Conta=======");
    }


    // Login v

    public void showDisplayLogin() {
        System.out.println("\n========Login========\n");
    }

    public void showLoginCancelled() {
        System.out.println("\nLogin cancelado.");
    }

    public void showLoginSuccessfully() {
        System.out.println("\nLogin Efetuado com sucesso\n");
    }

    public void showAttemptsRemaining(int remainingAttempts) {
        System.out.println("\nVocê tem: " + remainingAttempts + " tentativa(s) restante(s).\n");
    }

    public void showMaxAttemptsReached() {
        System.out.println("\nNúmero máximo de tentativas atingido. Acesso bloqueado");
    }

    // -----------------------------------------------


    // Register User v

    public void showCreateUser() {
        System.out.println("==== Criar novo usuário ====");
    }

    public void showUserCreatedSuccessfully() {
        System.out.println("\nNovo usuário criado com sucesso\n");
    }

    public void showValidationError(String message) {
        System.out.println("\nErro em validação: " + message + "\n");
    }

    public void showRegisterError() {
        System.out.println("\nCadastro cancelado.\n");
    }

    // -----------------------------------------------

    // Account v

    public void accountShowPasswordValidation() {
        System.out.println("\nA confirmação da senha Falhou. Criação de conta cancelada.\n");
    }

    public void accountCreatedSuccessfully(Account account) {
        System.out.println("\nConta criada com sucesso: " + account.getTitular().getName() + "\n");
    }

    public void accountValidationShowError(String message) {
        System.out.println("\nErro: " + message + "\n");
    }

    // -----------------------------------------------

    // Loan v
    public void loanShowLimitFormated(String formattedResult) {
        System.out.println("\nLimite total para emprestimo: " + formattedResult + "\n");
    }

    public void loanRequestShowCanceled() {
        System.out.println("\nSolicitação de empréstimo cancelada.\n");
    }

    public void showLoanSucess(Account updateAccount, BigDecimal requesAmount) {

        String formattedAmount = CurrencyUtils.formatToBrazilianCurrency(requesAmount);
        String formattedNewBalance = CurrencyUtils.formatToBrazilianCurrency(updateAccount.getBalance());

        System.out.println("\nEmpréstimo de " + formattedAmount + " concedido com sucesso!");
        System.out.println("Novo saldo na Conta " + updateAccount.getAccountNumber() + ": " + formattedNewBalance);
    }

    public void showResquestLoanErro(String message) {
        System.out.println("\nNão foi possivel conceder o empréstimo: " + message + "\n");
    }
    // -----------------------------------------------

    // Menus v

    public void showMenuGoBack() {
        System.out.println("\nVoltando ao menu anterior.\n");
    }

    public void showChoseOptions() {
        System.out.println("\nEscolha uma das opções acima.\n");
    }

    public void showOptionInvalid() {
        System.out.println("\nOpção inválida\n");
    }

    // -----------------------------------------------

    // Tranfer v

    public void showErroTransfer(String message) {
        System.out.println("\nErro ao transferir: " + message + "\n");
    }

    public void showTranferSuccessfully() {
        System.out.println("\nTransferência realizada com sucesso.\n");
    }

    public void showTranferErroValidationPassword() {
        System.out.println("\nConfirmação de senha Falhada. Tranferência cancelada.\n");
    }

    // -----------------------------------------------


    // Password v

    public void showPasswordValidationError() {
        System.out.println("\nConfirmação de senha falhou.Solicitação cancelada.\n");
    }

    public void showConfimedPassword() {
        System.out.println("\nPara continuar, por favor, confirme a sua senha.\n");
    }

    public void showPasswordNull() {
        System.out.println("\nOperação cancelada. Senha não fornecida.");
    }

    // -----------------------------------------------

    // Perfil v

    public void displayPersonalData(BankUser loggedInUser){
            System.out.println("\n===== Seus Dados Pessoais =====");
            System.out.println("Nome: " + loggedInUser.getName());
            System.out.println("Sobrenome: " + loggedInUser.getLastName());
            System.out.println("CPF: " + loggedInUser.getCpf());
            System.out.println("Telefone: " + loggedInUser.getPhoneNumber() + "\n");
    }

    public void showProfileHeader(BankUser loggedInUser) {
        System.out.println("\n===== Perfil de " + loggedInUser.getName() + " " +
                loggedInUser.getLastName() + " =====\n");
    }

    public void showChangePasswordScreen() {
        System.out.println("\n===== Alteração de senha =====");
    }

    public void showChangePhonenumberScreen() {
        System.out.println("\n===== Alteração de Telefone =====");
    }

    public void showChangeNameScreen(){
        System.out.println("\n===== Alteração de Nome e Sobrenome =====");
    }

    public void showNameChangedSuccessFully(){
        System.out.println("\nNome alterado com sucesso!");
    }

    public void showNameChangeError(String message){
        System.out.println("Não foi possível alterar o nome: " + message);
    }

    public void showProfilePasswordMismatch() {
        System.out.println("\n As novas senha não coincide. Tente novamente.");
    }

    public void showProfilePasswordChangeSuccessfully() {
        System.out.println("\nSennha alterada com secesso!");
    }

    public void showProfilePasswordUpdateError(String message){
        System.out.println("Não foi possivel alterar a senha: " + message);
    }

    public void showProfilePhoneChangedSuccessfully() {
        System.out.println("\nNúmero de telefone alterado com sucesso!");
    }

    public void showProfilePhoneUpdateError(String message){
        System.out.println("Não foi possivel alterar o telefone: "+ message);
    }

    public void showUserSessionExpired(){
        System.out.println("\nVocê foi desconectado. Por favor, faça o login novamente.");
    }
    // -----------------------------------------------


    public void showSucess(String message) {
        System.out.println("" + message);
    }

    public void showError(String message) {
        System.out.println("" + message);
    }

    public void print(String message) {
        System.out.println("" + message);
    }


}