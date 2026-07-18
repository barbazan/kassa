package com.plagame.game.kassa.utils;

/**
 * Created by Дмитрий Малышев on 30.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class NumberFormat {

    private static final char[] BASE_SUFFIXES = {
        ' ', 'K', 'M', 'B', 'T', 'Q', 'W', 'E', 'R', 'Y', 'U'
    };

    public static String format(long value) {
        if (value < 1000) return String.valueOf(value);

        int exp = 0;
        long scaled = value;

        while (scaled >= 1000 && exp < BASE_SUFFIXES.length - 1) {
            scaled /= 1000;
            exp++;
        }

        long divisor = pow1000(exp);
        long integerPart = value / divisor;
        long remainder = value % divisor;

        return formatParts(integerPart, remainder, divisor) + BASE_SUFFIXES[exp];
    }

    private static String formatParts(long integerPart, long remainder, long divisor) {

        // >= 100 → максимум 1 знак
        if (integerPart >= 100) {
            long decimal = (remainder * 10 + divisor / 2) / divisor;

            if (decimal == 10) {
                integerPart++;
                decimal = 0;
            }

            if (decimal == 0) return String.valueOf(integerPart);

            return integerPart + "." + decimal;
        }

        // < 100 → максимум 2 знака
        long decimal = (remainder * 100 + divisor / 2) / divisor;

        if (decimal == 100) {
            integerPart++;
            decimal = 0;
        }

        if (decimal == 0) return String.valueOf(integerPart);

        // 10, 20, 30 → 1.1 / 1.2 / 1.3
        if (decimal % 10 == 0) {
            return integerPart + "." + (decimal / 10);
        }

        // 01..09 → 1.01 / 1.02
        if (decimal < 10) {
            return integerPart + ".0" + decimal;
        }

        return integerPart + "." + decimal;
    }

    private static long pow1000(int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) {
            result *= 1000;
        }
        return result;
    }

    public static void main(String[] args) {
        long delta = 10;
        long num = 0;

        while (num < 5000) {
            num += delta;
            System.out.println("num = " + num + " format = " + format(num));
        }
    }
}
