package com.example.teachingassistant;

public class StudentDetail {
    public String fullname;
    public String email;
    public String key;
    public String password;
    public String username;
    public String id;

    public StudentDetail() {
    }

    public StudentDetail( String email,String fullname, String id,String key, String password, String username ) {
        this.fullname = fullname;
        this.email = email;
        this.key = key;
        this.password = password;
        this.username = username;
        this.id=id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getKey() {
        return key;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
