package BankSdNd.example.BsDnD.util;

import javax.swing.*;
import java.util.Arrays;

public class PasswordUtils {

    private PasswordUtils() {
        throw new IllegalArgumentException("Esta é uma classe utilitária e não pode ser instanciada.");
    }

    /**
     * Captura uma senha do usuário de forma segura, seja via GUI (Swing) ou Console.
     * Retorna a senha como uma String, mas minimiza a exposição da senha na memória
     * limpando o array de caracteres intermediário.
     *
     * @param messagem A mensagem a ser exibida para o usuário.
     * @return A senha como String, ou null se a operação for cancelada.
     */

    public static char[] catchPassword(String messagem) {
        java.io.Console console = System.console();

        boolean isHeadless = java.awt.GraphicsEnvironment.isHeadless();
        if (!isHeadless) {
            return catchPasswordSwing(messagem);

        } else if (console != null) {

            return catchPasswordConsole(console, messagem);
        } else {

            throw new IllegalArgumentException("Não é possivel capturar a senha: " +
                    "o ambiente não tem GUI e não há um console interativo disponivel.");
        }

    }

    private static char[] catchPasswordConsole(java.io.Console console, String message) {

        char[] passwordChar = console.readPassword("%s: ", message);
        return passwordChar != null ? passwordChar : null;
    }

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
