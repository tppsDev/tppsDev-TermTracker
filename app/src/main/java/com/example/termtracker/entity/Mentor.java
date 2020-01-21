package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "mentor")
public class Mentor implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    @Ignore
    public Mentor() {
    }

    public Mentor(int id, String firstName, String lastName, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return lastName + ", " + firstName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.email);
    }

    protected Mentor(Parcel in) {
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phoneNumber = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<Mentor> CREATOR = new Parcelable.Creator<Mentor>() {
        @Override
        public Mentor createFromParcel(Parcel source) {
            return new Mentor(source);
        }

        @Override
        public Mentor[] newArray(int size) {
            return new Mentor[size];
        }
    };
}
