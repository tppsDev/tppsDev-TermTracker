package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "course",
        foreignKeys = {
                @ForeignKey(entity = Term.class,
                        parentColumns = "id",
                        childColumns = "termId",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.CASCADE),
        },
        indices = @Index(value = "termId")
)
public class Course implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id;
    private int termId;
    private String title;
    private LocalDate startDate;
    private LocalDate anticipatedEndDate;
    private CourseStatus status;

    @Ignore
    public Course() {
    }

    @Ignore
    public Course(int termId, String title, LocalDate startDate, LocalDate anticipatedEndDate, CourseStatus status) {
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.anticipatedEndDate = anticipatedEndDate;
        this.status = status;
    }

    public Course(int id, int termId, String title, LocalDate startDate, LocalDate anticipatedEndDate, CourseStatus status) {
        this.id = id;
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.anticipatedEndDate = anticipatedEndDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
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

    public LocalDate getAnticipatedEndDate() {
        return anticipatedEndDate;
    }

    public void setAnticipatedEndDate(LocalDate anticipatedEndDate) {
        this.anticipatedEndDate = anticipatedEndDate;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return title;
    }

    public enum CourseStatus {
        PLAN_TO_TAKE("Plan to take"),
        IN_PROGRESS("In progress"),
        COMPLETED("Completed"),
        DROPPED("Dropped");

        private final String text;

        CourseStatus(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static CourseStatus fromString(String text) {
            for (CourseStatus status : values()) {
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
        dest.writeInt(this.termId);
        dest.writeString(this.title);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.anticipatedEndDate);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected Course(Parcel in) {
        this.id = in.readInt();
        this.termId = in.readInt();
        this.title = in.readString();
        this.startDate = (LocalDate) in.readSerializable();
        this.anticipatedEndDate = (LocalDate) in.readSerializable();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : CourseStatus.values()[tmpStatus];
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
}
