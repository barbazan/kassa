package com.plagame.game.kassa.utils;

import java.util.Date;

/**
 * Dmitry Malyshev
 * Date: 20.11.13 Time: 18:22
 * Email: dmitry.malyshev@gmail.com
 */
public class Time implements Comparable {

    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    private long hours;
    private long minutes;
    private long seconds;

    public Time(long millis) {
        long seconds = millis / 1000;
        setHours(seconds / 3600);
        seconds %= 3600;
        setMinutes(seconds / 60);
        setSeconds(seconds % 60);
    }

    public void setHours(long hours) {
        if (hours < 0) {
            hours = 0;
        }
        this.hours = hours;
    }

    public void setMinutes(long minutes) {
        if (minutes < 0 || minutes > 59) {
            minutes = 0;
        }
        this.minutes = minutes;
    }

    public void setSeconds(long seconds) {
        if (seconds < 0 || seconds > 59) {
            seconds = 0;
        }
        this.seconds = seconds;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Time)) return false;
        Time time = (Time) o;
        return hours == time.hours && minutes == time.minutes && seconds == time.seconds;
    }

    public int hashCode() {
        long result;
        result = hours;
        result = 29 * result + minutes;
        result = 29 * result + seconds;
        return Long.valueOf(result).hashCode();
    }

    public String toString() {
        return expand(hours) + ":" + expand(minutes) + ":" + expand(seconds);
    }

    public long toMillis() {
        return toSeconds() * 1000;
    }

    public long toSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    public int compareTo(Object o) {
        Time time = (Time) o;
        return Long.signum(toSeconds() - time.toSeconds());
    }

    private static String expand(long i) {
        String s = String.valueOf(i);
        while (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }

    private String toStringDAYS(double millis) {
        int days = (int) Math.floor(millis / DAY_MILLIS);
        int hours = (int) Math.round((millis - days * DAY_MILLIS) / HOUR_MILLIS);
        if (hours == 24) {
            days += 1;
            hours = 0;
        }
        return days + " " + getDaysName() + (hours == 0 ? "" : " " + hours + " " + getHoursName());
    }

    private String toStringHOURS(double millis) {
        int hours = (int) Math.floor(millis / HOUR_MILLIS);
        int minutes = (int) Math.round((millis - hours * HOUR_MILLIS) / MINUTE_MILLIS);
        if (minutes == 60) {
            hours += 1;
            minutes = 0;
        }
        if (hours == 24) {
            return toStringDAYS(millis);
        }
        return hours + " " + getHoursName() + (minutes == 0 ? "" : " " + minutes + " " + getMinutesName());
    }

    private String toStringMINUTES(double millis) {
        int minutes = (int) Math.floor(millis / MINUTE_MILLIS);
        int seconds = (int) Math.round((millis - minutes * MINUTE_MILLIS) / SECOND_MILLIS);
        if (seconds == 60) {
            minutes += 1;
            seconds = 0;
        }
        if (minutes == 60) {
            return toStringHOURS(millis);
        }
        return minutes + " " + getMinutesName() + (seconds == 0 ? "" : " " + seconds + " " + getSecondsName());
    }

    private String toStringSECONDS(double millis) {
        int seconds = (int) Math.round(millis / 1000);
        if (seconds == 60) {
            return toStringMINUTES(millis);
        }
        return seconds + " " + getSecondsName();
    }

    private String toStringDays(double millis) {
        int days = (int) Math.floor(millis / DAY_MILLIS);
        return days + " " + getDaysName();
    }

    private String toStringHours(double millis) {
        int hours = (int) Math.floor(millis / HOUR_MILLIS);
        return hours + " " + getHoursName();
    }

    private String toStringMinutes(double millis) {
        int minutes = (int) Math.floor(millis / MINUTE_MILLIS);
        return minutes + " " + getMinutesName();
    }

    public String toStringInHumanFormat() {
        double millis = Math.max(1000, toMillis());
        if(millis >= DAY_MILLIS) {
            return toStringDays(millis);
        } else if(millis >= HOUR_MILLIS) {
            return toStringHours(millis);
        } else if(millis >= MINUTE_MILLIS) {
            return toStringMinutes(millis);
        } else {
            return toStringSECONDS(millis);
        }
    }

    public String toStringInHumanFormat2() {
        double millis = Math.max(1000, toMillis());
        if(millis >= DAY_MILLIS) {
            return toStringDAYS(millis);
        } else if(millis >= HOUR_MILLIS) {
            return toStringHOURS(millis);
        } else if(millis >= MINUTE_MILLIS) {
            return toStringMINUTES(millis);
        } else {
            return toStringSECONDS(millis);
        }
    }

    private String getDaysName() {
        return "d";
    }

    private String getHoursName() {
        return "h";
    }

    private String getMinutesName() {
        return "m";
    }

    private String getSecondsName() {
        return "s";
    }

    public static boolean isDayChetniy(long time) {
        return new Date(time).getDay() % 2 == 0;
    }

    public static boolean isToday(long time) {
        Date date = new Date(time);
        Date now = new Date();

        return isSameDay(date, now);
    }

    public static boolean isYesterday(long time) {
        Date date = new Date(time);
        Date now = new Date();

        // дата = сегодня?
        if (isSameDay(date, now)) return false;

        // вычитаем 1 день из "сейчас"
        Date yesterday = new Date(now.getTime() - 24L * 60 * 60 * 1000);

        return isSameDay(date, yesterday);
    }

    private static boolean isSameDay(Date d1, Date d2) {
        return d1.getYear() == d2.getYear()
                && d1.getMonth() == d2.getMonth()
                && d1.getDate() == d2.getDate();
    }

}
