package com.sitiouno.directoryapp.Helper;

import android.widget.ImageView;

public class Datos {

    private String id;
    private String fullname;
    private String email;
    private String code;
    private String url;

    //Constructor
    public Datos(String id, String fullname, String email, String code, String url) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.code = code;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
