package BankSdNd.example.BsDnD.util;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputUtils {

    public static String readString(Scanner sc, String mensagem) {
        System.out.println(mensagem);
        return sc.nextLine();
    }

    public static int readInt(Scanner sc, String mensagem) {
        while (true) {

            try {
                System.out.println(mensagem);
                return Integer.parseInt(sc.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número Inteiro. ");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner sc, String mensagem) {
        while (true) {

            try {
                System.out.println(mensagem);
                return new BigDecimal(sc.nextLine().replace(",", "."));

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um valor numérico.");
                sc.nextLine();
            }
        }
    }


}
