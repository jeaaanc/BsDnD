package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.LoanService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserSessionHandler {

    private final AccountService accountService;
    private final LoanService loanService;
    private final AuthService authService;

    public UserSessionHandler(AccountService accountService, LoanService loanService, AuthService authService) {
        this.accountService = accountService;
        this.loanService = loanService;
        this.authService = authService;
    }

    public void runUserSession(BankUser loggedInUser, Scanner sc, ConsoleUI ui) {
        boolean loggedIn = true;

        while (loggedIn) {
            ui.personChecked(loggedInUser);
            int choice = InputUtils.readInt(sc, "Escolha uma opção");
            switch (choice) {
                case 1 -> registerUserAccount(loggedInUser, sc, ui);
                case 2 -> balance(loggedInUser);
                case 3 -> showTransferForm(loggedInUser, sc, ui);
                case 4 -> handleLoanRequest(loggedInUser, sc, ui);
                case 9 -> loggedIn = false;
                case 0 -> ui.clearScreen();
                default -> ui.showError("Escolha uma das opções acima");
            }
        }
    }

    public void registerUserAccount(BankUser loggedInUser, Scanner sc, ConsoleUI ui) {
        ui.showCreateAccount();

        String cpf = InputUtils.readString(sc, "Seu CPF: ");

        boolean isPasswordConfirmed = askConfirmTransactionPassword(loggedInUser);
        if (!isPasswordConfirmed){
            ui.showError("A confirmação da senha Falhou. Criação de conta cancelada.");
            return;
        }

        try {

            Account createdAccount = accountService.accountCreate(cpf);

            ui.showSucess("Conta criada com sucesso: " + createdAccount.getTitular().getName() + "\n");
        } catch (Exception e) {
            //Exeption!!!
            ui.showError("Erro: " + e.getMessage());
        }
    }

    public void handleLoanRequest(BankUser loggedInUser, Scanner scanner, ConsoleUI ui) {

        BigDecimal limit = loanService.calculateLoanLimit(loggedInUser);
        //arrumar info ui !!!!
        ui.showMoneyLoan();

        BigDecimal requesAmount = InputUtils.readBigDecimal(scanner, "Digite o valor que deseja solicitar" +
                "(ou 0 para cancelar): ");

        if (requesAmount.compareTo(BigDecimal.ZERO) == 0) {
            ui.print("Solicitação de empréstimo cancelada.");
            return;
        }

        boolean isPasswordConfirmed = askConfirmTransactionPassword(loggedInUser);
        if (!isPasswordConfirmed) {
            ui.showError("Confirmação de senha falhou. Solicitação cancelada.");
            return;
        }

        try {
            Account updateAccount = loanService.grantLoan(loggedInUser, requesAmount);
            ui.showSucess("Emprestimo de R$" + requesAmount + " concedido com sucesso!");
            ui.showSucess("Novo saldo na Conta " + updateAccount.getAccountNumber() + " : R$" + updateAccount.getBalance());
        } catch (Exception e) {
            ui.showError("Não foi possivel conceder o empréstimo: " + e.getMessage());
        }
    }

    public void showTransferForm(BankUser clientConfirmed, Scanner scanner, ConsoleUI ui) {
        ui.showTransferMenu();

        boolean isPasswordIsConfirmed = askConfirmTransactionPassword(clientConfirmed);

        if (!isPasswordIsConfirmed) {
            ui.showError("Confirmação de senha Falhada. Tranferência cancelada. ");
            return;
        }

        String accountOrigem = readAccountOrigem(clientConfirmed, scanner);
        // !!!! processo para se errar a conta de origem
        String accountDestination = readContaDestino(scanner);

        BigDecimal valor = InputUtils.readBigDecimal(scanner, "Digite o valor da transferência: ");

        try {

            accountService.transfer(accountOrigem, accountDestination, valor);
            ui.showSucess(" Transferência realizada com sucesso!");

        } catch (InvalidPasswordException | IllegalArgumentException e) {
            ui.showError("Erro ao transferir: " + e.getMessage());
        }
    }


    //Minha dica: se for manter com null, sempre documente no Javadoc que o método pode retornar null. Exemplo:


    public void balance(BankUser clientConfirmed) {
        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());
        System.out.println("\nContas");

        for (int i = 0; i < accounts.size(); i++) {
            Account ac = accounts.get(i);
            System.out.printf("\nConta: %d: %s: | Saldo: R$ %.2f%n", i + 1, ac.getAccountNumber(), ac.getBalance());
        }
    }

    private boolean askConfirmTransactionPassword(BankUser user) {

        char[] typedPassword = null;
        try {

            System.out.println("\nPara continuar, por favor, confirme a sua senha.");

            typedPassword = PasswordUtils.catchPassword("Senha: ");

            if (typedPassword == null || typedPassword.length == 0) {
                System.out.println("Operação cancelada. Senha não fornecida.");
                return false;
            }

            authService.validatePassword(user.getId(), typedPassword);
            return true;

        } catch (InvalidPasswordException | UserNotFoundException e) {
            System.out.println("Erro na confirmação: " + e.getMessage());
            return false;
        } finally {

            if (typedPassword != null) {
                Arrays.fill(typedPassword, '\0');
            }
        }
    }


    private String readAccountOrigem(BankUser user, Scanner scanner) {
        String typedAccount = InputUtils.readString(scanner, "Digite o número da sua conta(origem):");
        accountService.validateAccountOwnership(user.getId(), typedAccount);

        return typedAccount;
    }

    private String readContaDestino(Scanner scanner) {
        return InputUtils.readString(scanner, "Digite o número da conta de destino: ");
    }
}
