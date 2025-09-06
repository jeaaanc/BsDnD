package BankSdNd.example.BsDnD.controller;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.service.AccountService;
import BankSdNd.example.BsDnD.service.AuthService;
import BankSdNd.example.BsDnD.service.PersonService;
import BankSdNd.example.BsDnD.util.InputUtils;
import BankSdNd.example.BsDnD.util.PasswordUtils;
import BankSdNd.example.BsDnD.util.PersonInputCollector;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleController {
    private final PersonService personService;
    private final AccountService accountService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public ConsoleController(PersonService personService, AccountService accountService, AuthService authService, PasswordEncoder passwordEncoder) {
        this.personService = personService;
        this.accountService = accountService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    //Register >>>
    public void registerUserAccount(AccountService accountService, Scanner sc, ConsoleUI ui) {
        ui.showCreateAccount();

        String cpf = InputUtils.readString(sc, "Seu CPF: ");

        try {

            Account createdAccount = accountService.accountCreate(cpf);

            ui.showSucess("Conta criada com sucesso: " + createdAccount.getTitular().getName() + "\n");
        } catch (Exception e) {
            //Exeption!!!
            ui.showError("Erro: " + e.getMessage());
        }
    }

    public void registerUser(Scanner scanner, ConsoleUI ui) {
        int opcao = 1;

        ui.showCreateUser();
        do {
            PersonInputCollector collector = new PersonInputCollector(passwordEncoder);

            PersonDto dto = collector.collectUserInput(scanner);

            try {
                BankUser person = personService.savePerson(dto);
                System.out.println("\nNovo usuário criado com sucesso\n");
            } catch (IllegalArgumentException e) {
                System.out.println("Erro em validação: " + e.getMessage());
            }

            opcao = InputUtils.readInt(scanner, "\nDigite 1 para novo Cadastro ou 2 para sair: ");

        } while (opcao != 2);
    }
    //Register <<<

//Minha dica: se for manter com null, sempre documente no Javadoc que o método pode retornar null. Exemplo:

    public BankUser performLogin(Scanner sc, ConsoleUI ui) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            ui.showDisplayLogin();

            String cpf = InputUtils.readString(sc, "CPF: (ou digite 'sair' para cancelar): ");

            if ("sair".equalsIgnoreCase(cpf)) {
                System.out.println("\nLogin cancelado.");
                return null;
            }

            char[] rawPassword = null;
            try {

                rawPassword = PasswordUtils.catchPassword("Senha: ");

                if (rawPassword == null || rawPassword.length == 0) {

                    attempts++;
                    System.out.println("\nErro: Senha não pode ser vazia.");

                    if (attempts < MAX_ATTEMPTS) {
                        System.out.println("Você tem " + (MAX_ATTEMPTS - attempts) + " tentativa(s) restante(s).");
                    }
                    continue;
                }

                LoginDto loginDto = new LoginDto(cpf, rawPassword);
                BankUser loggedUser = authService.login(loginDto);

                ui.showSucess("\nLogin Efetuado com sucesso!\n");
                return loggedUser;

            } catch (InvalidPasswordException | UserNotFoundException | IllegalArgumentException e) {

                attempts++;
                System.out.println("\nErro: " + e.getMessage());
                if (attempts < MAX_ATTEMPTS) {
                    System.out.println("Você tem " + (MAX_ATTEMPTS - attempts) + " tentativa(s) restante(s).");
                }
            } finally {

                if (rawPassword != null) {
                    Arrays.fill(rawPassword, ' ');
                }
            }
        }

        System.out.println("\nNúmero máximo de " + MAX_ATTEMPTS + " tentativas atingido.");
        return null;
    }

    public void balance(BankUser clientConfirmed) {
        List<Account> accounts = accountService.searchClientAccount(clientConfirmed.getCpf());
        System.out.println("\nContas");

        for (int i = 0; i < accounts.size(); i++) {
            Account ac = accounts.get(i);
            System.out.printf("\nConta: %d: %s: | Saldo: R$ %.2f%n", i + 1, ac.getAccountNumber(), ac.getBalance());
        }
    }

    public void showTransferForm(BankUser clientConfirmed, Scanner scanner, ConsoleUI ui) {
        ui.showTransferMenu();

        askConfirmTransactionPassword(clientConfirmed);

        String accountOrigem = readAccountOrigem(clientConfirmed, scanner);
        String accountDestination = readContaDestino(scanner);

        BigDecimal valor = InputUtils.readBigDecimal(scanner, "Digite o valor da transferência: ");

        try {

            accountService.transfer(accountOrigem, accountDestination, valor);
            ui.showSucess(" Transferência realizada com sucesso!");

        } catch (InvalidPasswordException | IllegalArgumentException e) {
            ui.showError("Erro ao transferir: " + e.getMessage());
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

    //le a conta digita e ver se e o titular original
    private String readAccountOrigem(BankUser user, Scanner scanner) {
        String typedAccount = InputUtils.readString(scanner, "Digite o número da sua conta(origem):");
        accountService.validateAccountOwnership(user.getId(), typedAccount);

        return typedAccount;
    }

    private String readContaDestino(Scanner scanner) {
        return InputUtils.readString(scanner, "Digite o número da conta de destino: ");
    }

    //Alberto	Cunha	14521128009 / marcos	silva	12345678909

}


