package com.example.teachingassistant;

public class ClassItem {


    private String className;
    private String classInfo;
    private String numberLesson;

    public ClassItem(String className, String classInfo, String numberLesson) {
        this.className = className;
        this.classInfo = classInfo;
        this.numberLesson = numberLesson;
    }

    public ClassItem() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public String getNumberLesson() {
        return numberLesson;
    }

    public void setNumberLesson(String numberLesson) {
        this.numberLesson = numberLesson;
    }
}
