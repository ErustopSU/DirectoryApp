package com.example.directoryapp;

public class Datos {
    private String id, fullname, email, code;

    //Constructor
    public Datos(String id, String fullname, String email, String code) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.code = code;
    }

    //Metodos Getter y Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
