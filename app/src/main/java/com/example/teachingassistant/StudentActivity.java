package com.example.teachingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class StudentActivity extends AppCompatActivity {
    FloatingActionButton btnChatTeacher;
    Toolbar toolbar;
    private String className;
    private String classInfo;
    private int position;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();

    private ArrayList<StudentItemDetail> StudentItemDetail = new ArrayList<>();

    private MyCalendar calendar;
    private DatabaseReference mDatabase;
    String TAG = "TAG";

    TextView titleToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        btnChatTeacher = findViewById(R.id.btnChatTeacher);
        btnChatTeacher.setOnClickListener(v -> showGroupChat());

        Intent intent = getIntent();

        calendar = new MyCalendar();

        className = intent.getStringExtra("className");
        classInfo = intent.getStringExtra("classInfo");
        position = intent.getIntExtra("position", -1);

        loadDataDBFirst_1();

        TextView className_Student = findViewById(R.id.txtClassName);
        TextView classInfo_Student = findViewById(R.id.txtClassInfo);

        className_Student.setText(className);
        classInfo_Student.setText(classInfo);

        setToolbar();

        recyclerView = findViewById(R.id.recyclerViewStudent);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        studentAdapter = new StudentAdapter(this, studentItems);
        recyclerView.setAdapter(studentAdapter);

        studentAdapter.setOnItemClickListener(position -> changeStatus(position));

        loadStatus();
    }

    private void showGroupChat() {
        Intent intent = new Intent(this, ChatActivityList.class);

        Intent intent1 = getIntent();
        String userName = intent1.getStringExtra("username");
        String fullName = intent1.getStringExtra("fullName");


        String idClass = userName + "_" + className;

        intent.putExtra("userName", userName);
        intent.putExtra("idClass", idClass);
        intent.putExtra("fullName", fullName);

        startActivity(intent);
    }

    private void loadClassStudent() {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ClassStudent").child(userName).child(userName + "_" + className);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idDB = "", fullNameDb = "", Status = "";

                for (DataSnapshot child : snapshot.getChildren()) {
                    idDB = child.child("studentId").getValue(String.class);
                    fullNameDb = child.child("studentName").getValue(String.class);
                    Status = "";

                    StudentItem studentItem = new StudentItem(idDB, fullNameDb, Status);
                    studentItems.add(studentItem);
                    studentAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataDBFirst_1() {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(userName).child(userName + "_" + className).child(calendar.getDate());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idDB = "", fullNameDb = "", Status = "";
                int d = 0, k = -1;
                for (DataSnapshot child : snapshot.getChildren()) {
                    d++;

                    idDB = child.child("id").getValue(String.class);
                    fullNameDb = child.child("fullName").getValue(String.class);
                    Status = child.child("Status").getValue(String.class);

                    StudentItem studentItem = new StudentItem(idDB, fullNameDb, Status);
                    studentItems.add(studentItem);

                    k++;
                    //studentItems.get(k).setStatus(Status);

                    studentAdapter.notifyDataSetChanged();

                }
                if (d == 0) {
                    loadClassStudent();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void changeStatus(int position) {
        String status = studentItems.get(position).getStatus();

        if (status.equals("A")) status = "P";
        else status = "A";
        //db here

        //    Log.d(TAG, "changeStatus: "+position);

        studentItems.get(position).setStatus(status);
        studentAdapter.notifyItemChanged(position);

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        titleToolbar = toolbar.findViewById(R.id.titleToolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitleToolbar);

        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        titleToolbar.setText(calendar.getDate());
        subtitle.setVisibility(View.GONE);

        save.setOnClickListener(v -> saveStatus());
        back.setOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));

    }

    private void onFirstTimeSave_1(String id, String Name, String status, String date, String idAuto, String oldStatus, String classID, String userName) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (classID.equals(child.getKey()))
                        d++;

                }
                if (d == 0)
                    insertDataFirstTime(id, Name, status, date, idAuto, oldStatus, classID, userName);
                else
                    onFirstTimeSave_2(id, Name, status, date, idAuto, oldStatus, classID, userName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onFirstTimeSave_2(String id, String Name, String status, String date, String idAuto, String oldStatus, String classID, String userName) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).child(classID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (date.equals(child.getKey()))
                        d++;

                }
                if (d == 0)
                    insertDataFirstTime(id, Name, status, date, idAuto, oldStatus, classID, userName);
                else
                    checkStatusOld(id, classID, status, userName, date, oldStatus, Name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void insertDataFirstTime(String id, String Name, String status, String date, String idAuto, String oldStatus, String classID, String userName) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("className").setValue(className);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("id").setValue(id);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("fullName").setValue(Name);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("Status").setValue(status);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("Date").setValue(date);

        updateDataResultLearningFirstTime(id, classID, status);

    }

    private void updateDataResultLearningFirstTime(String id, String classID, String status) {

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");

        mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (classID.equals(child.getKey())) {

                        int pre = child.child("Present").getValue(int.class);
                        int abt = child.child("absent").getValue(int.class);
                        int Total = child.child("numOfDayBanned").getValue(int.class);
                        String fullName = child.child("studentName").getValue(String.class);

                        if (status.equals("A"))
                            abt++;
                        else
                            pre++;

                        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
                        mDatabase.child(id).child(classID).child("Present").setValue(pre);
                        mDatabase.child(id).child(classID).child("absent").setValue(abt);

                        Log.d("TAG", "onDataChange: " + classID);

                        if (abt > Total) {
                            mDatabase = FirebaseDatabase.getInstance().getReference("Banned");
                            mDatabase.child(classID).child(id).child("idStudent").setValue(id);
                            mDatabase.child(classID).child(id).child("fullName").setValue(fullName);
                        }

                    }
                }
                ;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void saveStatus() {
        Intent intent = getIntent();
        String noLesson = intent.getStringExtra("noLesson");

        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");
        String idAuto = calendar.getDate();

        int k = -1;

        for (StudentItem studentItem : studentItems) {
            k++;
            String status = studentItem.getStatus();
            String id = studentItem.getStudentId();
            String Name = studentItem.getStudentName();
            String date = calendar.getDate().toString();
            String oldStatus = status;

            if (!status.equals("P")) status = "A";
            //db here

            saveStatusToDb(id, Name, status, date, idAuto, oldStatus);

            studentItems.get(k).setStatus(status);
            studentAdapter.notifyItemChanged(k);
        }

        Toast.makeText(this, "Save successful!", Toast.LENGTH_SHORT).show();

        // loadDataDB();

    }

    private void saveStatusToDb(String id, String Name, String status, String date, String idAuto, String oldStatus) {
        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        String classID = userName + "_" + className;

        onFirstTimeSave(id, Name, status, date, idAuto, oldStatus, classID, userName);

    }

    private void onFirstTimeSave(String id, String Name, String status, String date, String idAuto, String oldStatus, String classID, String userName) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (userName.equals(child.getKey()))
                        d++;

                }
                if (d == 0)
                    insertDataFirstTime(id, Name, status, date, idAuto, oldStatus, classID, userName);
                else
                    onFirstTimeSave_1(id, Name, status, date, idAuto, oldStatus, classID, userName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkStatusOld(String id, String classID, String status, String userName, String date, String oldStatus, String Name) {


        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).child(classID).child(date).child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = "";
                String StatusOld = snapshot.child("Status").getValue(String.class);

                if (!oldStatus.equals("")) {
                    if (StatusOld.equals("A") && status.equals("A"))
                        key = "noThing";
                    else if (StatusOld.equals("P") && status.equals("P"))
                        key = "noThing";
                    else if (StatusOld.equals("A") && status.equals("P"))
                        key = "abt-pre+";
                    else if (StatusOld.equals("P") && status.equals("A"))
                        key = "abt+pre-";
                } else if (status.equals("A")) {
                    key = "abt+";
                } else if (status.equals("P")) {
                    key = "pre+";
                }

                updateResultLearning(id, classID, key, Name, status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateResultLearning(String id, String classID, String Key, String Name, String status) {

        final String[] specialCase = {""};

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");

        mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (classID.equals(child.getKey())) {

                        int pre = child.child("Present").getValue(int.class);
                        int absent = child.child("absent").getValue(int.class);

                        int noLesson = child.child("absent").getValue(int.class);

                        if (Key.equals("noThing")) {

                        } else if (Key.equals("abt-pre+")) {
                            pre++;
                            absent--;

                            specialCase[0] = "special";

                        } else if (Key.equals("abt+pre-")) {
                            absent++;
                            pre--;
                        } else if (Key.equals("abt+")) {
                            absent++;
                        } else if (Key.equals("pre+")) {
                            pre++;
                        }

                        int Total = child.child("numOfDayBanned").getValue(int.class);
                        String fullName = child.child("studentName").getValue(String.class);

                        if (absent > Total) {
                            mDatabase = FirebaseDatabase.getInstance().getReference("Banned");
                            mDatabase.child(classID).child(id).child("idStudent").setValue(id);
                            mDatabase.child(classID).child(id).child("fullName").setValue(fullName);
                        }

                        updateDataResultLearning(id, classID, pre, absent, Name, status, specialCase[0], Total);
                    }
                }
                ;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateDataResultLearning(String id, String classID, int pre, int abt, String Name, String status, String specialCase, int nobanned) {
        String idAuto = calendar.getDate();
        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        String date = idAuto;

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(id).child(classID).child("Present").setValue(pre);
        mDatabase.child(id).child(classID).child("absent").setValue(abt);

        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("className").setValue(className);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("id").setValue(id);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("fullName").setValue(Name);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("Status").setValue(status);
        mDatabase.child(userName).child(userName + "_" + className).child(idAuto).child(id).child("Date").setValue(date);

        if (specialCase.equals("special")) {

            if (abt <= nobanned) {
                updateBanned(userName + "_" + className, id);
            }

        }

    }

    private void updateBanned_1(String idClass, String idStudent) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Banned");
        mDatabase.child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (idStudent.equals(child.getKey()))
                        d++;

                }

                if (d != 0) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Banned");
                    reference.child(idClass).child(idStudent).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateBanned(String idClass, String idStudent) {


        mDatabase = FirebaseDatabase.getInstance().getReference("Banned");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (idClass.equals(child.getKey()))
                        d++;

                }

                if (d != 0)
                    updateBanned_1(idClass, idStudent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadStatus() {
        for (StudentItem studentItem : studentItems) {
            //String status = ....;
            //if(status != null) studentItem.setStatus(status);

        }
        studentAdapter.notifyDataSetChanged();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.addStudentToolbar) {
            showAddStudentDialog();
        } else if (menuItem.getItemId() == R.id.changeDateToolbar) {
            showCalendar();
        } else if (menuItem.getItemId() == R.id.exportData) {
            goToActivity();
        }
        return true;
    }

    private void goToActivity() {

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");
        String className = intent.getStringExtra("className");

        String idClass = userName + "_" + className;


        Intent writefile = new Intent(StudentActivity.this, ExportFile.class);
        writefile.putExtra("idClass", idClass);
        writefile.putExtra("className", className);

        startActivity(writefile);
    }

    private void showCalendar() {

        calendar.show(getSupportFragmentManager(), "");
        calendar.setOnCalendarOnClickListener(this::onCalendarClicked);

    }

    private void onCalendarClicked(int year, int month, int day) {
        calendar.setDate(year, month, day);
        titleToolbar.setText(calendar.getDate());
        loadDataDB();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                deleteStudent(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.ADD_STUDENT_DIALOG);
        dialog.setListener((studentId, studentName) -> addStudent(studentId, studentName));
    }

    private void loadDataDB() {
        studentItems.clear();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(userName).child(userName + "_" + className).child(calendar.getDate());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idDB = "", fullNameDb = "", Status = "";
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    d++;

                    idDB = child.child("id").getValue(String.class);
                    fullNameDb = child.child("fullName").getValue(String.class);
                    Status = child.child("Status").getValue(String.class);

                    StudentItem studentItem = new StudentItem(idDB, fullNameDb, Status);
                    studentItems.add(studentItem);
                    studentAdapter.notifyDataSetChanged();

                }
                if (d == 0) {
                    loadClassStudent();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addStudent(String studentId, String studentName) {
        if (studentId.equals(""))
            Toast.makeText(this, "Please enter ID Student!", Toast.LENGTH_SHORT).show();
        else if (studentId.length() != 8)
            Toast.makeText(this, "ID Student must have 8 number", Toast.LENGTH_SHORT).show();
        else {
            getStudentNameDB(studentId);
            loadDataDB();
        }

    }

    private void insertAttendanceStudentDetail(String userName, String clName, String id, String fullName, String dateDb) {

        //   Log.d(TAG, "insertAttendanceStudentDetail: ");
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).child(userName + "_" + className).child(dateDb).child(id).child("Date").setValue(dateDb);
        mDatabase.child(userName).child(userName + "_" + className).child(dateDb).child(id).child("Status").setValue("");
        mDatabase.child(userName).child(userName + "_" + className).child(dateDb).child(id).child("className").setValue(clName);
        mDatabase.child(userName).child(userName + "_" + className).child(dateDb).child(id).child("fullName").setValue(fullName);
        mDatabase.child(userName).child(userName + "_" + className).child(dateDb).child(id).child("id").setValue(id);

        //  loadDataDB();
    }

    private void insertAttendanceStudent(String userName, String clName, String id, String fullName) {


        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");
        mDatabase.child(userName).child(userName + "_" + className).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {

                    insertAttendanceStudentDetail(userName, clName, id, fullName, child.getKey());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void insertStudentClassStudent(String idDB, String fullNameDb) {
        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        mDatabase = FirebaseDatabase.getInstance().getReference("ClassStudent");
        mDatabase.child(userName).child(userName + "_" + className).child(idDB).child("studentId").setValue(idDB);
        mDatabase.child(userName).child(userName + "_" + className).child(idDB).child("studentName").setValue(fullNameDb);
        mDatabase.child(userName).child(userName + "_" + className).child(idDB).child("className").setValue(className);

        StudentItem studentItem = new StudentItem(idDB, fullNameDb, "");

        studentItems.add(studentItem);
        studentAdapter.notifyDataSetChanged();

        insertAttendanceStudent(userName, className, idDB, fullNameDb);
        insertResultLearningStudent(userName, idDB, fullNameDb);


        //  loadDataDB();

    }

    private void insertResultLearningStudent(String userName, String Id, String fullName) {

        Intent mainAccIntent = getIntent();
        String noLesson = mainAccIntent.getStringExtra("noLesson");


        int numOfDayBanned = Integer.parseInt(noLesson) * 20 / 100;

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(Id).child(userName + "_" + className).child("IdStudent").setValue(Id);
        mDatabase.child(Id).child(userName + "_" + className).child("studentName").setValue(fullName);
        mDatabase.child(Id).child(userName + "_" + className).child("IdClassName").setValue(userName + "_" + className);
        mDatabase.child(Id).child(userName + "_" + className).child("ClassName").setValue(className);
        mDatabase.child(Id).child(userName + "_" + className).child("classInfo").setValue(classInfo);

        mDatabase.child(Id).child(userName + "_" + className).child("absent").setValue(0);
        mDatabase.child(Id).child(userName + "_" + className).child("Present").setValue(0);
        mDatabase.child(Id).child(userName + "_" + className).child("NoLession").setValue(noLesson);
        mDatabase.child(Id).child(userName + "_" + className).child("numOfDayBanned").setValue(numOfDayBanned);


//        mDatabase = FirebaseDatabase.getInstance().getReference("Class");
//        mDatabase.child(userName).child(userName + "_" + className).addListenerForSingleValueEvent (new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String noLession = snapshot.child("numOfLesson").getValue(String.class);
//                insertResultLearningStudent_1(userName, Id, fullName, noLession);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    private void getStudentNameDB(String id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    String idDB = child.child("IdStudent").getValue(String.class);
                    String fullNameDb = child.child("fullName").getValue(String.class);

                    if (id.equals(idDB)) {
                        d++;
                        insertStudentClassStudent(idDB, fullNameDb);
                    }

                }
                if (d == 0)
                    Toast.makeText(StudentActivity.this, "Student ID is not create", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(StudentActivity.this, "Add student successful", Toast.LENGTH_SHORT).show();
                d = 0;
                //     studentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteAttendanceStudentDetail(String userName, String clName, String id, String dateDb) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");

        mDatabase.child(userName).child(userName + "_" + clName).child(dateDb).child(id).removeValue();

        //    studentAdapter.notifyItemRemoved(position);

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(id).child(userName + "_" + clName).removeValue();


    }

    private void deleteStudent(int position) {

        String id_position = studentItems.get(position).getStudentId();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        deleteDataOnResultLearning(id_position, userName + "_" + className);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ClassStudent");
        reference.child(userName).child(userName + "_" + className).child(id_position).removeValue();

        mDatabase = FirebaseDatabase.getInstance().getReference("Attendance");
        mDatabase.child(userName).child(userName + "_" + className).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    deleteAttendanceStudentDetail(userName, className, id_position, child.getKey());
                    //  Log.d(TAG, "onDataChange: " + child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        studentItems.remove(position);
        studentAdapter.notifyItemRemoved(position);

        //   loadDataDB();

    }

    private void deleteDataOnResultLearning(String id, String idClass) {

        mDatabase = FirebaseDatabase.getInstance().getReference("ResultLearning");
        mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.d("TAG", "onDataChange: " + child.getKey());
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

}