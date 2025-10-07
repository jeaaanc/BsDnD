package BankSdNd.example.BsDnD.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Utility class for currency-related formatting operations.
 * This class provides methods to format numerical values into locale-specific currency strings.
 * It cannot be instantiated or extended.
 */
public final class CurrencyUtils {

    private CurrencyUtils() {
    }


    /**
     * Formats a {@code BigDecimal} value into a currency {@code String} using the Brazilian Portuguese (pt-BR) locale.
     *
     * This method handles the currency symbol (R$), thousands separators (.), and the decimal separator (,).
     * For example, a BigDecimal value of 1234.56 would be formatted as "R$ 1.234,56".
     *
     * @param value The {@code BigDecimal} amount to be formatted. Can be null.
     * @return A {@code String} representing the formatted currency, or a formatted zero value if the input is null.
     */
    public static String formatToBrazilianCurrency(BigDecimal value) {
        Locale ptBrLocale = new Locale("pt", "BR");

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(ptBrLocale);
        return currencyFormatter.format(value);
    }
}
