package com.example.teachingassistant;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    ArrayList<StudentItem> studentItems;
    Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public StudentAdapter (Context context, ArrayList<StudentItem> studentItems) {
        this.studentItems = studentItems;
        this.context = context;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnCreateContextMenuListener {


        TextView studentId;
        TextView studentName;
        TextView status;
        CardView cardView;

        public StudentViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {

            super(itemView);
            studentId = itemView.findViewById(R.id.studentId);
            studentName = itemView.findViewById(R.id.studentName);
            status = itemView.findViewById(R.id.statusStudent);
            cardView = itemView.findViewById(R.id.cardViewStudent);

            itemView.setOnClickListener(v-> onItemClickListener.onClick(getAdapterPosition()) );
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                                    ContextMenu.ContextMenuInfo contextMenuInfo) {
           // contextMenu.add(getAdapterPosition(),0,0,"Edit");
            contextMenu.add(getAdapterPosition(),0,0,"Delete");
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item,
                                                                    parent, false);

        return new StudentViewHolder(itemView, onItemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.studentId.setText(studentItems.get(position).getStudentId());
        holder.studentName.setText(studentItems.get(position).getStudentName());
        holder.status.setText(studentItems.get(position).getStatus());

        holder.cardView.setCardBackgroundColor(getColor(position));

    }

    private int getColor(int position) {
        String status = studentItems.get(position).getStatus();

        if (status.equals("P"))
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(
                                    context, R.color.colorPresent)));

        else if (status.equals("A"))
            return  Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(
                                                    context, R.color.colorAbsent)));

        return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(
                                context, R.color.colorNormal)));
    }

    @Override
    public int getItemCount() {
        return studentItems.size();
    }
}
