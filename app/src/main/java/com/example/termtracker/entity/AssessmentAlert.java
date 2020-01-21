package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "assessmentAlert",
        foreignKeys = @ForeignKey(entity = Assessment.class,
                parentColumns = "id",
                childColumns = "assessmentId",
                onDelete = ForeignKey.RESTRICT),
        indices = @Index(value = "assessmentId")
)
public class AssessmentAlert implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id;
    private int assessmentId;
    private LocalDateTime alertTime;
    private String message;

    @Ignore
    public AssessmentAlert() {
    }

    @Ignore
    public AssessmentAlert(LocalDateTime alertTime, String message) {
        this.alertTime = alertTime;
        this.message = message;
    }

    public AssessmentAlert(int id, LocalDateTime alertTime, String message) {
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

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
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
        return "Assessment Alert:\n\t" + message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.assessmentId);
        dest.writeSerializable(this.alertTime);
        dest.writeString(this.message);
    }

    protected AssessmentAlert(Parcel in) {
        this.id = in.readInt();
        this.assessmentId = in.readInt();
        this.alertTime = (LocalDateTime) in.readSerializable();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<AssessmentAlert> CREATOR = new Parcelable.Creator<AssessmentAlert>() {
        @Override
        public AssessmentAlert createFromParcel(Parcel source) {
            return new AssessmentAlert(source);
        }

        @Override
        public AssessmentAlert[] newArray(int size) {
            return new AssessmentAlert[size];
        }
    };
}
