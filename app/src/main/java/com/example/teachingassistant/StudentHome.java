package com.example.teachingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentHome extends AppCompatActivity {
    FloatingActionButton btnAddClassStudent;
    RecyclerView recyclerViewClassStudent;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<ClassItem> classItems = new ArrayList<>();
    ArrayList<ClassItemStudentHome> myIdItems = new ArrayList<>();

    Toolbar toolbar;
    String TAG = "TAG";

    private void loadDataDBFirst() {

        Intent intent = getIntent();
        String IdStudent = intent.getStringExtra("IdStudent");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ResultLearning").child(IdStudent);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String IdClassName = "", className = "", NoLession = "", pre = "", absent = "", NoBanned = "", classInfo = "";
                ClassDetail detail = new ClassDetail();

                for (DataSnapshot child : snapshot.getChildren()) {

                    NoLession = child.child("NoLession").getValue(String.class);
                    classInfo = child.child("classInfo").getValue(String.class);
                    className = child.child("ClassName").getValue(String.class);
                    IdClassName = child.child("IdClassName").getValue(String.class);

                    ClassItem classItem = new ClassItem(className, classInfo, NoLession);
                    classItems.add(classItem);

                    ClassItemStudentHome myIdItem = new ClassItemStudentHome(IdClassName);
                    myIdItems.add(myIdItem);

                    classAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentHome.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataDB() {

        classItems.clear();
        classAdapter.notifyDataSetChanged();


        Intent intent = getIntent();
        String IdStudent = intent.getStringExtra("IdStudent");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ResultLearning").child(IdStudent);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String IdClassName = "", className = "", NoLession = "", pre = "", absent = "", NoBanned = "", classInfo = "";
                ClassDetail detail = new ClassDetail();

                for (DataSnapshot child : snapshot.getChildren()) {

                    NoLession = child.child("NoLession").getValue(String.class);
                    classInfo = child.child("classInfo").getValue(String.class);
                    className = child.child("ClassName").getValue(String.class);
                    IdClassName = child.child("IdClassName").getValue(String.class);

                    ClassItem classItem = new ClassItem(className, classInfo, NoLession);
                    classItems.add(classItem);

                    ClassItemStudentHome myIdItem = new ClassItemStudentHome(IdClassName);
                    myIdItems.add(myIdItem);

                    classAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentHome.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        loadDataDBFirst();

        recyclerViewClassStudent = findViewById(R.id.recyclerViewClassStudent);
        recyclerViewClassStudent.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewClassStudent.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerViewClassStudent.setAdapter(classAdapter);

        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));
        setToolbar();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        TextView title = toolbar.findViewById(R.id.titleToolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitleToolbar);

        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("CLASSES");

        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        toolbar.inflateMenu(R.menu.student_home_activity);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private boolean onMenuItemClick(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.refreshStudent) {

            loadDataDB();

        }
        if (menuItem.getItemId() == R.id.logout) {
            startActivity(new Intent(StudentHome.this, Login.class));
            finish();
        }
        return true;
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this, StudentNotification.class);
        Intent intent1 = getIntent();
        String IdStudent = intent1.getStringExtra("IdStudent");
        String fullName = intent1.getStringExtra("fullName");
        String userName = intent1.getStringExtra("userName");

        intent.putExtra("IdStudent", IdStudent);
        intent.putExtra("className", classItems.get(position).getClassName());
        intent.putExtra("classInfo", classItems.get(position).getClassInfo());
        intent.putExtra("No.Lesson", classItems.get(position).getNumberLesson());
        intent.putExtra("position", position);

        intent.putExtra("idClass", myIdItems.get(position).getIdClassName());
        intent.putExtra("fullName", fullName);
        intent.putExtra("userName", userName);

        startActivity(intent);
    }

}