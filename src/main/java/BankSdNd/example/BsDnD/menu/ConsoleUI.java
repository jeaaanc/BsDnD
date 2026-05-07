package BankSdNd.example.BsDnD.menu;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.util.CurrencyUtils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ConsoleUI {

    public void displayRegisterAll() {
        System.out.println("=====Cadastros=====");
        System.out.println("""
                1- Cadastrar nova Pessoa.
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
                6- Deletar conta.           
                9- Sair.
                0- Limpar a tela.
                """);
    }

    public void displayProfileMenu() {
        System.out.println("""
                1- Dados Pessoais
                2- Exibir contas
                3- Alterar Nome
                4- Alterar Senha de Login
                5- Alterar Senha de Transação
                6- Alterar Telefone
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
        System.out.println("\nConta criada com sucesso: " + account.getHolder().getName() + "\n");
    }

    public void showAccountValidationError(String message) {
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

    public void showLoanSuccess(Account updateAccount, BigDecimal requesAmount) {

        String formattedAmount = CurrencyUtils.formatToBrazilianCurrency(requesAmount);
        String formattedNewBalance = CurrencyUtils.formatToBrazilianCurrency(updateAccount.getBalance());

        System.out.println("\nEmpréstimo de " + formattedAmount + " concedido com sucesso!");
        System.out.println("Novo saldo na Conta " + updateAccount.getAccountNumber() + ": " + formattedNewBalance);
    }

    public void showLoanRequestError(String message) {
        System.out.println("\nNão foi possivel conceder o empréstimo: " + message + "\n");
    }
    // -----------------------------------------------

    // Menus v

    public void showMenuGoBack() {
        System.out.println("\nVoltando ao menu anterior.\n");
    }

    public void showChooseOptions() {
        System.out.println("\nEscolha uma das opções acima.\n");
    }

    public void showOptionInvalid() {
        System.out.println("\nOpção inválida\n");
    }

    // -----------------------------------------------

    // Tranfer v

    public void showTransferError(String message) {
        System.out.println("\nErro ao transferir: " + message + "\n");
    }

    public void showTransferSuccess() {
        System.out.println("\nTransferência realizada com sucesso.\n");
    }

    public void showTransferPasswordError() {
        System.out.println("\nConfirmação de senha Falhada. Tranferência cancelada.\n");
    }

    // -----------------------------------------------


    // Password v

    public void showPasswordValidationError() {
        System.out.println("\nConfirmação de senha falhou.Solicitação cancelada.\n");
    }

    public void showConfirmPassword() {
        System.out.println("\nPara continuar, por favor, confirme a sua senha.\n");
    }

    public void showPasswordNull() {
        System.out.println("\nOperação cancelada. Senha não fornecida.");
    }

    // -----------------------------------------------

    // Perfil v

    public void displayPersonalData(BankUser loggedInUser) {
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

    public void showChangeNameScreen() {
        System.out.println("\n===== Alteração de Nome e Sobrenome =====");
    }

    public void showNameChangeSuccess() {
        System.out.println("\nNome alterado com sucesso!");
    }

    public void showNameChangeError(String message) {
        System.out.println("Não foi possível alterar o nome: " + message);
    }

    public void showProfilePasswordMismatch() {
        System.out.println("\n As novas senha não coincide. Tente novamente.");
    }

    public void showProfilePasswordChangeSuccess() {
        System.out.println("\nSennha alterada com secesso!");
    }

    public void showProfilePasswordUpdateError(String message) {
        System.out.println("Não foi possivel alterar a senha: " + message);
    }

    public void showProfilePhoneChangeSuccess() {
        System.out.println("\nNúmero de telefone alterado com sucesso!");
    }

    public void showProfilePhoneUpdateError(String message) {
        System.out.println("Não foi possivel alterar o telefone: " + message);
    }

    public void showUserSessionExpired() {
        System.out.println("\nVocê foi desconectado. Por favor, faça o login novamente.");
    }
    // -----------------------------------------------

    // Delete v
    public void showDeleteAccountMenu() {
        System.out.println("\n===== Encerrar Conta =====");
    }

    public void showSuccess(String message) {
        System.out.println("" + message);
    }

    public void showError(String message) {
        System.out.println("" + message);
    }

    public void showAccessDeniedNoActiveAccount() {
        System.out.println("\n[Ação Negada] Você ainda não possui uma conta bancária ativa!");
        System.out.println("-> Por favor, use a opção '1- Criar conta' no menu principal primeiro.\n");
    }

    public void showInvalidOption() {
        System.out.println("Opção inválida.");
    }

    public void showAccountClosedSuccess() {
        System.out.println("Conta encerrada com sucesso!");
    }

    public void showAccountClosingError(String message) {
        System.out.println("Erro ao encerrar conta: " + message);
    }

    public void showTransferPasswordMismatch() {
        System.out.println("\nConfirmação de senha Falhada. Tranferência cancelada.\n");
    }

    public void showRetryOrCancelMenu() {
        System.out.println("1- Tentar novamente");
        System.out.println("2- Cancelar");
    }

    public void showOwnershipError() {
        System.out.println("Você não é o titular desta conta ou ela não existe.");
    }

    public void print(String message) {
        System.out.println("" + message);
    }


}