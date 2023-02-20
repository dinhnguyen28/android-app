package com.example.teachingassistant;

public class ClassDetail {

    public String classInfo;
    public String className;
    public String classSession;
    public String userName;

    public ClassDetail() {}

    public ClassDetail(String classInfo, String className, String classLesson, String userName){

        this.classInfo=classInfo;
        this.className=className;
        this.classSession= classLesson;
        this.userName = userName;

    }

    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassSession() {
        return classSession;
    }

    public void setClassSession(String classSession) {
        this.classSession = classSession;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
