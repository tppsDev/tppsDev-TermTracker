package com.example.termtracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.R;
import com.example.termtracker.entity.Note;

import java.util.List;

public class NoteTitleListAdapter extends RecyclerView.Adapter<NoteTitleListAdapter.NoteTitleViewHolder> {
    private final LayoutInflater inflater;
    private List<Note> noteList;
    private final OnItemClickListener listener;

    public NoteTitleListAdapter(Context context, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteTitleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater
                .inflate(R.layout.note_title_recyclerview_item,
                        viewGroup, false);

        return new NoteTitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteTitleViewHolder viewHolder, int position) {
        if (noteList != null) {
            Note note = noteList.get(position);
            viewHolder.noteRVTitleTextView.setText(note.getTitle());
            viewHolder.bind(note,listener);
        } else {
            viewHolder.noteRVTitleTextView.setText(R.string.no_notes);
        }
    }

    public Note getNoteAt(int position) {
        return noteList.get(position);
    }

    public void setNotes(List<Note> notes) {
        noteList = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (noteList != null) {
            return noteList.size();
        } else {
            return 0;
        }
    }

    public class NoteTitleViewHolder extends RecyclerView.ViewHolder{
        TextView noteRVTitleTextView;

        public NoteTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            noteRVTitleTextView = itemView
                    .findViewById(R.id.noteTitleRVTitleTextView);
        }

        public void bind(final Note note, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClick(note);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Note item);
    }
}
