package com.plagame.game.kassa.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class CashPaymentGenerator {

    private static final int[] BILLS_5 = {500, 5000, 10000};
    private static final int[] BILLS_10 = {1000, 5000, 10000};
    private static final int[] BILLS_20 = {2000, 5000, 10000};
    private static final int[] BILLS_50_100 = {5000, 10000};
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
    public static int generatePaidAmount(int priceInCents) {
        List<Integer> bills = generateBills(priceInCents);

        int total = 0;
        for (int bill : bills) {
            total += bill;
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
    public static List<Integer> generateBills(int priceInCents) {

        validatePrice(priceInCents);

        int need = priceInCents + 1;

        int[] customerBills = chooseCustomerBills();

        List<Integer> result = new ArrayList<>();

        int total = 0;
        int smallestBill = Integer.MAX_VALUE;

        while (total < need) {

            List<Integer> allowedBills = new ArrayList<>();

            for (int bill : customerBills) {

                int newTotal = total + bill;
                int newSmallestBill = Math.min(smallestBill, bill);

                if (newTotal < 0) {
                    continue;
                }

                if (newTotal < need
                    || newTotal - newSmallestBill < need) {

                    allowedBills.add(bill);
                }
            }

            if (allowedBills.isEmpty()) {
                throw new IllegalStateException(
                    "Не удалось подобрать оплату. Цена: " + priceInCents);
            }

            int selectedBill = chooseNaturalBill(allowedBills);

            result.add(selectedBill);
            total += selectedBill;
            smallestBill = Math.min(smallestBill, selectedBill);
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

    private static void validatePrice(int priceInCents) {

        if (priceInCents < 0) {
            throw new IllegalArgumentException(
                "Price must be non-negative: " + priceInCents);
        }

        if (priceInCents == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                "Price is too large: " + priceInCents);
        }
    }

    public static void main(String[] args) {

        Random random = new Random();

        for (int i = 0; i < 1000; i++) {

            int price = random.nextInt(100_000) + 100;

            List<Integer> bills = generateBills(price);

            int paidAmount = bills.stream().mapToInt(Integer::intValue).sum();

            System.out.printf(
                "%.2f -> %.2f %s%n",
                price / 100.0,
                paidAmount / 100.0,
                bills.stream()
                    .map(v -> String.format("%.0f", v / 100.0))
                    .collect(Collectors.toList())
            );
        }
    }
}

