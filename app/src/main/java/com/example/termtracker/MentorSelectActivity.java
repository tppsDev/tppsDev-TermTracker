package com.example.termtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.Mentor;
import com.example.termtracker.ui.MentorListAdapter;
import com.example.termtracker.view_model.MentorSelectViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MentorSelectActivity extends AppCompatActivity implements ConstraintCheck {
    private static final int ADD_MENTOR_REQ_CODE = 1983;

    MentorSelectViewModel viewModel;
    MentorListAdapter mentorListAdapter;

    Mentor selectedMentor;

    FloatingActionButton addMentorFab;
    int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_select);

        Intent intent = getIntent();
        if (!intent.hasExtra(getString(R.string.course_id_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            courseId = intent.getIntExtra(getString(R.string.course_id_key), -1);
            initView();
            initListeners();
        }
    }

    private void initView() {
        viewModel = ViewModelProviders.of(this).get(MentorSelectViewModel.class);
        addMentorFab = findViewById(R.id.mentorSelectAddMentorFab);

        RecyclerView mentorRV = findViewById(R.id.mentorSelectRV);
        mentorListAdapter = new MentorListAdapter(this, mentorItemOnClickListener);
        mentorRV.setAdapter(mentorListAdapter);
        mentorRV.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getAllMentors().observe(this, new Observer<List<Mentor>>() {
            @Override
            public void onChanged(List<Mentor> mentors) {
                mentorListAdapter.setMentors(mentors);
            }
        });

        final ColorDrawable background = new ColorDrawable(ContextCompat.getColor(this, R.color.colorErrorRed));
        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_36);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 0;
                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());


                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                deleteIcon.setBounds(iconLeft,iconTop,iconRight,iconBottom);

                background.draw(c);
                deleteIcon.draw(c);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                selectedMentor = mentorListAdapter.getMentorAt(viewHolder.getAdapterPosition());
                int mentorId = selectedMentor.getId();

                viewModel.getCourseCountByMentorId(mentorId, MentorSelectActivity.this);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(mentorRV);
    }

    private void initListeners() {
        addMentorFab.setOnClickListener(v -> {
            Intent mentorEditIntent = new Intent(MentorSelectActivity.this, MentorEditActivity.class);
            mentorEditIntent.putExtra(getString(R.string.edit_mentor_mode_key), 1);
            startActivityForResult(mentorEditIntent, ADD_MENTOR_REQ_CODE);
        });
    }

    private MentorListAdapter.OnItemClickListener mentorItemOnClickListener =
            new MentorListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Mentor mentor) {
            handleMentorSelection(mentor);
        }
    };

    private void handleMentorSelection(Mentor mentor) {
        viewModel.insertCourseMentorXref(new CourseMentorXRef(courseId, mentor.getId()));
        this.setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_MENTOR_REQ_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Mentor added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mentor not added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void handleConstraintCheck(int count) {
        if (count < 1) {
            viewModel.deleteMentor(selectedMentor);
        } else {
            Snackbar snackbar = Snackbar.make(addMentorFab,
                    getString(R.string.delete_mentor_with_course_error),
                    5000);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            snackbar.show();
            mentorListAdapter.notifyDataSetChanged();
        }
    }
}
