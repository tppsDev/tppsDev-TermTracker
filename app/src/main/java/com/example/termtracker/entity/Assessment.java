package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "assessment",
        foreignKeys = @ForeignKey(entity = Course.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        indices = @Index(value = "courseId"))
public class Assessment implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id;
    private AssessmentType type;
    private int courseId;
    private String title;
    private String description;
    private LocalDate goalDate;
    private AssessmentStatus status;

    @Ignore
    public Assessment() {
    }

    public Assessment(int id, AssessmentType type, int courseId, String title, String description, LocalDate goalDate, AssessmentStatus status) {
        this.id = id;
        this.type = type;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.goalDate = goalDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = type;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(LocalDate goalDate) {
        this.goalDate = goalDate;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public enum AssessmentType {
        OBJECTIVE("Objective"),
        PERFORMANCE("Performance");

        private final String text;

        AssessmentType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static AssessmentType fromString(String text) {
            for (AssessmentType type : values()) {
                if (type.text.equals(text)) {
                    return type;
                }
            }
            return  null;
        }
    }

    public enum AssessmentStatus {
        NOT_SCHEDULED("Not Scheduled"),
        SCHEDULED("Scheduled"),
        SUBMITTED("Submitted"),
        PASSED("Passed"),
        FAILED("Failed");

        private final String text;

        AssessmentStatus(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static AssessmentStatus fromString(String text) {
            for (AssessmentStatus status : values()) {
                if (status.text.equals(text)) {
                    return status;
                }
            }
            return  null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.courseId);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeSerializable(this.goalDate);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected Assessment(Parcel in) {
        this.id = in.readInt();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : AssessmentType.values()[tmpType];
        this.courseId = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.goalDate = (LocalDate) in.readSerializable();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : AssessmentStatus.values()[tmpStatus];
    }

    public static final Parcelable.Creator<Assessment> CREATOR = new Parcelable.Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel source) {
            return new Assessment(source);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };
}
