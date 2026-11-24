package com.workshop.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtil {
    private static final DecimalFormat RUPIAH_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        RUPIAH_FORMAT = new DecimalFormat("Rp #,##0", symbols);
    }

    public static String format(BigDecimal amount) {
        if (amount == null) return "Rp 0";
        return RUPIAH_FORMAT.format(amount);
    }

    public static String format(double amount) {
        return RUPIAH_FORMAT.format(amount);
    }

    public static BigDecimal parse(String str) {
        if (str == null || str.trim().isEmpty()) return BigDecimal.ZERO;
        String clean = str.replaceAll("[^0-9]", "");
        if (clean.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(clean);
    }
}
