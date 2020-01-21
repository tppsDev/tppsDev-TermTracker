package com.example.termtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.CourseWithMentors;
import com.example.termtracker.entity.Mentor;
import com.example.termtracker.ui.MentorListAdapter;
import com.example.termtracker.view_model.CourseMentorsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


public class CourseMentorsActivity extends AppCompatActivity {
    private static final int ADD_MENTOR_REQ_CODE = 98;

    CourseMentorsViewModel viewModel;
    ActionMenuView menuView;
    MentorListAdapter mentorListAdapter;

    TextView subHeaderTextView;
    FloatingActionButton addMentorFab;

    CourseWithMentors selectedCourseWithMentors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_mentors);

        Intent intent = getIntent();
        if (!intent.hasExtra(getString(R.string.course_with_mentors_object_key))) {
            finish();
        } else {
            selectedCourseWithMentors = intent.getParcelableExtra(getString(R.string.course_with_mentors_object_key));
            initView();
            initListeners();
            //loadData();
        }
    }

    private void initView() {
        subHeaderTextView = findViewById(R.id.courseMentorsSubHeaderTextView);
        addMentorFab = findViewById(R.id.courseMentorsAddMentorFab);

        viewModel = ViewModelProviders.of(this).get(CourseMentorsViewModel.class);

        Toolbar appBar = findViewById(R.id.courseMentorsTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        RecyclerView mentorRV = findViewById(R.id.courseMentorsRV);
        mentorListAdapter = new MentorListAdapter(this, mentorItemClickListener);
        mentorRV.setAdapter(mentorListAdapter);
        mentorRV.setLayoutManager(new LinearLayoutManager(this));

        final int courseId = selectedCourseWithMentors.getCourse().getId();
        viewModel.getCourseWithMentorsByCourseId(courseId).observe(this, new Observer<CourseWithMentors>() {
            @Override
            public void onChanged(CourseWithMentors courseWithMentors) {
                mentorListAdapter.setMentors(courseWithMentors.getCourseMentors());
                loadData();
            }
        });

        final ColorDrawable background = new ColorDrawable(ContextCompat.getColor(this, R.color.colorErrorRed));
        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_remove_circle_white_36);
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
                Mentor selectedMentor = mentorListAdapter.getMentorAt(viewHolder.getAdapterPosition());
                int mentorId = selectedMentor.getId();
                int courseId = selectedCourseWithMentors.getCourse().getId();

                CourseMentorXRef courseMentorXRef = new CourseMentorXRef(courseId, mentorId);

                viewModel.deleteCourseMentorXRef(courseMentorXRef);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(mentorRV);
    }

    private void initListeners() {
        addMentorFab.setOnClickListener(v -> handleAddMentorFab());
        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);
    }

    private void handleAddMentorFab() {
        Intent mentorSelectIntent = new Intent(CourseMentorsActivity.this, MentorSelectActivity.class);
        mentorSelectIntent.putExtra(getString(R.string.course_id_key), selectedCourseWithMentors.getCourse().getId());
        startActivityForResult(mentorSelectIntent, ADD_MENTOR_REQ_CODE);
    }

    private MentorListAdapter.OnItemClickListener mentorItemClickListener =
            new MentorListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Mentor mentor) {
            Intent mentorDetailIntent =
                    new Intent(CourseMentorsActivity.this, MentorDetailActivity.class);
            mentorDetailIntent.putExtra(getString(R.string.mentor_id_key), mentor.getId());
            startActivity(mentorDetailIntent);
        }
    };

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( CourseMentorsActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(CourseMentorsActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(CourseMentorsActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(CourseMentorsActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(CourseMentorsActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    private void loadData() {
        String subHeaderString = selectedCourseWithMentors.getCourse().getTitle()
                + "\nMentor List";
        subHeaderTextView.setText(subHeaderString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MENTOR_REQ_CODE) {
            handleAddMentorResult(resultCode);
        }
    }

    private void handleAddMentorResult(int resultCode) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "Add mentor error", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Add mentor cancelled", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "Add mentor successful", Toast.LENGTH_SHORT).show();
        }
    }
}
