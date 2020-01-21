package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.Assessment.AssessmentType;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class AssessmentWithCourseNameListAdapter extends RecyclerView.Adapter<AssessmentWithCourseNameListAdapter.AssessmentViewHolder> {
    private final LayoutInflater assessmentInflater;
    private List<AssessmentWithCourse> assessmentList;
    private final OnItemClickListener listener;

    public AssessmentWithCourseNameListAdapter(Context context, OnItemClickListener listener) {
        assessmentInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = assessmentInflater
                .inflate(R.layout.assessment_with_course_name_recyclerview_item, viewGroup, false);

        return new AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder assessmentViewHolder, int position) {
        if (assessmentList != null) {
            AssessmentWithCourse assessment = assessmentList.get(position);
            if (assessment.getAssessment().getType().equals(AssessmentType.PERFORMANCE)) {
                assessmentViewHolder.assessmentWithCourseRVAssessmentTypeImageView
                        .setImageResource(R.drawable.ic_performance_assessment);
            } else {
                assessmentViewHolder.assessmentWithCourseRVAssessmentTypeImageView
                        .setImageResource(R.drawable.ic_objective_assessment);
            }
            assessmentViewHolder.assessmentWithCourseRVAssessmentTextView
                    .setText(assessment.getAssessment().getTitle());
            assessmentViewHolder.assessmentWithCourseRVCourseTextView
                    .setText(assessment.getCourse().getTitle());
            assessmentViewHolder.assessmentWithCourseRVAssessmentGoalDateTextView
                    .setText(assessment.getAssessment().getGoalDate().format(DateTimeUtils.SHORT_DATE));
            assessmentViewHolder.assessmentWithCourseRVCourseDueDateTextView
                    .setText(assessment.getCourse().getAnticipatedEndDate().format(DateTimeUtils.SHORT_DATE));
            assessmentViewHolder.bind(assessment.getAssessment(), listener);
        } else {
            assessmentViewHolder.assessmentWithCourseRVAssessmentTextView
                    .setText(R.string.no_assessment);
        }
    }

    public AssessmentWithCourse getAssessmentAt(int position) {
        return assessmentList.get(position);
    }

    public void setAssessments(List<AssessmentWithCourse> assessments) {
        assessmentList = assessments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (assessmentList != null) {
            return assessmentList.size();
        } else {
            return 0;
        }
    }

    public class AssessmentViewHolder extends RecyclerView.ViewHolder {
        ImageView assessmentWithCourseRVAssessmentTypeImageView;
        TextView assessmentWithCourseRVAssessmentTextView;
        TextView assessmentWithCourseRVCourseTextView;
        TextView assessmentWithCourseRVAssessmentGoalDateTextView;
        TextView assessmentWithCourseRVCourseDueDateTextView;


        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            assessmentWithCourseRVAssessmentTypeImageView = itemView.findViewById(R.id.courseDetailAssessmentRVAssessmentTypeImageView);
            assessmentWithCourseRVAssessmentTextView = itemView.findViewById(R.id.courseDetailAssessmentRVAssessmentTextView);
            assessmentWithCourseRVCourseTextView = itemView.findViewById(R.id.courseDetailAssessmentRVStatusLabel);
            assessmentWithCourseRVAssessmentGoalDateTextView = itemView.findViewById(R.id.courseDetailAssessmentRVAssessmentGoalDateTextView);
            assessmentWithCourseRVCourseDueDateTextView = itemView.findViewById(R.id.courseDetailAssessmentRVCourseDueDateTextView);
        }

        public void bind(final Assessment assessment, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(assessment);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Assessment item);
    }
}
