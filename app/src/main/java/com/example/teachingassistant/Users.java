package com.example.teachingassistant;

public class Users {
    public String fullname;
    public String email;
    public String key;
    public String password;
    public String username;

    public Users() {
    }

    public Users(String fullname, String email, String key, String password, String username) {
        this.fullname = fullname;
        this.email = email;
        this.key = key;
        this.password = password;
        this.username = username;
    }

    public String getName() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }
}

