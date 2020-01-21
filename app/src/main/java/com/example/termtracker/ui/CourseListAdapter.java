package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.Course;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {
    private LayoutInflater inflator;
    private List<Course> courseList;
    private final OnItemClickListener listener;

    public CourseListAdapter(Context context, OnItemClickListener listener) {
        inflator = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflator.inflate(R.layout.course_recyclerview_item, viewGroup, false);

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder courseViewHolder, int position) {
        if (courseList != null) {
            Course course = courseList.get(position);
            courseViewHolder.courseRVCourseTextView
                    .setText(course.getTitle());
            courseViewHolder.courseRVStartDateTextView
                    .setText(course.getStartDate().format(DateTimeUtils.SHORT_DATE));
            courseViewHolder.courseRVEndDateTextView
                    .setText(course.getAnticipatedEndDate().format(DateTimeUtils.SHORT_DATE));
            courseViewHolder.bind(course, listener);
        } else {
            courseViewHolder.courseRVCourseTextView
                    .setText(R.string.no_courses);
        }
    }

    public Course getCourseAt(int position) {
        return courseList.get(position);
    }

    public void setCourses(List<Course> courses) {
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
