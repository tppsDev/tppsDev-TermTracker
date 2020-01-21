package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.termtracker.util.DateTimeUtils;

import java.time.LocalDate;
import java.time.Period;

@Entity(tableName = "term")
public class Term implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    @Ignore
    public Term() {
    }

    @Ignore
    public Term(String title, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Term(int id, String title, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Period getTermPeriod() {
        if (startDate == null || endDate == null) {
            // throw exception
        }
        return Period.between(endDate, startDate);
    }

    @Override
    public String toString() {
        return title
                + " " + startDate.format(DateTimeUtils.SHORT_DATE)
                + " - " + endDate.format(DateTimeUtils.SHORT_DATE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
    }

    protected Term(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.startDate = (LocalDate) in.readSerializable();
        this.endDate = (LocalDate) in.readSerializable();
    }

    public static final Parcelable.Creator<Term> CREATOR = new Parcelable.Creator<Term>() {
        @Override
        public Term createFromParcel(Parcel source) {
            return new Term(source);
        }

        @Override
        public Term[] newArray(int size) {
            return new Term[size];
        }
    };
}
