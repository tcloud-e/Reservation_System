package com.example.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class TimetableUtil {

    public record PeriodInfo(int period, LocalTime startTime, LocalTime endTime, String subject) {
        public String label() {
            return period + "コマ目（" + startTime + "〜" + endTime + " " + subject + "）";
        }
    }

    private static final Map<DayOfWeek, List<PeriodInfo>> TIMETABLE = new LinkedHashMap<>();

    static {
        TIMETABLE.put(DayOfWeek.MONDAY, List.of(
            new PeriodInfo(1, LocalTime.of(10, 0),  LocalTime.of(11, 0),  "国語"),
            new PeriodInfo(2, LocalTime.of(11, 15), LocalTime.of(12, 15), "数学"),
            new PeriodInfo(3, LocalTime.of(13, 15), LocalTime.of(14, 15), "理科"),
            new PeriodInfo(4, LocalTime.of(14, 30), LocalTime.of(15, 30), "社会")
        ));
        TIMETABLE.put(DayOfWeek.TUESDAY, List.of(
            new PeriodInfo(1, LocalTime.of(10, 0),  LocalTime.of(11, 0),  "英語"),
            new PeriodInfo(2, LocalTime.of(11, 15), LocalTime.of(12, 15), "情報"),
            new PeriodInfo(3, LocalTime.of(13, 15), LocalTime.of(14, 15), "国語"),
            new PeriodInfo(4, LocalTime.of(14, 30), LocalTime.of(15, 30), "数学")
        ));
        TIMETABLE.put(DayOfWeek.WEDNESDAY, List.of(
            new PeriodInfo(1, LocalTime.of(10, 0),  LocalTime.of(11, 0),  "理科"),
            new PeriodInfo(2, LocalTime.of(11, 15), LocalTime.of(12, 15), "社会"),
            new PeriodInfo(3, LocalTime.of(13, 15), LocalTime.of(14, 15), "英語"),
            new PeriodInfo(4, LocalTime.of(14, 30), LocalTime.of(15, 30), "情報")
        ));
        TIMETABLE.put(DayOfWeek.THURSDAY, List.of(
            new PeriodInfo(1, LocalTime.of(10, 0),  LocalTime.of(11, 0),  "国語"),
            new PeriodInfo(2, LocalTime.of(11, 15), LocalTime.of(12, 15), "数学"),
            new PeriodInfo(3, LocalTime.of(13, 15), LocalTime.of(14, 15), "理科"),
            new PeriodInfo(4, LocalTime.of(14, 30), LocalTime.of(15, 30), "社会")
        ));
        TIMETABLE.put(DayOfWeek.FRIDAY, List.of(
            new PeriodInfo(1, LocalTime.of(10, 0),  LocalTime.of(11, 0),  "英語"),
            new PeriodInfo(2, LocalTime.of(11, 15), LocalTime.of(12, 15), "情報"),
            new PeriodInfo(3, LocalTime.of(13, 15), LocalTime.of(14, 15), "国語"),
            new PeriodInfo(4, LocalTime.of(14, 30), LocalTime.of(15, 30), "数学")
        ));
        TIMETABLE.put(DayOfWeek.SATURDAY, List.of(
            new PeriodInfo(1, LocalTime.of(15, 45), LocalTime.of(16, 45), "理科"),
            new PeriodInfo(2, LocalTime.of(17, 0),  LocalTime.of(18, 0),  "社会"),
            new PeriodInfo(3, LocalTime.of(18, 15), LocalTime.of(19, 15), "英語")
        ));
        TIMETABLE.put(DayOfWeek.SUNDAY, List.of(
            new PeriodInfo(1, LocalTime.of(15, 45), LocalTime.of(16, 45), "情報"),
            new PeriodInfo(2, LocalTime.of(17, 0),  LocalTime.of(18, 0),  "国語"),
            new PeriodInfo(3, LocalTime.of(18, 15), LocalTime.of(19, 15), "算数")
        ));
    }

    /** 曜日のコマ一覧を返す */
    public static List<PeriodInfo> getPeriods(DayOfWeek day) {
        return TIMETABLE.getOrDefault(day, Collections.emptyList());
    }

    /** 曜日＋コマ番号から PeriodInfo を返す */
    public static Optional<PeriodInfo> getPeriod(DayOfWeek day, int period) {
        return getPeriods(day).stream().filter(p -> p.period() == period).findFirst();
    }

    /** 曜日の日本語名 */
    public static String dayLabel(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> "月曜日";
            case TUESDAY   -> "火曜日";
            case WEDNESDAY -> "水曜日";
            case THURSDAY  -> "木曜日";
            case FRIDAY    -> "金曜日";
            case SATURDAY  -> "土曜日";
            case SUNDAY    -> "日曜日";
        };
    }
}
