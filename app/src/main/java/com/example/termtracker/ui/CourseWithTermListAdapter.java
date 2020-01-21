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
import com.example.termtracker.entity.CourseWithTerm;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class CourseWithTermListAdapter extends RecyclerView.Adapter<CourseWithTermListAdapter.CourseWithTermViewHolder> {
    private LayoutInflater inflator;
    private List<CourseWithTerm> courseList;
    private final OnItemClickListener listener;

    public CourseWithTermListAdapter (Context context, OnItemClickListener listener) {
        inflator = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseWithTermViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflator.inflate(R.layout.course_with_term_recyclerview_item, viewGroup, false);

        return new CourseWithTermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseWithTermViewHolder viewHolder, int position) {
        if (courseList != null) {
            CourseWithTerm course = courseList.get(position);
            viewHolder.courseTextView
                    .setText(course.getCourse().getTitle());
            viewHolder.statusTextView
                    .setText(course.getCourse().getStatus().toString());
            viewHolder.termTextView
                    .setText(course.getTerm().getTitle());
            viewHolder.startDateTextView
                    .setText(course.getCourse().getStartDate().format(DateTimeUtils.SHORT_DATE));
            viewHolder.endDateTextView
                    .setText(course.getCourse().getAnticipatedEndDate().format(DateTimeUtils.SHORT_DATE));
        } else {
            viewHolder.courseTextView
                    .setText(R.string.no_courses);
        }

        viewHolder.bind(courseList.get(position), listener);
    }

    public Course getCourseAt(int position) {
        return courseList.get(position).getCourse();
    }

    public void setCourses(List<CourseWithTerm> courses) {
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

    public class CourseWithTermViewHolder extends RecyclerView.ViewHolder {
        TextView courseTextView;
        TextView statusTextView;
        TextView termTextView;
        TextView startDateTextView;
        TextView endDateTextView;

        public CourseWithTermViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTextView = itemView.findViewById(R.id.courseWithTermRVCourseTextView);
            statusTextView = itemView.findViewById(R.id.courseWithTermRVStatusTextView);
            termTextView = itemView.findViewById(R.id.courseWithTermRVTermTextView);
            startDateTextView = itemView.findViewById(R.id.courseWithTermRVStartDateTextView);
            endDateTextView = itemView.findViewById(R.id.courseWithTermRVEndDateTextView);
        }

        public void bind(final CourseWithTerm course, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(course);
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(CourseWithTerm item);
    }
}
