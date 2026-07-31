package com.plagame.game.kassa.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CashPaymentGenerator {

    private static final int[] BILLS = {5, 10, 20, 50, 100};
    private static final Random RANDOM = new Random();

    public static int generatePaidAmount(double price) {

        int need = (int) price + 1;

        // Если хватает одной купюры — оставляем вашу "естественность"
        List<Integer> singleBills = new ArrayList<>();

        for (int bill : BILLS) {
            if (bill >= need) {
                singleBills.add(bill);
            }
        }

        if (!singleBills.isEmpty()) {
            return chooseNatural(singleBills);
        }

        return findBestSum(need);
    }

    /**
     * Ищет минимальную сумму >= need,
     * используя любое количество купюр.
     */
    private static int findBestSum(int need) {

        int maxBill = 100;

        // небольшой запас
        int limit = need + maxBill;

        boolean[] reachable = new boolean[limit + 1];
        int[] billsUsed = new int[limit + 1];

        reachable[0] = true;

        for (int sum = 0; sum <= limit; sum++) {

            if (!reachable[sum])
                continue;

            for (int bill : BILLS) {

                int next = sum + bill;

                if (next > limit)
                    continue;

                if (!reachable[next] || billsUsed[next] > billsUsed[sum] + 1) {
                    reachable[next] = true;
                    billsUsed[next] = billsUsed[sum] + 1;
                }
            }
        }

        for (int sum = need; sum <= limit; sum++) {
            if (reachable[sum]) {
                return sum;
            }
        }

        // Теоретически сюда не попадем
        return need;
    }

    private static int chooseNatural(List<Integer> variants) {

        Collections.sort(variants);

        double r = RANDOM.nextDouble();

        if (r < 0.8)
            return variants.get(0);

        if (r < 0.95 && variants.size() > 1)
            return variants.get(1);

        return variants.get(RANDOM.nextInt(variants.size()));
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
