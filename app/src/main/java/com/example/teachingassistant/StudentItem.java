package com.example.teachingassistant;

public class StudentItem {

    private String studentId;
    private String studentName;
    private String status;

    public StudentItem(String studentId, String studentName,String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
    }

    public StudentItem() {
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
