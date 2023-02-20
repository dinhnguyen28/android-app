package com.example.teachingassistant;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyClassDialog extends DialogFragment {
    public static final String ADD_CLASS_DIALOG ="addClass";
    public static final String EDIT_CLASS_DIALOG = "editClass";

    private MyClassDialog.OnClickListener listener;


    public interface OnClickListener{
        void onClick(String txt1, String txt2, String txt3);
    }
    public void setListener(MyClassDialog.OnClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null;
        if(getTag().equals(ADD_CLASS_DIALOG))dialog = addClassDialog();
        if (getTag().equals(EDIT_CLASS_DIALOG)) dialog = editCLassDialog();
        dialog.getWindow().setBackgroundDrawable(new
                                                ColorDrawable(android.graphics.Color.TRANSPARENT));


        return dialog;
    }

    private Dialog addClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_class, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.nameClassDialog);
        title.setText("Add New Class");



        EditText classNameDialog = view.findViewById(R.id.edtClassDialog1);
        EditText classInfoDialog = view.findViewById(R.id.edtClassDialog2);
        EditText numbOfSession = view.findViewById(R.id.edtClassDialog3);

        classNameDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        numbOfSession.setInputType(InputType.TYPE_CLASS_NUMBER);



        classNameDialog.setHint("Class name");
        classInfoDialog.setHint("Class info");
        numbOfSession.setHint("Number of lesson");


        //numbsOfSession.setVisibility(View.GONE);

        Button btnCancelDialog =  view.findViewById(R.id.btnCancelClassDialog);
        Button btnAddDialog =  view.findViewById(R.id.btnAddClassDialog);

        btnCancelDialog.setOnClickListener(v ->dismiss());
        btnAddDialog.setOnClickListener(v -> {
            String className = classNameDialog.getText().toString();
            String classInfo = classInfoDialog.getText().toString();
            String numbsSession = numbOfSession.getText().toString();


            listener.onClick(className, classInfo, numbsSession);
            dismiss();


        });

        return builder.create();

    }
    private Dialog editCLassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_class, null);
        builder.setView(view);
        TextView title = view.findViewById(R.id.nameClassDialog);
        title.setText("Edit Class");

        EditText classNameDialog = view.findViewById(R.id.edtClassDialog1);
        EditText classInfoDialog = view.findViewById(R.id.edtClassDialog2);
        EditText numbOfSession = view.findViewById(R.id.edtClassDialog3);

        classNameDialog.setVisibility(View.GONE);

        classNameDialog.setHint("Class Name");
        classInfoDialog.setHint("Class Info");
        numbOfSession.setHint("No. Lesson");


        Button btnCancelDialog = view.findViewById(R.id.btnCancelClassDialog);
        Button btnAddClassDialog = view.findViewById(R.id.btnAddClassDialog);

        btnAddClassDialog.setText("UPDATE");


        btnCancelDialog.setOnClickListener(v -> dismiss());
        btnAddClassDialog.setOnClickListener(v -> {
           // String className = classNameDialog.getText().toString();
            String classInfo = classInfoDialog.getText().toString();
            String numbsSession = numbOfSession.getText().toString();
            listener.onClick(null, classInfo, numbsSession);
            dismiss();


        });

        return builder.create();
    }

}
