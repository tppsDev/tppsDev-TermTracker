package com.example.termtracker.util;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.time.temporal.ChronoUnit.DAYS;

public final class DateTimeUtils {
    /**
     * Date DateTimeFormatter pattern of "M/d/YYYY"
     */
    public static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("M/d/yyyy");

    /**
     * Date & time DateTimeFormatter pattern of "MMMM d, YYYY"
     */
    public static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    /**
     * Date & time DateTimeFormatter pattern of "h:mm a"
     */
    public static final DateTimeFormatter SIMPLE_TIME = DateTimeFormatter.ofPattern("h:mma");

    /**
     * Date & time DateTimeFormatter pattern of "M/d/yyyy h:mm a"
     */
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

    private DateTimeUtils() {}

    public static boolean dateIsBetween(LocalDate checkDate, LocalDate firstDate, LocalDate secondDate) throws DateTimeUtilException {
        if ( firstDate.equals(secondDate)) {
            throw new DateTimeUtilException("Invalid Data Range");
        }
        if ( firstDate.isBefore(secondDate)) {
            return !((checkDate.isBefore(firstDate)) || (checkDate.isAfter(secondDate)));
        } else {
            return !((checkDate.isAfter(firstDate)) || (checkDate.isBefore(secondDate)));
        }
    }

    public static boolean dateStringIsValid(String date) {
        LocalDate testDate;
        try {
            testDate = LocalDate.parse(date, DateTimeUtils.SHORT_DATE);
        } catch (DateTimeParseException ex) {
            Log.d(TAG, "dateStringIsValid: " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean timeStringIsValid(String time) {
        LocalTime testTime;
        try {
            testTime = LocalTime.parse(time, DateTimeUtils.SIMPLE_TIME);
        } catch (DateTimeParseException ex) {
            Log.d(TAG, "dateStringIsValid: " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static PeriodResponse periodUntil(LocalDate date) throws DateTimeUtilException {
        PeriodResponse periodResponse;
        LocalDate now = LocalDate.now();

        // Check to be sure date passed is not before current date
        if (!date.isAfter(now)) {
            throw new DateTimeUtilException(
                    "DateTimeUtils.periodUntil requires future date. Please check date or use DateTimeUtils.periodSince.");
        }

        long days = DAYS.between(now, date);
        // Determine unit based on number of days

        if (days > 14) {
            long weeks = ((days % 7) > 0) ? ((days / 7) + 1) : (days / 7);
            periodResponse = new PeriodResponse(PeriodUnit.WEEK, weeks);
        } else {
            periodResponse = new PeriodResponse(PeriodUnit.DAY, days);
        }

        return periodResponse;
    }

    public static PeriodResponse periodSince(LocalDate date) throws DateTimeUtilException {
        PeriodResponse periodResponse;
        LocalDate now = LocalDate.now();

        // Check to be sure date passed is not after current date
        if (!date.isBefore(now)) {
            throw new DateTimeUtilException(
                    "DateTimeUtils.periodSince requires past date. Please check date or use DateTimeUtils.periodUntil.");
        }

        Period period = Period.between(date, now);
        // Determine unit based on number of days
        if (period.getDays() > 62) {
            periodResponse = new PeriodResponse(PeriodUnit.MONTH, period.getMonths());
        } else if (period.getDays() > 14) {
            int weeks = ((period.getDays() % 7) > 0) ? ((period.getDays() / 7) + 1) : (period.getDays() / 7);
            periodResponse = new PeriodResponse(PeriodUnit.WEEK, weeks);
        } else {
            periodResponse = new PeriodResponse(PeriodUnit.DAY, period.getDays());
        }

        return periodResponse;
    }

    public enum PeriodUnit {
        DAY,
        WEEK,
        MONTH
    }
}
