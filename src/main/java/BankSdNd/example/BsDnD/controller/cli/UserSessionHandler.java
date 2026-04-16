package BankSdNd.example.BsDnD.controller.cli;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.util.InputUtils;
import org.springframework.stereotype.Component;
import java.util.Scanner;

/**
 * Controller responsible for managing the application's flow and logic for an authenticated user.
 * It handles the user session, displaying menus for logged-in actions and delegating
 * tasks to specialized handlers.
 */
@Component
public class UserSessionHandler {

    private final AccountOperationHandler accountOperationHandler;
    private final UserProfileHandler userProfileHandler;
    private final LoanHandler loanHandler;

    private final Scanner sc;
    private final ConsoleUI ui;

    private BankUser currentUser;

    public UserSessionHandler(AccountOperationHandler accountOperationHandler,
                              UserProfileHandler userProfileHandler,
                              LoanHandler loanHandler,
                              Scanner sc,
                              ConsoleUI ui) {
        this.accountOperationHandler = accountOperationHandler;
        this.userProfileHandler = userProfileHandler;
        this.loanHandler = loanHandler;
        this.sc = sc;
        this.ui = ui;
    }

    /**
     * Starts and manages the main loop for a logged-in user's session.
     * This method displays the main menu of actions and delegates tasks to other handlers based on user input.
     * The session ends when the user chooses to log out.
     *
     * @param loggedInUser The authenticated {@code BankUser} object representing the current user.
     */
    public void runUserSession(BankUser loggedInUser) {
        this.currentUser = loggedInUser;
        boolean loggedIn = true;

        while (loggedIn) {
            ui.personChecked(this.currentUser);
            int choice = InputUtils.readInt(sc, "Escolha uma opção: ");

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
                default -> ui.showChoseOptions();
            }
        }
        this.currentUser = null;
        ui.showUserSessionExpired();
    }
}
