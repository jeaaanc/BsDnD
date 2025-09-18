package BankSdNd.example.BsDnD.util;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputUtils {

    public static String readString(Scanner sc, String mensagem) {
        System.out.print(mensagem);
        return sc.nextLine();
    }

    public static int readInt(Scanner sc, String mensagem) {
        while (true) {

            try {

                System.out.print(mensagem);
                int value = Integer.parseInt(sc.nextLine());
                return value;

            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número Inteiro. ");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = sc.nextLine();
            try {
                String noPoint = input.replace(".", "");
                String formatJava = noPoint.replace(",", ".");
                return new BigDecimal(formatJava);

            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um valor numérico.");
            }
        }
    }


}
