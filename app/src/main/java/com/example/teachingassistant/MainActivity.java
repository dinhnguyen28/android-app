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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btnAddClass;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    Toolbar toolbar;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddClass = findViewById(R.id.btnAddClass);
        btnAddClass.setOnClickListener(v -> showDialog());

        loadDataDBFirst();

        recyclerView = findViewById(R.id.recyclerViewClass);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);

        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));
        setToolbar();

    }

    private void loadDataDB() {
        classItems.clear();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Class").child(userName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classInfo = "", className = "", numOfLesson = "";
                ClassDetail detail = new ClassDetail();

                for (DataSnapshot child : snapshot.getChildren()) {

                    detail = child.getValue(ClassDetail.class);

                    className = detail.getClassName();
                    classInfo = detail.getClassInfo();
                    numOfLesson = snapshot.child(userName + "_" + className).child("numOfLesson").getValue(String.class);

                    ClassItem classItem = new ClassItem(className, classInfo, numOfLesson);
                    classItems.add(classItem);
                    classAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadDataDBFirst() {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Class").child(userName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classInfo = "", className = "", numOfLesson = "";
                ClassDetail detail = new ClassDetail();

                for (DataSnapshot child : snapshot.getChildren()) {

                    detail = child.getValue(ClassDetail.class);

                    className = detail.getClassName();
                    classInfo = detail.getClassInfo();

                    numOfLesson = snapshot.child(userName + "_" + className).child("numOfLesson").getValue(String.class);

                    ClassItem classItem = new ClassItem(className, classInfo, numOfLesson);
                    classItems.add(classItem);
                    classAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        TextView title = toolbar.findViewById(R.id.titleToolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitleToolbar);

        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("CLASSES");

        subtitle.setVisibility(View.GONE);
        save.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);
        toolbar.inflateMenu(R.menu.teacher_home_class_activity);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private void gotoItemActivity(int position) {
        Intent mainAccIntent = getIntent();
        String username = mainAccIntent.getStringExtra("username");


        String fullName = mainAccIntent.getStringExtra("fullName");

        Intent intent = new Intent(MainActivity.this, StudentActivity.class);

        intent.putExtra("username", username);
        intent.putExtra("className", classItems.get(position).getClassName());
        intent.putExtra("classInfo", classItems.get(position).getClassInfo());
        intent.putExtra("noLesson", classItems.get(position).getNumberLesson());
        intent.putExtra("position", position);
        intent.putExtra("fullName", fullName);


        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showEditClassDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showDialog() {
        MyClassDialog dialog = new MyClassDialog();
        dialog.show(getSupportFragmentManager(), MyClassDialog.ADD_CLASS_DIALOG);
        dialog.setListener((className, classInfo, numbsSession) -> addClass(className, classInfo, numbsSession));

    }

    private void showEditClassDialog(int position) {
        MyClassDialog dialog = new MyClassDialog();
        dialog.show(getSupportFragmentManager(), MyClassDialog.EDIT_CLASS_DIALOG);
        dialog.setListener((className, classInfo, numbsSession) -> editClass(position, className, classInfo, numbsSession));

    }

    private void addClass(String className, String classInfo, String classSession) {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        if (className.equals(""))
            Toast.makeText(this, "Please enter class name!", Toast.LENGTH_SHORT).show();
        else if (classSession.equals(""))
            Toast.makeText(this, "Please enter No.lesson!", Toast.LENGTH_SHORT).show();
        else {


            mDatabase = FirebaseDatabase.getInstance().getReference("Class");
            mDatabase.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int d =0;
                    for (DataSnapshot child : snapshot.getChildren()) {

                        String x = child.getKey();
                        if (x.equals(userName+"_"+className)){
                            d++;
                        }
                       // Log.d("TAG", "onDataChange: "+x);

                    }

                    if(d==0)
                        addClassToDb(className,classInfo,classSession);
                    else
                        Toast.makeText(MainActivity.this, "Class Name is exist", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





        }


    }

    private void addClassToDb(String className, String classInfo, String classSession) {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        mDatabase = FirebaseDatabase.getInstance().getReference("Class");
        mDatabase.child(userName).child(userName + "_" + className).child("userName").setValue(userName);
        mDatabase.child(userName).child(userName + "_" + className).child("className").setValue(className);
        mDatabase.child(userName).child(userName + "_" + className).child("classInfo").setValue(classInfo);
        mDatabase.child(userName).child(userName + "_" + className).child("numOfLesson").setValue(classSession);

     //   String idClass = userName + "_" + className;
        loadDataDB();

    }

    private void insertClassChat(String userName, String idClass) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
        String currentDateandTime = sdf.format(new Date());

        String date = currentDateandTime;
        String IDkey = mDatabase.push().getKey();

        sdf = new SimpleDateFormat("HH:mm");
        currentDateandTime = sdf.format(new Date());

        String time = currentDateandTime;

        Intent intent = getIntent();
        String fullName = intent.getStringExtra("fullName");

        String fMess = "Hello I'm " + fullName + ". The classroom's Teacher. Contact me if you have any problem.";

        mDatabase = FirebaseDatabase.getInstance().getReference("Chat");
        mDatabase.child(idClass).child(IDkey).child("userName").setValue(userName);
        mDatabase.child(idClass).child(IDkey).child("contentMessage").setValue(fMess);
        mDatabase.child(idClass).child(IDkey).child("Date").setValue(date);
        mDatabase.child(idClass).child(IDkey).child("Time").setValue(time);
        mDatabase.child(idClass).child(IDkey).child("fullName").setValue(fullName);


    }

    private void editClass(int position, String className, String classInfo, String numbsSession) {
        //db here

        String numbsSessionDB = classItems.get(position).getNumberLesson();
        String classInfoDB = classItems.get(position).getClassInfo();

        if (numbsSession.equals("")){
            numbsSession = numbsSessionDB;
        }
        if (classInfo.equals("")){
            classInfo = classInfoDB;
        }
           // Toast.makeText(this, "Please enter No.lesson", Toast.LENGTH_SHORT).show();

        if(classInfo.equals("") && numbsSession.equals("")){
            Toast.makeText(this, "Please enter something!", Toast.LENGTH_SHORT).show();
        }else{
            String className_position = classItems.get(position).getClassName();

            Intent intent = getIntent();
            String userName = intent.getStringExtra("username");
            String idCLass = userName + "_" + className_position;

            mDatabase = FirebaseDatabase.getInstance().getReference("Class");

            mDatabase.child(userName).child(userName + "_" + className_position).child("classInfo").setValue(classInfo);
            mDatabase.child(userName).child(userName + "_" + className_position).child("numOfLesson").setValue(numbsSession);


            classItems.get(position).setClassInfo(classInfo);
            classItems.get(position).setNumberLesson(numbsSession);

            classAdapter.notifyItemChanged(position);


            mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");

            String finalNumbsSession = numbsSession;
            String finalClassInfo = classInfo;
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot child : snapshot.getChildren()) {

                        String x = child.getKey();
                        updateResultLearning(x,idCLass, finalNumbsSession, finalClassInfo);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void updateResultLearning(String id,String idClass,String numbsSession,String classInfo){
        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(id).child(idClass).child("NoLession").setValue(numbsSession);

        int numOfDayBanned = Integer.parseInt(numbsSession) * 20 / 100;
        mDatabase.child(id).child(idClass).child("numOfDayBanned").setValue(numOfDayBanned);
        mDatabase.child(id).child(idClass).child("classInfo").setValue(classInfo);



    }

    private void removeClassAttendance(String userName, String idClass) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (userName.equals(child.getKey()))
                        d++;

                }
                if (d != 0)
                    removeClassAttendance1(userName, idClass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void removeClassAttendance1(String userName, String idClass) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (idClass.equals(child.getKey()))
                        d++;

                }
                if (d != 0)
                    removeClassAttendanceLast(userName, idClass);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void removeClassAttendanceLast(String userName, String idClass) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");
        mDatabase.child(userName).child(idClass).removeValue();

    }

    private void deleteClass(int position) {
        //db here

        String className_position = classItems.get(position).getClassName();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        String idClass = userName + "_" + className_position;

        mDatabase = FirebaseDatabase.getInstance().getReference("ClassStudent");
        mDatabase.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (idClass.equals(child.getKey()))
                        d++;

                }
                if (d != 0) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ClassStudent");
                    reference.child(userName).child(idClass).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        removeClassAttendance(userName, idClass);


        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {

                    deleteDataOnResultLearning(child.getKey(), idClass);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference("Chat");
        mDatabase.child(idClass).removeValue();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Class");
        reference.child(userName).child(userName + "_" + className_position).removeValue();

        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);
        //  loadDataDB();
    }

    private void deleteDataOnResultLearning(String id, String idClass) {

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {

                    if (idClass.equals(child.getKey())) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ResultLearning");
                        reference.child(id).child(idClass).removeValue();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.logoutTeacher) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
        return true;
    }

}