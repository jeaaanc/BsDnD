package BankSdNd.example.BsDnD.adapter.in.cli.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import org.springframework.context.i18n.LocaleContextHolder;


/**
 * Utility class for currency-related formatting operations within the CLI adapter.
 */
public final class CurrencyUtils {

    private CurrencyUtils() {
    }


    /**
     * Formats a {@code BigDecimal} value into a currency {@code String} using the current locale.
     *
     * @param value The {@code BigDecimal} amount to be formatted. Can be null.
     * @return A {@code String} representing the formatted currency.
     */
    public static String formatCurrency(BigDecimal value) {
        if (value == null) value = BigDecimal.ZERO;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(LocaleContextHolder.getLocale());
        return currencyFormatter.format(value);
    }
}
