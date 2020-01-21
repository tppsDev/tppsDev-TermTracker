package com.example.termtracker.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class CourseWithAssessmentsListAdapter extends RecyclerView.Adapter<CourseWithAssessmentsListAdapter.CourseViewHolder> {
    private final LayoutInflater courseInflater;
    private List<CourseWithAssessments> courseList;
    private final OnItemClickListener listener;

    public CourseWithAssessmentsListAdapter(Context context, OnItemClickListener listener) {
        courseInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = courseInflater.inflate(R.layout.course_recyclerview_item, viewGroup, false);

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder courseViewHolder, int position) {
        if (courseList != null) {
            CourseWithAssessments course = courseList.get(position);
            courseViewHolder.courseRVCourseTextView.setText(course.getCourse().getTitle());
            courseViewHolder.courseRVStartDateTextView.setText(course.getCourse().getStartDate().format(DateTimeUtils.SHORT_DATE));
            courseViewHolder.courseRVEndDateTextView.setText(course.getCourse().getAnticipatedEndDate().format(DateTimeUtils.SHORT_DATE));
            courseViewHolder.bind(course.getCourse(), listener);
        } else {
            courseViewHolder.courseRVCourseTextView.setText(R.string.no_courses);
        }
    }

    public void setCourses(List<CourseWithAssessments> courses) {
        courseList = courses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (courseList != null) {
            return courseList.size();
        } else {
            return 0;
        }
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseRVCourseTextView;
        TextView courseRVStartDateTextView;
        TextView courseRVEndDateTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseRVCourseTextView = itemView.findViewById(R.id.courseRVCourseTextView);
            courseRVStartDateTextView = itemView.findViewById(R.id.courseRVStartDateTextView);
            courseRVEndDateTextView = itemView.findViewById(R.id.courseRVEndDateTextView);
        }

        public void bind(final Course course, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(course);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Course item);
    }
}
