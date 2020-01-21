package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class AssessmentAlertWithAssessment implements Parcelable {
    @Embedded
    private AssessmentAlert assessmentAlert;

    @Relation(parentColumn = "assessmentId",
        entity = Assessment.class,
    entityColumn = "id")
    private Assessment assessment;

    @Ignore
    public AssessmentAlertWithAssessment() {
    }

    public AssessmentAlertWithAssessment(AssessmentAlert assessmentAlert, Assessment assessment) {
        this.assessmentAlert = assessmentAlert;
        this.assessment = assessment;
    }

    public AssessmentAlert getAssessmentAlert() {
        return assessmentAlert;
    }

    public void setAssessmentAlert(AssessmentAlert assessmentAlert) {
        this.assessmentAlert = assessmentAlert;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.assessmentAlert, flags);
        dest.writeParcelable(this.assessment, flags);
    }

    protected AssessmentAlertWithAssessment(Parcel in) {
        this.assessmentAlert = in.readParcelable(AssessmentAlert.class.getClassLoader());
        this.assessment = in.readParcelable(Assessment.class.getClassLoader());
    }

    public static final Parcelable.Creator<AssessmentAlertWithAssessment> CREATOR = new Parcelable.Creator<AssessmentAlertWithAssessment>() {
        @Override
        public AssessmentAlertWithAssessment createFromParcel(Parcel source) {
            return new AssessmentAlertWithAssessment(source);
        }

        @Override
        public AssessmentAlertWithAssessment[] newArray(int size) {
            return new AssessmentAlertWithAssessment[size];
        }
    };
}
