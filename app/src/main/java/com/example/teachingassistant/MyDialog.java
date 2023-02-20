package com.example.teachingassistant;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {

    private void setNameStudentDialog(String fullName) {

    }

    public static final String ADD_STUDENT_DIALOG = "addStudent";
    // public static final String EDIT_STUDENT_DIALOG = "editStudent";


    private OnClickListener listener;


    public interface OnClickListener {
        void onClick(String txt1, String txt2);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override

    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        Dialog dialog = null;

        if (getTag().equals(ADD_STUDENT_DIALOG)) dialog = addStudentDialog();
        //if(getTag().equals(EDIT_STUDENT_DIALOG))dialog = editStudentDialog();

        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }


    private Dialog addStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.nameDialog);
        title.setText("Add New Student");

        EditText studentId = view.findViewById(R.id.edtDialog1);
        EditText studentName = view.findViewById(R.id.edtDialog2);

        studentName.setVisibility(view.GONE);

        studentId.setHint("Student ID");
        studentName.setHint("Student Name");

        Button btnCancelDialog = view.findViewById(R.id.btnCancelDialog);
        Button btnAddDialog = view.findViewById(R.id.btnAddDialog);

        btnCancelDialog.setOnClickListener(v -> dismiss());

        btnAddDialog.setOnClickListener(v -> {
            String id = studentId.getText().toString();
            String name = studentName.getText().toString();

            studentId.setText("");
            studentName.setText("");

            listener.onClick(id, name);

        });

        return builder.create();

    }

}
