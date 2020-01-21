package com.example.termtracker.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "courseMentor",
        primaryKeys = {"courseId", "mentorId"},
        foreignKeys = {
            @ForeignKey(entity = Course.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE),
            @ForeignKey(entity = Mentor.class,
                parentColumns = "id",
                childColumns = "mentorId",
                onDelete = ForeignKey.RESTRICT,
                    onUpdate = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "courseId"),
                @Index(value = "mentorId")
        }
)
public class CourseMentorXRef {
    private int courseId;
    private int mentorId;

    @Ignore
    public CourseMentorXRef() {
    }

    public CourseMentorXRef(int courseId, int mentorId) {
        this.courseId = courseId;
        this.mentorId = mentorId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }
}
