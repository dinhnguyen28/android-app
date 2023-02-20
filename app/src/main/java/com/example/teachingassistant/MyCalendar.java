package com.example.teachingassistant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyCalendar extends DialogFragment {
    Calendar calendar = Calendar.getInstance();

    public interface OnCalendarOnClickListener {
        void onClick(int year, int month, int day);
    }
    public OnCalendarOnClickListener onCalendarOnClickListener;


    public void setOnCalendarOnClickListener(OnCalendarOnClickListener onCalendarOnClickListener) {
        this.onCalendarOnClickListener = onCalendarOnClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(),((view, year, month, dayOfMonth) -> {
            onCalendarOnClickListener.onClick(year, month, dayOfMonth);
        }),calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH));
    }

    void setDate(int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

    }

    String getDate() {
        return DateFormat.format("dd-MM-yyyy", calendar).toString();
    }
}
