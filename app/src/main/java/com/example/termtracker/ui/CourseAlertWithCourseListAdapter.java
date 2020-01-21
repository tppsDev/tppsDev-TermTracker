package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.CourseAlertWithCourse;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class CourseAlertWithCourseListAdapter extends RecyclerView.Adapter<CourseAlertWithCourseListAdapter.CourseAlertViewHolder> {
    private final LayoutInflater alertInflater;
    private List<CourseAlertWithCourse> alertList;
    private final OnItemClickListener listener;

    public CourseAlertWithCourseListAdapter(Context context, OnItemClickListener listener) {
        alertInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseAlertViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = alertInflater
                .inflate(R.layout.course_alert_recyclerview_item, viewGroup, false);

        return new CourseAlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAlertViewHolder viewHolder, int position) {
        if (alertList != null) {
            CourseAlertWithCourse alert = alertList.get(position);
            viewHolder.courseTextView.setText(alert.getCourse().getTitle());
            viewHolder.dateTimeTextView
                    .setText(alert.getCourseAlert().getAlertTime().format(DateTimeUtils.DATE_TIME));
            viewHolder.messageTextView.setText(alert.getCourseAlert().getMessage());
            viewHolder.bind(alert, listener);
        } else {
            viewHolder.courseTextView.setText(R.string.no_alerts);
        }
    }

    public CourseAlert getCourseAlertAt(int position) {
        return alertList.get(position).getCourseAlert();
    }

    public void setCourseAlerts(List<CourseAlertWithCourse> alerts) {
        alertList = alerts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (alertList !=null) {
            return alertList.size();
        } else {
            return 0;
        }
    }

    public class CourseAlertViewHolder  extends RecyclerView.ViewHolder {
        TextView courseTextView;
        TextView dateTimeTextView;
        TextView messageTextView;

        public CourseAlertViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTextView = itemView.findViewById(R.id.courseAlertRVCourseTextView);
            dateTimeTextView = itemView.findViewById(R.id.courseAlertRVDateTimeTextView);
            messageTextView = itemView.findViewById(R.id.courseAlertRVMessageTextView);
        }

        public void bind(final CourseAlertWithCourse alert, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(alert);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CourseAlertWithCourse item);
    }
}
