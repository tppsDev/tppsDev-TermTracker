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
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class CourseDetailAssessmentListAdapter extends RecyclerView.Adapter<CourseDetailAssessmentListAdapter.CourseDetailAssessmentViewHolder> {
    private final LayoutInflater inflater;
    private CourseWithAssessments course;
    private final OnItemClickListener listener;

    public CourseDetailAssessmentListAdapter(Context context, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseDetailAssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                               int viewType) {

        View view = inflater
                .inflate(R.layout.course_detail_assessment_recyclerview_item,
                        viewGroup, false);

        return new CourseDetailAssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseDetailAssessmentViewHolder viewHolder, int position) {
        if (course.getAssessments() != null) {
            Assessment assessment = course.getAssessments().get(position);
            if (assessment.getType().equals(Assessment.AssessmentType.PERFORMANCE)) {
                viewHolder.courseDetailAssessmentRVAssessmentTypeImageView
                        .setImageResource(R.drawable.ic_performance_assessment);
            } else {
                viewHolder.courseDetailAssessmentRVAssessmentTypeImageView
                        .setImageResource(R.drawable.ic_objective_assessment);
            }
            viewHolder.courseDetailAssessmentRVAssessmentTextView
                    .setText(assessment.getTitle());
            viewHolder.courseDetailAssessmentRVCourseDueDateTextView
                    .setText(course.getCourse().getAnticipatedEndDate()
                            .format(DateTimeUtils.SHORT_DATE));
            viewHolder.courseDetailAssessmentRVAssessmentGoalDateTextView
                    .setText(assessment.getGoalDate().format(DateTimeUtils.SHORT_DATE));
            viewHolder.bind(assessment, listener);
        } else {
            viewHolder.courseDetailAssessmentRVAssessmentTextView
                    .setText(R.string.no_assessment);
        }
    }

    public Assessment getAssessmentAt(int position) {
        return course.getAssessments().get(position);
    }

    public void setCourse(CourseWithAssessments courseWithAssessments) {
        course = courseWithAssessments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (course != null) {
            if (course.getAssessments() != null) {
                return course.getAssessments().size();
            }
        }
        return 0;
    }

    public class CourseDetailAssessmentViewHolder extends RecyclerView.ViewHolder {
        ImageView courseDetailAssessmentRVAssessmentTypeImageView;
        TextView courseDetailAssessmentRVAssessmentTextView;
        TextView courseDetailAssessmentRVStatusTextView;
        TextView courseDetailAssessmentRVAssessmentGoalDateTextView;
        TextView courseDetailAssessmentRVCourseDueDateTextView;

        public CourseDetailAssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            courseDetailAssessmentRVAssessmentTypeImageView =
                    itemView.findViewById(R.id.courseDetailAssessmentRVAssessmentTypeImageView);
            courseDetailAssessmentRVAssessmentTextView =
                    itemView.findViewById(R.id.courseDetailAssessmentRVAssessmentTextView);
            courseDetailAssessmentRVStatusTextView =
                    itemView.findViewById(R.id.courseDetailAssessmentRVStatusTextView);
            courseDetailAssessmentRVAssessmentGoalDateTextView =
                    itemView.findViewById(R.id.courseDetailAssessmentRVAssessmentGoalDateTextView);
            courseDetailAssessmentRVCourseDueDateTextView =
                    itemView.findViewById(R.id.courseDetailAssessmentRVCourseDueDateTextView);
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
