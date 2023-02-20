package com.example.teachingassistant;

public class StudentDetailBanned {

    private String studentIdBanned;
    private String studentNameBanned;

    public StudentDetailBanned(String studentIdBanned, String studentNameBanned) {
        this.studentIdBanned = studentIdBanned;
        this.studentNameBanned = studentNameBanned;
    }

    public String getStudentIdBanned() {
        return studentIdBanned;
    }

    public String getStudentNameBanned() {
        return studentNameBanned;
    }

    public StudentDetailBanned() {
    }


}
