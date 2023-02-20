package com.example.teachingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    Button btnReg;
    private EditText txtName, txtEmail, txtUsername, txtPassword, txtConfirmPwd, txtIdStudent;
    RadioButton radioStudent, radioTeacher;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtConfirmPwd = findViewById(R.id.txtConfirmPwd);
        txtIdStudent = findViewById(R.id.txtIdStudent);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnReg = findViewById(R.id.btnReg);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        radioStudent = findViewById(R.id.radioStudent);
        radioTeacher = findViewById(R.id.radioTeacher);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().equals("")) {
                    Toast.makeText(Register.this, "Please enter your full name!", Toast.LENGTH_SHORT).show();

                }
                else if (txtName.getText().toString().length() < 6) {
                    Toast.makeText(Register.this, "Your full name must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (txtEmail.getText().toString().equals("")) {
                    Toast.makeText(Register.this, "Please enter your Email!", Toast.LENGTH_SHORT).show();
                }
                else if (txtEmail.getText().toString().length() < 6) {
                    Toast.makeText(Register.this, "Your Email must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (txtUsername.getText().toString().equals("")) {
                    Toast.makeText(Register.this, "Please enter your Username!", Toast.LENGTH_SHORT).show();
                }
                else if (txtUsername.getText().toString().length() < 6) {
                    Toast.makeText(Register.this, "Your Username must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (txtPassword.getText().toString().equals("")) {
                    Toast.makeText(Register.this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
                }
                else if (txtPassword.getText().toString().length() < 6) {
                    Toast.makeText(Register.this, "Your Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (txtConfirmPwd.getText().toString().equals("")) {
                    Toast.makeText(Register.this, "Please confirm Password!", Toast.LENGTH_SHORT).show();
                }
                else if (txtConfirmPwd.getText().toString().length() < 6) {
                    Toast.makeText(Register.this, "Your Confirm Password at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (!txtConfirmPwd.getText().toString().equals(txtPassword.getText().toString())) {
                    Toast.makeText(Register.this, "Your Password must equals Confirm Password", Toast.LENGTH_SHORT).show();
                }
                else if (txtIdStudent.getText().toString().equals("") && radioStudent.isChecked()) {
                    Toast.makeText(Register.this, "Please enter your Student ID!", Toast.LENGTH_SHORT).show();
                }
                else if (txtIdStudent.getText().toString().length() != 8 && radioStudent.isChecked()) {
                    Toast.makeText(Register.this, "Your Student ID must be 8 character", Toast.LENGTH_SHORT).show();
                }
                else if (radioStudent.isChecked()) {
                    checkExistStudent(txtUsername.getText().toString(),txtName.getText().toString(),txtEmail.getText().toString(),txtPassword.getText().toString(),txtIdStudent.getText().toString());
                }
                else {
                    checkExistTeacher(txtUsername.getText().toString(),txtName.getText().toString(),txtEmail.getText().toString(),txtPassword.getText().toString(),txtIdStudent.getText().toString());
                }

            }
        });

    }

    private void checkExistStudent(String userName, String fullName, String Email, String Password, String IdStudent) {
        String finalUserName = userName.toLowerCase();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");

        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int k = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().toString().equals(finalUserName)) {
                        k++;
                        Toast.makeText(Register.this, "", Toast.LENGTH_SHORT).show();
                    }

                }
                if (k==0){


                    mDatabase = FirebaseDatabase.getInstance().getReference("Account");

                    mDatabase.child(finalUserName).child("userName").setValue(finalUserName);
                    mDatabase.child(finalUserName).child("fullName").setValue(fullName);
                    mDatabase.child(finalUserName).child("Email").setValue(Email);
                    mDatabase.child(finalUserName).child("userName").setValue(finalUserName);
                    mDatabase.child(finalUserName).child("Password").setValue(Password);
                    mDatabase.child(finalUserName).child("IdStudent").setValue(IdStudent);
                    mDatabase.child(finalUserName).child("Key").setValue("0");

                    Toast.makeText(Register.this, "Register successful !", Toast.LENGTH_SHORT).show();

                    txtIdStudent.setText("");
                    txtUsername.setText("");
                    txtPassword.setText("");
                    txtConfirmPwd.setText("");
                    txtName.setText("");
                    txtEmail.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkExistTeacher(String userName, String fullName, String Email, String Password, String IdStudent) {
        String finalUserName = userName.toLowerCase();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");

        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int k =0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().toString().equals(finalUserName)) {
                        k++;
                        Toast.makeText(Register.this, "UserName is exist, Please Try Again!", Toast.LENGTH_SHORT).show();
                    }

                }
                if (k==0){

                    mDatabase = FirebaseDatabase.getInstance().getReference("Account");

                    mDatabase.child(finalUserName).child("userName").setValue(finalUserName);
                    mDatabase.child(finalUserName).child("fullName").setValue(fullName);
                    mDatabase.child(finalUserName).child("Email").setValue(Email);
                    mDatabase.child(finalUserName).child("userName").setValue(finalUserName);
                    mDatabase.child(finalUserName).child("Password").setValue(Password);
                    mDatabase.child(finalUserName).child("IdStudent").setValue(IdStudent);
                    mDatabase.child(finalUserName).child("Key").setValue("1");

                    Toast.makeText(Register.this, "Register successful!", Toast.LENGTH_SHORT).show();

                    txtIdStudent.setText("");
                    txtUsername.setText("");
                    txtPassword.setText("");
                    txtConfirmPwd.setText("");
                    txtName.setText("");
                    txtEmail.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}