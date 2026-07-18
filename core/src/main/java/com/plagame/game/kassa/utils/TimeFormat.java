package com.plagame.game.kassa.utils;

/**
 * Created by Дмитрий Малышев on 12.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class TimeFormat {

    public static String formatAbsenceDuration(long totalMinutes) {
        if (totalMinutes < 60) {
            return totalMinutes + " " + getMinuteWord(totalMinutes).toUpperCase();
        }

        long hours = totalMinutes / 60;
        return hours + " " + getHourWord(hours).toUpperCase();
    }

    private static String getHourWord(long hours) {
        if (hours % 100 >= 11 && hours % 100 <= 14) {
            return "часов";
        }

        switch ((int) (hours % 10)) {
            case 1:
                return "час";
            case 2:
            case 3:
            case 4:
                return "часа";
            default:
                return "часов";
        }
    }

    private static String getMinuteWord(long minutes) {
        if (minutes % 100 >= 11 && minutes % 100 <= 14) {
            return "минут";
        }

        switch ((int) (minutes % 10)) {
            case 1:
                return "минута";
            case 2:
            case 3:
            case 4:
                return "минуты";
            default:
                return "минут";
        }
    }

}
