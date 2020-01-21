package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "courseAlert",
        foreignKeys = @ForeignKey(entity = Course.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index(value = "courseId")
)
public class CourseAlert implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id;
    private int courseId;
    private LocalDateTime alertTime;
    private String message;

    @Ignore
    public CourseAlert() {
    }

    @Ignore
    public CourseAlert(LocalDateTime alertTime, String message) {
        this.alertTime = alertTime;
        this.message = message;
    }

    public CourseAlert(int id, LocalDateTime alertTime, String message) {
        this.id = id;
        this.alertTime = alertTime;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(LocalDateTime alertTime) {
        this.alertTime = alertTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertMessage() {
        return "Course Alert:\n\t" + message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.courseId);
        dest.writeSerializable(this.alertTime);
        dest.writeString(this.message);
    }

    protected CourseAlert(Parcel in) {
        this.id = in.readInt();
        this.courseId = in.readInt();
        this.alertTime = (LocalDateTime) in.readSerializable();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<CourseAlert> CREATOR = new Parcelable.Creator<CourseAlert>() {
        @Override
        public CourseAlert createFromParcel(Parcel source) {
            return new CourseAlert(source);
        }

        @Override
        public CourseAlert[] newArray(int size) {
            return new CourseAlert[size];
        }
    };
}
