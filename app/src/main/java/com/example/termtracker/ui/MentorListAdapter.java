package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.Mentor;

import java.util.List;

public class MentorListAdapter extends RecyclerView.Adapter<MentorListAdapter.MentorViewHolder> {
    private LayoutInflater inflater;
    private List<Mentor> mentorList;
    private final OnItemClickListener listener;

    public MentorListAdapter(Context context, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MentorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.mentor_recyclerview_item, viewGroup, false);

        return new MentorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorViewHolder viewHolder, int position) {
        if (mentorList != null) {
            Mentor mentor = mentorList.get(position);
            viewHolder.nameTextView.setText(mentor.getDisplayName());
            viewHolder.bind(mentor, listener);
        } else {
            viewHolder.nameTextView.setText(R.string.no_mentors);
        }
    }

    public Mentor getMentorAt(int position) {
        return mentorList.get(position);
    }

    public void setMentors(List<Mentor> mentors) {
        mentorList = mentors;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mentorList != null) {
            return mentorList.size();
        } else {
            return 0;
        }
    }

    public class MentorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public MentorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.mentorRVNameTextView);
        }

        public void bind(final Mentor mentor, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(mentor);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Mentor item);
    }
}
