package BankSdNd.example.BsDnD.util;

import javax.swing.*;


/**
 * A utility class for securely capturing user passwords.
 *
 * This class provides a method to read a password without echoing it to the screen,
 * automatically selecting the best available method based on the execution environment
 * (either a graphical Swing dialog or the system console).
 * This class cannot be instantiated or extended.
 */
public final class PasswordUtils {

    private PasswordUtils() {
        throw new IllegalArgumentException("Utility class");
    }


    /**
     * Securely captures a password from the user.
     *
     * It automatically detects if a graphical environment is available. If so, it displays
     * a {@code JPasswordField} dialog. Otherwise, it falls back to using the system console,
     * which hides user input. The returned char array should be cleared by the caller
     * as soon as it is no longer needed.
     *
     * @param message The prompt message to be displayed to the user.
     * @return A char array ({@code char[]}) containing the entered password, or {@code null} if the operation is canceled.
     * @throws IllegalStateException if the application is running in a headless environment with no interactive console available.
     */
    public static char[] catchPassword(String message) {
        java.io.Console console = System.console();

        boolean isHeadless = java.awt.GraphicsEnvironment.isHeadless();
        if (!isHeadless) {
            return catchPasswordSwing(message);

        } else if (console != null) {

            return catchPasswordConsole(console, message);
        } else {

            throw new IllegalArgumentException("Não é possivel capturar a senha: " +
                    "o ambiente não tem GUI e não há um console interativo disponivel.");
        }

    }

    // Helper method for console input.
    private static char[] catchPasswordConsole(java.io.Console console, String message) {

        char[] passwordChar = console.readPassword("%s: ", message);
        return passwordChar != null ? passwordChar : null;
    }


    // Helper method for Swing GUI input
    private static char[] catchPasswordSwing(String message) {

        JPasswordField passwordField = new JPasswordField(16);

        int option = JOptionPane.showConfirmDialog(
                null, passwordField, message,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {

            return passwordField.getPassword();
        }
        return null;
    }
}
