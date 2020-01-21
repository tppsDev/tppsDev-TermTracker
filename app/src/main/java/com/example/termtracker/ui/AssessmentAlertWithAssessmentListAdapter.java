package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.AssessmentAlertWithAssessment;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class AssessmentAlertWithAssessmentListAdapter extends RecyclerView.Adapter<AssessmentAlertWithAssessmentListAdapter.AssessmentAlertViewHolder> {
    private final LayoutInflater alertInflater;
    private List<AssessmentAlertWithAssessment> alertList;
    private final OnItemClickListener listener;

    public AssessmentAlertWithAssessmentListAdapter(Context context, OnItemClickListener listener) {
        alertInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssessmentAlertViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = alertInflater
                .inflate(R.layout.assessment_alert_recyclerview_item, viewGroup, false);

        return new AssessmentAlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentAlertViewHolder viewHolder, int position) {
        if (alertList != null) {
            AssessmentAlertWithAssessment alert = alertList.get(position);
            viewHolder.assessmentTextView.setText(alert.getAssessment().getTitle());
            viewHolder.dateTimeTextView
                    .setText(alert.getAssessmentAlert().getAlertTime().format(DateTimeUtils.DATE_TIME));
            viewHolder.messageTextView.setText(alert.getAssessmentAlert().getMessage());
            viewHolder.bind(alert, listener);
        } else {
            viewHolder.assessmentTextView.setText(R.string.no_alerts);
        }
    }

    public AssessmentAlert getAssessmentAlertAt(int position) {
        return alertList.get(position).getAssessmentAlert();
    }

    public void setAssessmentAlerts(List<AssessmentAlertWithAssessment> alerts) {
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

    public class AssessmentAlertViewHolder  extends RecyclerView.ViewHolder {
        TextView assessmentTextView;
        TextView dateTimeTextView;
        TextView messageTextView;

        public AssessmentAlertViewHolder(@NonNull View itemView) {
            super(itemView);
            assessmentTextView = itemView.findViewById(R.id.assessmentAlertRVAssessmentTextView);
            dateTimeTextView = itemView.findViewById(R.id.assessmentAlertRVDateTimeTextView);
            messageTextView = itemView.findViewById(R.id.assessmentAlertRVMessageTextView);
        }

        public void bind(final AssessmentAlertWithAssessment alert, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(alert);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AssessmentAlertWithAssessment item);
    }
}
