package com.example.teachingassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ExportFile extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private File filePath = null;

    private ArrayList<StudentDetailBanned> listStudent = new ArrayList<>();

    private int Count = 0;
    private String listBanned = "";
    Toolbar toolbar;
    Button exportFileDocx, exportFilePdf, exportFileXls;

    TextView txtExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_file);

        setToolbar();


        exportFileDocx = (Button) findViewById(R.id.exportFileDocx);
        exportFilePdf = (Button) findViewById(R.id.exportFilePdf);
        exportFileXls = (Button) findViewById(R.id.exportFileXls);
        txtExport = (TextView) findViewById(R.id.txtExport);

        //permission
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);


        Intent intent = getIntent();
        String idClass = intent.getStringExtra("idClass");
        String className = intent.getStringExtra("className");


        txtExport.setText(className + "'s class");

        //filename
        String FILE_NAME_DOCX = className + " class banned students.docx";
        String FILE_NAME_PDF = className + " class banned students.pdf";
        String FILE_NAME_XLS = className + " class banned students.xls";


        mDatabase = FirebaseDatabase.getInstance().getReference("Banned");

        mDatabase.child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int d = 0;
                listBanned += "Class Name: " + className + "\n";
                for (DataSnapshot child : snapshot.getChildren()) {
                    d++;
                    String idStudent = child.child("idStudent").getValue(String.class);
                    String fullName = child.child("fullName").getValue(String.class);
                    String content = "Ordinal number: " + d + ", Student ID: " + idStudent + ", Full name: " + fullName + "\n";
                    listBanned = listBanned + content;

                    StudentDetailBanned studentItem = new StudentDetailBanned(idStudent, fullName);
                    listStudent.add(studentItem);
                    Count++;

                }
                if (d == 0) {
                    listBanned = "This " + className + " class don't have banned student";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        exportFileDocx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportFileDocx(FILE_NAME_DOCX, listBanned);
            }
        });
        exportFilePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportFilePdf(FILE_NAME_PDF, listBanned);
            }
        });

        exportFileXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportFileXls(FILE_NAME_XLS, className);
            }
        });

    }


    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        TextView title = toolbar.findViewById(R.id.titleToolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitleToolbar);

        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        back.setOnClickListener(v -> onBackPressed());

        subtitle.setVisibility(View.GONE);
        title.setVisibility(View.GONE);

        save.setVisibility(View.INVISIBLE);
    }

    private void ExportFilePdf(String FILE_NAME, String dataStudent) {

        String filePath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOCUMENTS + "/" + FILE_NAME;
        Document mPdf = new Document();

        try {
            PdfWriter.getInstance(mPdf, new FileOutputStream(filePath));
            mPdf.open();
            mPdf.add(new Paragraph(dataStudent));
            mPdf.close();


            Toast.makeText(ExportFile.this, "File save to: " + filePath
                    , Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    private void ExportFileDocx(String FILE_NAME, String dataStudent) {


//        File root = Environment.getExternalStorageDirectory();
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        filePath = new File(root, FILE_NAME);

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            XWPFRun xwpfRun = xwpfParagraph.createRun();


            if (dataStudent.contains("\n")) {
                String[] row = dataStudent.split("\n");

                xwpfRun.setText(row[0], 0); // set first line into XWPFRun

                for (int i = 1; i < row.length; i++) {
                    // add break and insert new text
                    xwpfRun.addBreak();
                    xwpfRun.setText(row[i]);
                    xwpfRun.setFontSize(13);
                }

            } else {
                xwpfRun.setText(dataStudent, 0);
                xwpfRun.setFontSize(13);
            }


            //xwpfRun.setText(dataStudent);


            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            xwpfDocument.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            xwpfDocument.close();
            Toast.makeText(ExportFile.this, "File save to: " + filePath
                    , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ExportFileXls(String FILE_NAME, String className) {

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        filePath = new File(root, FILE_NAME);

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(className);

        HSSFRow hssfRow = hssfSheet.createRow(0);

        int rownum = 0;
        Cell cell;
        Row row;

        row = hssfSheet.createRow(rownum);


        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Ordinal number");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Student ID");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Full Name");

        rownum++;
        row = hssfSheet.createRow(rownum);

        for (int i = 0; i < Count; i++) {

            String id = listStudent.get(i).getStudentIdBanned();
            String fullName = listStudent.get(i).getStudentNameBanned();

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i + 1);
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(id);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(fullName);

            rownum++;
            row = hssfSheet.createRow(rownum);

        }

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);
            Toast.makeText(ExportFile.this, "File save to: " + filePath
                    , Toast.LENGTH_SHORT).show();
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}