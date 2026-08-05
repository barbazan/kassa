package com.plagame.game.kassa.enums;

import com.plagame.game.kassa.beans.User;

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
    ACHIEVEMENT_INFO_8(8, "Титан транзакций", "Продать товар на сумму \nсвыше 50 долларов."),
    ACHIEVEMENT_INFO_9(9, "Гигантский платёж", "Продать товар на сумму \nсвыше 100 долларов."),
    ACHIEVEMENT_INFO_10(10, "Космический платёж", "Продать товар на сумму \nсвыше 500 долларов."),
    ACHIEVEMENT_INFO_11(11, "Ночной дозор", "Продавать товары в ночное время \n(с 21:00 реального времени)."),
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

    public boolean isComplete() {
        Integer count = User.get().achievments.get(type);
        if(count != null) {
            switch (this) {
                case ACHIEVEMENT_INFO_1: return count >= 10;
                case ACHIEVEMENT_INFO_2: return count >= 2;
                case ACHIEVEMENT_INFO_3: return count > 0;
                case ACHIEVEMENT_INFO_4: return count >= 20;
                case ACHIEVEMENT_INFO_5: return count >= 55;
                case ACHIEVEMENT_INFO_6: return count >= 15;
                case ACHIEVEMENT_INFO_7: return count > 0;
                case ACHIEVEMENT_INFO_8: return count >= 50;
                case ACHIEVEMENT_INFO_9: return count >= 100;
                case ACHIEVEMENT_INFO_10: return count >= 500;
                case ACHIEVEMENT_INFO_11: return count > 0;
            }
        }
        return false;
    }
}
