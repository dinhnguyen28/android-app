package com.example.teachingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button btnReg;
    Button btnLogin;
    private EditText txtUsername, txtPassword;
    private TextView showNotice;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnReg = findViewById(R.id.btnReg);
        Intent intent = new Intent(this, Register.class);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        showNotice = findViewById(R.id.showNotice);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected() == true) {


                    if (txtUsername.getText().toString().equals("") && txtPassword.getText().toString().equals("")) {
                        showNotice.setText("Please enter your username and password");
                        //Toast.makeText(Login.this, "Please enter your username!", Toast.LENGTH_SHORT).show();
                    } else if (txtUsername.getText().toString().equals("")) {
                        showNotice.setText("Please enter your Username");
                        //Toast.makeText(Login.this, "Please enter your username!", Toast.LENGTH_SHORT).show();
                    } else if (txtPassword.getText().toString().equals("")) {
                        showNotice.setText("Please enter your Password!");
                        //Toast.makeText(Login.this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
                    } else {
                        //showNotice.setText("");
                        checkUserName(txtUsername.getText().toString(), txtPassword.getText().toString());
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your network connection, no internet Connectivity", Toast.LENGTH_LONG).show();
                }


            }
        });
    }


    private void checkUserName(String User, String Pwd) {
        User.toLowerCase();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (User.equals(child.getKey().toString())) {
                        d++;
                    }
                }
                if (d == 0)
                    showNotice.setText("Wrong username or password, please try again");
                else
                    checkPwd(User, Pwd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void checkPwd(String User, String Pwd) {

        Intent Teacher_login = new Intent(this, MainActivity.class);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");
        Query checkUser = reference.orderByChild("userName").equalTo(txtUsername.getText().toString());

        checkUser.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String passwordDB = snapshot.child(txtUsername.getText().toString()).child("Password").getValue(String.class);
                String Key = snapshot.child(txtUsername.getText().toString()).child("Key").getValue(String.class);

                String fullName = snapshot.child(txtUsername.getText().toString()).child("fullName").getValue(String.class);

                if (passwordDB.equals(txtPassword.getText().toString())) {

                    Toast.makeText(Login.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    if (Key.equals("1")) {
                        Teacher_login.putExtra("username", txtUsername.getText().toString());
                        Teacher_login.putExtra("fullName", fullName);
                        startActivity(Teacher_login);
                        finish();
                    } else {

                        getIdStudent(txtUsername.getText().toString());

                    }
                } else {
                    showNotice.setText("Wrong username or password, please try again");
                    //Toast.makeText(Login.this, "Wrong Password, please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getIdStudent(String userName) {
        Intent Student_login = new Intent(this, StudentHome.class);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account").child(userName);
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String userName = snapshot.child("userName").getValue(String.class);
                String Id = snapshot.child("IdStudent").getValue(String.class);
                String fullName = snapshot.child("fullName").getValue(String.class);

                Student_login.putExtra("userName", userName);
                Student_login.putExtra("fullName", fullName);
                Student_login.putExtra("IdStudent", Id);
                startActivity(Student_login);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {
            return false;
        }
        return true;
    }
}