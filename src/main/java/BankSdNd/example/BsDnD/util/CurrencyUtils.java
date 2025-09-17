package BankSdNd.example.BsDnD.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    /**
     * Formata um valor BigDecimal para uma String de moeda no padrão brasileiro (R$).
     * Exemplo: um BigDecimal de 7000 vira a String "R$ 7.000,00".
     *
     * @param value O valor a ser formatado.
     * @return Uma String formatada como moeda brasileira.
     */

    public static String formatToBrazilianCurrency(BigDecimal value) {
        Locale ptBrLocale = new Locale("pt", "BR");

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(ptBrLocale);
        return currencyFormatter.format(value);
    }
}
