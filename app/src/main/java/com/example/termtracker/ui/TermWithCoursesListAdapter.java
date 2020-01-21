package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.TermWithCourses;
import com.example.termtracker.util.DateTimeUtils;

import java.util.List;

public class TermWithCoursesListAdapter extends RecyclerView.Adapter<TermWithCoursesListAdapter.TermViewHolder> {
    private LayoutInflater termInflater;
    private final OnItemClickListener listener;
    private List<TermWithCourses> termList;

    public TermWithCoursesListAdapter(Context context, OnItemClickListener listener) {
        termInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = termInflater.inflate(R.layout.term_recyclerview_item, viewGroup,false);

        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder termHolder, int position) {
        if (termList != null) {
            TermWithCourses term = termList.get(position);
            termHolder.termRVTermTextView.setText(term.getTerm().getTitle());
            termHolder.termRVCourseCountTextView.setText(String.valueOf(term.getCourses().size()));
            termHolder.termRVStartTextView
                    .setText(term.getTerm().getStartDate().format(DateTimeUtils.SHORT_DATE));
            termHolder.termRVEndTextView
                    .setText(term.getTerm().getEndDate().format(DateTimeUtils.SHORT_DATE));
        } else {
            termHolder.termRVTermTextView.setText(R.string.no_terms);
        }
        termHolder.bind(termList.get(position), listener);
    }

    public TermWithCourses getTermAt(int position) {
        return termList.get(position);
    }

    public void setTerms(List<TermWithCourses> terms) {
        termList = terms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (termList != null) {
            return termList.size();
        } else {
            return 0;
        }
    }

    public class TermViewHolder extends RecyclerView.ViewHolder {
        TextView termRVTermTextView;
        TextView termRVCourseCountTextView;
        TextView termRVStartTextView;
        TextView termRVEndTextView;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            termRVTermTextView = itemView.findViewById(R.id.termRVTermTextView);
            termRVCourseCountTextView = itemView.findViewById(R.id.termRVCourseCountTextView);
            termRVStartTextView = itemView.findViewById(R.id.termRVStartTextView);
            termRVEndTextView = itemView.findViewById(R.id.termRVEndTextView);
        }

        public void bind(final TermWithCourses term, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(term);
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick(TermWithCourses term);
    }
}
