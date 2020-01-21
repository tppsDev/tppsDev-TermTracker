package com.example.termtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.termtracker.entity.TermWithCourses;
import com.example.termtracker.ui.TermWithCoursesListAdapter;
import com.example.termtracker.view_model.AllTermsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TermActivity extends AppCompatActivity {
    private static final int EDIT_TERM_REQ_CODE = 19;
    ActionMenuView menuView;
    TextView contentTermUserTextView;
    TermWithCoursesListAdapter termAdapter;

    AllTermsViewModel termsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        termsViewModel = ViewModelProviders.of(this).get(AllTermsViewModel.class);

        Toolbar appBar = findViewById(R.id.termTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        SharedPreferences sPref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        contentTermUserTextView = this.findViewById(R.id.contentTermUserTextView);
        String user = sPref.getString(getString(R.string.user_name_key), "User");
        user = user + "'s Terms";
        contentTermUserTextView.setText(user);

        RecyclerView termRecyclerView = findViewById(R.id.termRecyclerView);

        termAdapter =
                new TermWithCoursesListAdapter(this,
                        new TermWithCoursesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TermWithCourses term) {
                Intent termDetailIntent =
                        new Intent(TermActivity.this, TermDetailActivity.class);
                termDetailIntent.putExtra(getString(R.string.term_id_key), term.getTerm().getId());
                startActivity(termDetailIntent);
            }
        });

        termRecyclerView.setAdapter(termAdapter);
        termRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        termsViewModel
                .getAllTermsWithCourses()
                .observe(this, new Observer<List<TermWithCourses>>() {
            @Override
            public void onChanged(List<TermWithCourses> termWithCourses) {
                termAdapter.setTerms(termWithCourses);
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
                TermWithCourses selectedTerm = termAdapter.getTermAt(viewHolder.getAdapterPosition());

                if (selectedTerm.getCourses().size() < 1) {
                    termsViewModel.deleteTerm(selectedTerm.getTerm());
                } else {
                    Snackbar snackbar = Snackbar.make(termRecyclerView,
                            getString(R.string.delete_term_with_courses_error),
                            5000);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    snackbar.show();
                    termAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(termRecyclerView);

        FloatingActionButton fab = findViewById(R.id.termRVAddButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTermIntent =
                        new Intent(TermActivity.this, TermEditActivity.class);
                addTermIntent.putExtra(getString(R.string.edit_term_mode_key), 1);
                startActivityForResult(addTermIntent, EDIT_TERM_REQ_CODE);
            }
        });
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( TermActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(TermActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(TermActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(TermActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_TERM_REQ_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Term added", Toast.LENGTH_LONG).show();
        }  else {
            Toast.makeText(this, "Term add cancelled", Toast.LENGTH_LONG).show();
        }
    }
}
