package com.plagame.game.kassa.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class CashPaymentGenerator {

    private static final int[] BILLS_5 = {5, 50, 100};
    private static final int[] BILLS_10 = {10, 50, 100};
    private static final int[] BILLS_20 = {20, 50, 100};
    private static final int[] BILLS_50_100 = {50, 100};
    private static final Random RANDOM = new Random();

    private CashPaymentGenerator() {
    }

    private static int[] chooseCustomerBills() {
        int chance = RANDOM.nextInt(100);

        // 10% покупателей
        if (chance < 10) {
            return BILLS_5;
        }

        // Следующие 20%
        if (chance < 30) {
            return BILLS_10;
        }

        // Следующие 40%
        if (chance < 70) {
            return BILLS_20;
        }

        // Оставшиеся 30%
        return BILLS_50_100;
    }

    /**
     * Возвращает общую сумму, которую дал покупатель.
     */
    public static int generatePaidAmount(double price) {
        List<Integer> bills = generateBills(price);

        int total = 0;

        for (int bill : bills) {
            total = Math.addExact(total, bill);
        }

        return total;
    }

    /**
     * Возвращает конкретный набор купюр.
     *
     * Условия:
     * 1. Общая сумма строго больше price.
     * 2. Если убрать любую купюру, денег станет недостаточно.
     */
    public static List<Integer> generateBills(double price) {

        validatePrice(price);

        long need = (long) Math.floor(price) + 1L;

        // Набор номиналов, доступных конкретному покупателю.
        int[] customerBills = chooseCustomerBills();

        List<Integer> result = new ArrayList<>();

        long total = 0;
        int smallestBill = Integer.MAX_VALUE;

        while (total < need) {

            List<Integer> allowedBills = new ArrayList<>();

            // Используем только купюры этого покупателя.
            for (int bill : customerBills) {

                long newTotal = total + bill;
                int newSmallestBill =
                    Math.min(smallestBill, bill);

                if (newTotal > Integer.MAX_VALUE) {
                    continue;
                }

                /*
                 * Если сумма уже достаточная, проверяем,
                 * что ни одну купюру нельзя будет убрать.
                 */
                if (newTotal < need
                    || newTotal - newSmallestBill < need) {

                    allowedBills.add(bill);
                }
            }

            if (allowedBills.isEmpty()) {
                throw new IllegalStateException(
                    "Не удалось подобрать оплату."
                        + " Цена: " + price
                );
            }

            int selectedBill =
                chooseNaturalBill(allowedBills);

            result.add(selectedBill);
            total += selectedBill;
            smallestBill =
                Math.min(smallestBill, selectedBill);
        }

        Collections.shuffle(result, RANDOM);

        return result;
    }

    /**
     * Выбирает случайную купюру с небольшим уклоном
     * в сторону крупных номиналов.
     *
     * Вес купюры равен её номиналу:
     * 100 будет встречаться чаще 50,
     * а 50 — чаще 10.
     */
    private static int chooseNaturalBill(List<Integer> variants) {

        int totalWeight = 0;

        for (int bill : variants) {
            totalWeight += bill;
        }

        int randomValue = RANDOM.nextInt(totalWeight);

        for (int bill : variants) {
            randomValue -= bill;

            if (randomValue < 0) {
                return bill;
            }
        }

        // Теоретически недостижимо.
        return variants.get(variants.size() - 1);
    }

    private static void validatePrice(double price) {

        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                "Price must be a finite non-negative number: " + price
            );
        }

        /*
         * Максимальная сумма типа int, которую можно собрать
         * из купюр, кратных пяти.
         */
        long maxPayableAmount =
            Integer.MAX_VALUE - Integer.MAX_VALUE % 5L;

        if (price >= maxPayableAmount) {
            throw new IllegalArgumentException(
                "Price is too large: " + price
            );
        }
    }

    public static void main(String[] args) {

        Random random = new Random();

        for (int i = 0; i < 1000; i++) {

            // Случайная цена от 1.00 до 1000.00
            double price = (random.nextInt(100_000) + 100) / 100.0;

            List<Integer> bills = generateBills(price);

            int paidAmount = 0;

            for (int bill : bills) {
                paidAmount += bill;
            }

            System.out.printf(
                "%.2f -> %d %s%n",
                price,
                paidAmount,
                bills
            );
        }
    }
}


//package com.plagame.game.kassa.utils;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//
///**
// * Created by Дмитрий Малышев on 26.07.2026.
// * Email: dmitry.malyshev@gmail.com
// */
//public class CashPaymentGenerator {
//
//    private static final int[] BILLS = {5, 10, 20, 50, 100};
//    private static final Random RANDOM = new Random();
//
//    public static int generatePaidAmount(double price) {
//
//        int need = (int) Math.ceil(price);
//
//        List<Integer> variants = new ArrayList<>();
//
//        // сначала одна купюра
//        for (int a : BILLS) {
//            if (a >= need) {
//                variants.add(a);
//            }
//        }
//
//        if (!variants.isEmpty()) {
//            return chooseNatural(variants);
//        }
//
//
//        // две купюры
//        variants.clear();
//
//        for (int a : BILLS) {
//            for (int b : BILLS) {
//
//                int sum = a + b;
//
//                if (sum >= need) {
//                    variants.add(sum);
//                }
//            }
//        }
//
//
//        if (!variants.isEmpty()) {
//            return Collections.min(variants);
//        }
//
//
//        // три купюры
//        variants.clear();
//
//        for (int a : BILLS) {
//            for (int b : BILLS) {
//                for (int c : BILLS) {
//
//                    int sum = a + b + c;
//
//                    if (sum >= need) {
//                        variants.add(sum);
//                    }
//                }
//            }
//        }
//
//        return Collections.min(variants);
//    }
//
//    private static int chooseNatural(List<Integer> variants) {
//
//        // сортируем от маленькой к большой
//        Collections.sort(variants);
//
//        double r = RANDOM.nextDouble();
//
//        if (r < 0.8) {
//            return variants.get(0); // самая маленькая
//        }
//
//        if (r < 0.95 && variants.size() > 1) {
//            return variants.get(1); // следующая
//        }
//
//        return variants.get(RANDOM.nextInt(variants.size()));
//    }
//
//
//
//    public static void main(String[] args) {
//        double[] prices = {
//            3.90,
//            5.90,
//            8.90,
//            18.90,
//            15.90,
//            25.90,
//            39.50,
//            107.00,
//            55.00,
//            65.00,
//            123.00
//        };
//
//        for (int i = 0; i < 5; i++) {
//            System.out.println("---- попытка " + (i + 1));
//
//            for (double price : prices) {
//                System.out.printf("%.2f -> %d%n",
//                    price,
//                    generatePaidAmount(price));
//            }
//        }
//    }
//}
