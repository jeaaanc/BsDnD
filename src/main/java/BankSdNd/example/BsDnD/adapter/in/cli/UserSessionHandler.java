package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import org.springframework.stereotype.Component;
import java.util.Scanner;

/**
 * Controller responsible for managing the application's flow and logic for an authenticated user.
 */
@Component
public class UserSessionHandler {

    private final AccountOperationHandler accountOperationHandler;
    private final UserProfileHandler userProfileHandler;
    private final LoanHandler loanHandler;
    private final InputUtils inputUtils;

    private final Scanner sc;
    private final ConsoleUI ui;

    private BankUser currentUser;

    public UserSessionHandler(AccountOperationHandler accountOperationHandler,
                              UserProfileHandler userProfileHandler,
                              LoanHandler loanHandler,
                              InputUtils inputUtils,
                              Scanner sc,
                              ConsoleUI ui) {
        this.accountOperationHandler = accountOperationHandler;
        this.userProfileHandler = userProfileHandler;
        this.loanHandler = loanHandler;
        this.inputUtils = inputUtils;
        this.sc = sc;
        this.ui = ui;
    }

    /**
     * Starts and manages the main loop for a logged-in user's session.
     * @param loggedInUser The authenticated {@code BankUser} object representing the current user.
     */
    public void runUserSession(BankUser loggedInUser) {
        this.currentUser = loggedInUser;
        boolean loggedIn = true;

        while (loggedIn) {
            ui.personChecked(this.currentUser);
            int choice = inputUtils.readInt(sc, ui.getMessage("prompt.choose_option"));

            switch (choice) {
                case 1 -> accountOperationHandler.registerUserAccount(this.currentUser);
                case 2 -> accountOperationHandler.balance(this.currentUser);
                case 3 -> accountOperationHandler.showTransferForm(this.currentUser);
                case 4 -> loanHandler.handleLoanRequest(this.currentUser);
                case 5 -> {
                    this.currentUser = userProfileHandler.showUserProfile(this.currentUser);
                    if (this.currentUser == null) {
                        loggedIn = false;
                    }
                }
                case 6 -> accountOperationHandler.handleAccountDeletion(this.currentUser);
                case 9 -> loggedIn = false;
                case 0 -> ui.clearScreen();
                default -> ui.showChooseOptions();
            }
        }
        this.currentUser = null;
        ui.showUserSessionExpired();
    }
}
