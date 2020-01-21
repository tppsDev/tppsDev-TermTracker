package com.example.termtracker.util;

public class PeriodResponse {
    private DateTimeUtils.PeriodUnit periodUnit;
    private long periodInt;

    public PeriodResponse(DateTimeUtils.PeriodUnit periodUnit, long periodInt) {
        this.periodUnit = periodUnit;
        this.periodInt = periodInt;
    }

    public DateTimeUtils.PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(DateTimeUtils.PeriodUnit periodUnit) {
        this.periodUnit = periodUnit;
    }

    public long getPeriodInt() {
        return periodInt;
    }

    public void setPeriodInt(long periodInt) {
        this.periodInt = periodInt;
    }
}
