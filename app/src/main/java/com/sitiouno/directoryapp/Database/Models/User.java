package com.sitiouno.directoryapp.Database.Models;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName; //La anotación @SerializedName indica que el miembro anotado debe serializarse en JSON
// con el valor de nombre proporcionado en el atributo de anotación. Esta anotación anulará cualquier correo electrónico FieldNamingPolicy,
//incluida la política de nomenclatura de campos predeterminada, que pueda haber estado usando la GsonBuilderclase.

public class User {
    @SerializedName("fullname")
    private String fullname;

    @SerializedName("email")
    public String email;

    @SerializedName("code")
    private int code;

    @SerializedName("_id")
    private String id;

    //Metodos Getter
    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public int getCode() {
        return code;
    }

    public String getId() {
        return id;
    }


    //Metodos Setter

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setId(String id) {
        this.id = id;
    }


}
