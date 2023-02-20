package com.example.teachingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StudentNotification extends AppCompatActivity {
    FloatingActionButton btnChatStudent;
    TextView studentClassName, studentClassInfo, studentNumOfLesson, studentPresent, studentAbsent, studentNotification;
    private DatabaseReference mDatabase;
    String TAG = "TAG";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notification);

        studentClassInfo = findViewById(R.id.studentClassInfo);
        studentClassName = findViewById(R.id.studentClassName);
        studentNumOfLesson = findViewById(R.id.studentNumOfLesson);
        studentNotification = findViewById(R.id.studentNotification);
        studentPresent = findViewById(R.id.studentPresent);
        studentAbsent = findViewById(R.id.studentAbsent);

        Intent intent = new Intent(StudentNotification.this, ChatActivityList.class);

        Intent intent1 = getIntent();
        String className = intent1.getStringExtra("className");
        String classInfo = intent1.getStringExtra("classInfo");
        String NoLesson = intent1.getStringExtra("No.Lesson");
        String idClass = intent1.getStringExtra("idClass");
        String IdStudent = intent1.getStringExtra("IdStudent");
//phan tren
        studentClassInfo.setText(classInfo);
        studentClassName.setText(className);
        studentNumOfLesson.setText("No.Lesson: " + NoLesson);
//phan thong bao + phan diem danh
        getResultLesson(IdStudent, idClass);
        setToolbar();
        btnChatStudent = findViewById(R.id.btnChatStudent);
        btnChatStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentOld = getIntent();

                String fullName = intentOld.getStringExtra("fullName");
                String userName = intentOld.getStringExtra("userName");
                String idClass = intentOld.getStringExtra("idClass");

                intent.putExtra("fullName", fullName);
                intent.putExtra("userName", userName);
                intent.putExtra("idClass", idClass);

                startActivity(intent);
            }
        });

    }

    private void loadDataDB() {

        Intent intent1 = getIntent();
        String idClass = intent1.getStringExtra("idClass");
        String IdStudent = intent1.getStringExtra("IdStudent");

        getResultLesson(IdStudent, idClass);

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(IdStudent).child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classInfo = snapshot.child("classInfo").getValue(String.class);
                String NoLession = snapshot.child("NoLession").getValue(String.class);

                studentClassInfo.setText(classInfo);
                studentNumOfLesson.setText("No.Lesson: " + NoLession);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        TextView title = toolbar.findViewById(R.id.titleToolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitleToolbar);

        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("NOTIFICATION");

        //title.setVisibility(View.GONE);
        subtitle.setVisibility(View.GONE);

        save.setVisibility(View.INVISIBLE);

        toolbar.inflateMenu(R.menu.notification_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));

        back.setOnClickListener(v -> onBackPressed());
    }

    private void getResultLesson(String idStudent, String idClass) {

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(idStudent).child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Notification = "";
                int k = 0;

                String IdClassName = snapshot.child("IdClassName").getValue(String.class);
                int dayPresent = snapshot.child("Present").getValue(int.class);
                int dayAbsent = snapshot.child("absent").getValue(int.class);
                int noBanned = snapshot.child("numOfDayBanned").getValue(int.class);

                studentPresent.setText(String.valueOf(dayPresent));
                studentAbsent.setText(String.valueOf(dayAbsent));

                if (noBanned - dayAbsent == 1) {
                    Notification = "Warning!. You only have 1 last day off. ";
                } else if (noBanned - dayAbsent == 0) {
                    Notification = "Warning!. You have 0 last day off.";
                } else if (noBanned - dayAbsent < 0) {
                    k=1;
                    Notification = "You have been banned from exam!. Please contact to your teacher if this is a mistake";
                } else {
                    Notification = "There is no announcement!. Have a good day <3 ";
                }

                studentNotification.setText(Notification);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refreshNotify) {

            loadDataDB();

        }
        return true;
    }

}