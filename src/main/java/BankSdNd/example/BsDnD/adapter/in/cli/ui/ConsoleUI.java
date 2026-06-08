package BankSdNd.example.BsDnD.adapter.in.cli.ui;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.adapter.in.cli.util.CurrencyUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ConsoleUI {

    private final MessageSource messageSource;

    public ConsoleUI(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public void displayRegisterAll() {
        System.out.println(getMessage("menu.register.title"));
        System.out.println(getMessage("menu.register.option.person"));
        System.out.println(getMessage("menu.register.option.exit"));
        System.out.println(getMessage("menu.option.clear"));
    }

    public void firstDisplayMenu() {
        System.out.println(getMessage("menu.divider"));
        System.out.println(getMessage("menu.title"));
        System.out.println(getMessage("menu.divider"));
        System.out.println(getMessage("menu.option.register"));
        System.out.println(getMessage("menu.option.login"));
        System.out.println(getMessage("menu.option.exit"));
        System.out.println(getMessage("menu.option.clear"));
    }

    public void personChecked(BankUser clientConfirmed) {
        System.out.println(getMessage("menu.user.welcome", clientConfirmed.getName()));
        System.out.println(getMessage("menu.user.option.create_account"));
        System.out.println(getMessage("menu.user.option.balance"));
        System.out.println(getMessage("menu.user.option.transfer"));
        System.out.println(getMessage("menu.user.option.loan"));
        System.out.println(getMessage("menu.user.option.profile"));
        System.out.println(getMessage("menu.user.option.delete_account"));
        System.out.println(getMessage("menu.user.option.logout"));
        System.out.println(getMessage("menu.option.clear"));
    }

    public void displayProfileMenu() {
        System.out.println(getMessage("menu.profile.option.data"));
        System.out.println(getMessage("menu.profile.option.accounts"));
        System.out.println(getMessage("menu.profile.option.change_name"));
        System.out.println(getMessage("menu.profile.option.change_password"));
        System.out.println(getMessage("menu.profile.option.change_transaction_password"));
        System.out.println(getMessage("menu.profile.option.change_phone"));
        System.out.println(getMessage("menu.profile.option.back"));
        System.out.println(getMessage("menu.profile.option.clear"));
        System.out.println("===========================");
    }


    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void displayAccountList(List<Account> accounts) {
        System.out.println(getMessage("account.list_header"));
        if (accounts == null || accounts.isEmpty()) {
            System.out.println(getMessage("account.list_empty"));

        } else {
            for (int i = 0; i < accounts.size(); i++) {
                Account acc = accounts.get(i);

                String formattedBalance = CurrencyUtils.formatCurrency(acc.getBalance());
                System.out.println(getMessage("account.list_item", 
                        i + 1, 
                        acc.getAccountNumber(), 
                        formattedBalance));
            }
        }
        System.out.println("==================");
    }

    public void showMoneyLoan() {
        System.out.println(getMessage("loan.header"));
    }

    public void showTransferMenu() {
        System.out.println(getMessage("transfer.header"));
    }

    public void showCreateAccount() {
        System.out.println(getMessage("account.header"));
    }


    // Login v

    public void showDisplayLogin() {
        System.out.println(getMessage("login.header"));
    }

    public void showLoginCancelled() {
        System.out.println(getMessage("login.cancelled"));
    }

    public void showLoginSuccessfully() {
        System.out.println(getMessage("login.success"));
    }

    public void showAttemptsRemaining(int remainingAttempts) {
        System.out.println(getMessage("login.attempts_remaining", remainingAttempts));
    }

    public void showMaxAttemptsReached() {
        System.out.println(getMessage("login.max_attempts"));
    }

    // -----------------------------------------------


    // Register User v

    public void showCreateUser() {
        System.out.println(getMessage("user.register.header"));
    }

    public void showUserCreatedSuccessfully() {
        System.out.println(getMessage("user.register.success"));
    }

    public void showValidationError(String message) {
        System.out.println(getMessage("account.validation_error", message));
    }

    public void showRegisterError() {
        System.out.println(getMessage("user.register.error"));
    }

    // -----------------------------------------------

    // Account v

    public void accountShowPasswordValidation() {
        System.out.println(getMessage("account.password_validation_fail"));
    }

    public void accountCreatedSuccessfully(Account account) {
        System.out.println(getMessage("account.created_success", account.getHolder().getName()));
    }

    public void showAccountValidationError(String message) {
        System.out.println(getMessage("account.validation_error", message));
    }

    // -----------------------------------------------

    // Loan v
    public void loanShowLimitFormated(String formattedResult) {
        System.out.println(getMessage("loan.limit", formattedResult));
    }

    public void loanRequestShowCanceled() {
        System.out.println(getMessage("loan.cancelled"));
    }

    public void showLoanSuccess(Account updatedAccount, BigDecimal requestedAmount) {

        String formattedAmount = CurrencyUtils.formatCurrency(requestedAmount);
        String formattedNewBalance = CurrencyUtils.formatCurrency(updatedAccount.getBalance());

        System.out.println(getMessage("loan.success", formattedAmount));
        System.out.println(getMessage("loan.new_balance", updatedAccount.getAccountNumber(), formattedNewBalance));
    }

    public void showLoanRequestError(String message) {
        System.out.println(getMessage("loan.error", message));
    }
    // -----------------------------------------------

    // Menus v

    public void showMenuGoBack() {
        System.out.println(getMessage("generic.go_back"));
    }

    public void showChooseOptions() {
        System.out.println(getMessage("generic.choose_option"));
    }

    public void showOptionInvalid() {
        System.out.println(getMessage("generic.invalid_option"));
    }

    // -----------------------------------------------

    // Tranfer v

    public void showTransferError(String message) {
        System.out.println(getMessage("transfer.error", message));
    }

    public void showTransferSuccess() {
        System.out.println(getMessage("transfer.success"));
    }

    public void showTransferPasswordError() {
        System.out.println(getMessage("transfer.password_error"));
    }

    // -----------------------------------------------


    // Password v

    public void showPasswordValidationError() {
        System.out.println(getMessage("validation.password_fail"));
    }

    public void showConfirmPassword() {
        System.out.println(getMessage("validation.password_confirm"));
    }

    public void showPasswordNull() {
        System.out.println(getMessage("validation.password_null"));
    }

    // -----------------------------------------------

    // Perfil v

    public void displayPersonalData(BankUser loggedInUser) {
        System.out.println(getMessage("profile.personal_data.header"));
        System.out.println(getMessage("profile.personal_data.name", loggedInUser.getName()));
        System.out.println(getMessage("profile.personal_data.last_name", loggedInUser.getLastName()));
        System.out.println(getMessage("profile.personal_data.cpf", loggedInUser.getCpf()));
        System.out.println(getMessage("profile.personal_data.phone", loggedInUser.getPhoneNumber()));
        System.out.println();
    }

    public void showProfileHeader(BankUser loggedInUser) {
        System.out.println(getMessage("menu.profile.title", loggedInUser.getName(), loggedInUser.getLastName()));
    }

    public void showChangePasswordScreen() {
        System.out.println(getMessage("profile.change_password.header"));
    }

    public void showChangePhonenumberScreen() {
        System.out.println(getMessage("profile.change_phonenumber.header"));
    }

    public void showChangeNameScreen() {
        System.out.println(getMessage("profile.change_name.header"));
    }

    public void showNameChangeSuccess() {
        System.out.println(getMessage("profile.name.success"));
    }

    public void showNameChangeError(String message) {
        System.out.println(getMessage("profile.name.error", message));
    }

    public void showProfilePasswordMismatch() {
        System.out.println(getMessage("profile.password.mismatch"));
    }

    public void showProfilePasswordChangeSuccess() {
        System.out.println(getMessage("profile.password.success"));
    }

    public void showProfilePasswordUpdateError(String message) {
        System.out.println(getMessage("profile.password.error", message));
    }

    public void showProfilePhoneChangeSuccess() {
        System.out.println(getMessage("profile.phone.success"));
    }

    public void showProfilePhoneUpdateError(String message) {
        System.out.println(getMessage("profile.phone.error", message));
    }

    public void showUserSessionExpired() {
        System.out.println(getMessage("validation.session_expired"));
    }
    // -----------------------------------------------

    // Delete v
    public void showDeleteAccountMenu() {
        System.out.println(getMessage("account.delete_header"));
    }

    public void showSuccess(String message) {
        System.out.println(getMessage("generic.success", message));
    }

    public void showError(String message) {
        System.out.println(getMessage("generic.error", message));
    }

    public void showAccessDeniedNoActiveAccount() {
        System.out.println(getMessage("account.no_active_account"));
        System.out.println(getMessage("account.no_active_account_hint"));
    }

    public void showInvalidOption() {
        System.out.println(getMessage("generic.invalid_option"));
    }

    public void showAccountClosedSuccess() {
        System.out.println(getMessage("account.closed_success"));
    }

    public void showAccountClosingError(String message) {
        System.out.println(getMessage("account.closing_error", message));
    }

    public void showTransferPasswordMismatch() {
        System.out.println(getMessage("transfer.password_error"));
    }

    public void showRetryOrCancelMenu() {
        System.out.println(getMessage("prompt.retry_or_cancel_1"));
        System.out.println(getMessage("prompt.retry_or_cancel_2"));
    }

    public void showOwnershipError() {
        System.out.println(getMessage("account.ownership_error"));
    }

    public void print(String message) {
        System.out.println(message);
    }


}
