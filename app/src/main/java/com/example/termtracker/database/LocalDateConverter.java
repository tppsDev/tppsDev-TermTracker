package com.example.termtracker.database;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateConverter {
    @TypeConverter
    public static Long toTimestamp(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZonedDateTime zDateTime  = ZonedDateTime.of(localDate.atStartOfDay(), ZoneId.systemDefault());
        return zDateTime.toInstant().toEpochMilli();
    }

    @TypeConverter
    public static LocalDate toLocalDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
