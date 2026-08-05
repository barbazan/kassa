package com.plagame.game.kassa.enums;

/**
 * Created by Дмитрий Малышев on 05.08.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum AchievementInfo {
    ACHIEVEMENT_INFO_1(1, "Первый полёт", "Провести 10 операций на кассе."),
    ACHIEVEMENT_INFO_2(2, "Платёжный хамелеон", "Принять платежи всеми \nдоступными в игре способами."),
    ACHIEVEMENT_INFO_3(3, "Рождение легенды", "Достичь положительного баланса \nкассы по итогам дня."),
    ACHIEVEMENT_INFO_4(4, "Сердце ритма", "Обслужить 20 покупателей."),
    ACHIEVEMENT_INFO_5(5, "Мастер потока", "Обслужить всего 55 покупателей."),
    ACHIEVEMENT_INFO_6(6, "Безупречная серия", "Провести 15 операций подряд \nбез единой ошибки."),
    ACHIEVEMENT_INFO_7(7, "Охотник за легендами", "Продать редкий товар"), // (всё золото, дубайский шоколад, карандаш, механическая точилка, нить для зубов, лобстер и морской ёж, зелёная бутылка вина, миндальное и кокосовое молоко
    ACHIEVEMENT_INFO_8(8, "Титан транзакций", "Принять оплату на сумму \nсвыше 50 долларов."),
    ACHIEVEMENT_INFO_9(9, "Гигантский платёж", "Принять оплату на сумму \nсвыше 100 долларов."),
    ACHIEVEMENT_INFO_10(10, "Космический платёж", "Принять оплату на сумму \nсвыше 500 долларов."),
    ACHIEVEMENT_INFO_11(11, "Ночной дозор", "Продавать товары в ночное время \n(с 21:00 реального времени)."),
//    ACHIEVEMENT_INFO_11(11, "Ночной дозор", "Продавать товары в ночное время \n(с 21:00 реального времени). \nПока остальные спят, \nты стоишь на страже экономики."),
//    ACHIEVEMENT_INFO_12(12, "Кассир года", "Удерживать топ‑1 в рейтинге на протяжении 5 дней. Ты не просто работаешь — ты защищаешь честь кассовой стойки"),
    ;


    public int type;
    public String name;
    public String description;

    AchievementInfo(int type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        ENUM_MAPS.ACHIEVEMENT_INFO_MAP.put(type, this);
    }

    public static AchievementInfo getByType(int type) {
        return ENUM_MAPS.ACHIEVEMENT_INFO_MAP.get(type);
    }

}
