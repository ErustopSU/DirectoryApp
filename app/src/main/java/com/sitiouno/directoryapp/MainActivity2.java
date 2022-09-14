package com.sitiouno.directoryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.sitiouno.directoryapp.Database.AdminSQLiteOpenHelper;
import com.sitiouno.directoryapp.Database.RetrofitClient;
import com.sitiouno.directoryapp.Helper.UtilsNetwork;
import com.sitiouno.directoryapp.Database.Models.User;
import com.sitiouno.directoryapp.Database.Interfaces.UsersInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity2 extends AppCompatActivity {

    private TextView tv1;
    private TextInputLayout et1, et2, et3;
    private Button boton1, boton2;
    private ImageView vistaimagen;

    private String id, fullname, email, code, method;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Conexion de la parte logica con la grafica
        tv1 = findViewById(R.id.txtv1);
        et1 = findViewById(R.id.cajaet1);
        et2 = findViewById(R.id.cajaet2);
        et3 = findViewById(R.id.cajaet3);
        boton2 = findViewById(R.id.cancelar);
        boton1 = findViewById(R.id.boton1);

        vistaimagen = findViewById(R.id.imageUserGallery2);

        // Load data to intent
        id = getIntent().getStringExtra("id");
        fullname = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        code = getIntent().getStringExtra("code");
        method = getIntent().getStringExtra("method");
        position = getIntent().getIntExtra("position", 0);

        //Set data
        et1.getEditText().setText(fullname);
        et2.getEditText().setText(email);
        et3.getEditText().setText(code);

        //Set title and text button
        tv1.setText(method);
        boton1.setText(method);

        switch (method) {
            case "OBTENER":
                boton1.setVisibility(View.GONE);

                tv1.setText("VISTA DE LA TARJETA");

                vistaimagen.findViewById(R.id.imageUserGallery2);

                boton2.setText("REGRESAR");

                et1.setEnabled(false);
                et2.setEnabled(false);
                et3.setEnabled(false);

                vistaimagen.setVisibility(View.VISIBLE);
                vistaimagen.setImageBitmap(getImageBitmap(MainActivity.catitos.get(position).getUrl()));

                break;
            case "ACTUALIZAR":

                vistaimagen.setVisibility(View.GONE);

                boton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        final String compruebaname = et1.getEditText().getText().toString();
                        final String compruebaemail = et2.getEditText().getText().toString().trim();
                        final String regex = "(?:[^<>()\\[\\].,;:\\s@\"]+(?:\\.[^<>()\\[\\].,;:\\s@" +
                                "\"]+)*|\"[^\\n\"]+\")@(?:[^<>()\\[\\].,;:\\s@\"]+\\.)+[^<>()\\[\\]" +
                                "\\.,;:\\s@\"]{2,63}";
                        final String solotexto = "[a-zA-Z ]+";

                        if (
                                et1.getEditText().getText().toString().isEmpty() ||
                                et2.getEditText().getText().toString().isEmpty() ||
                                et3.getEditText().getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity2.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                        } else if
                        ((!compruebaname.matches(solotexto))) {
                            Toast.makeText(MainActivity2.this, "Asegurate de ingresar solo texto y sin acentos en el campo de nombre", Toast.LENGTH_LONG).show();
                        } else if (et3.getEditText()
                                .getText().length() > 5) {
                            Toast.makeText(MainActivity2.this, "El codigo es mayor a 5 digitos", Toast.LENGTH_SHORT).show();
                        } else if ((!compruebaemail.matches
                                (regex))) {
                            Toast.makeText(MainActivity2.this, "Por favor, introduce un correo valido", Toast.LENGTH_LONG).show();
                        } else if (UtilsNetwork.isOnline(MainActivity2.this)) {

                            String fullname = et1.getEditText().getText().toString();
                            String email = et2.getEditText().getText().toString();
                            int code = Integer.parseInt(et3.getEditText().getText().toString());

                            updateUser(fullname, email, code, id);
                        } else {
                            Toast.makeText(MainActivity2.this, "Necesitas internet para editar una tarjeta.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                break;
            case "CREATE":

                tv1.setText("CREA UNA TARJETA");
                boton1.setText("CREAR");
                vistaimagen.setVisibility(View.GONE);

                boton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String compruebaname = et1.getEditText().getText().toString();
                        final String compruebaemail = et2.getEditText().getText().toString().trim();
                        final String regex = "(?:[^<>()\\[\\].,;:\\s@\"]+(?:\\.[^<>()\\[\\].,;:\\s@" +
                                "\"]+)*|\"[^\\n\"]+\")@(?:[^<>()\\[\\].,;:\\s@\"]+\\.)+[^<>()\\[\\]" +
                                "\\.,;:\\s@\"]{2,63}";
                        final String solotexto = "[a-zA-Z ]+";

                        if (et1.getEditText().getText().toString().isEmpty() ||
                                et2.getEditText().getText().toString().isEmpty() ||
                                et3.getEditText().getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity2.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                        } else if
                        ((!compruebaname.matches(solotexto))) {
                            Toast.makeText(MainActivity2.this, "Asegurate de ingresar solo texto y sin acentos en el campo de nombre", Toast.LENGTH_LONG).show();
                        } else if (et3.getEditText()
                                .getText().length() > 5) {
                            Toast.makeText(MainActivity2.this, "El codigo es mayor a 5 digitos", Toast.LENGTH_SHORT).show();
                        } else if ((!compruebaemail.matches
                                (regex))) {
                            Toast.makeText(MainActivity2.this, "Por favor, introduce un correo valido", Toast.LENGTH_LONG).show();
                        } else {
                            createUser(retrieveUser());

                        }
                    }
                });
                break;
        }


        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //Un bitmap es un formato de imagen que permite adaptar la imagen al requerimento que tu app tiene
    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        Bitmap resizedBitmap = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            bm = BitmapFactory.decodeStream(bis);
            resizedBitmap = bm.createScaledBitmap(bm, 250, 270, false);

            bis.close();
            is.close();

            return resizedBitmap;
        } catch (IOException e) {
            Log.e("Image error", "Error getting bitmap", e);
            return null;
        }
    }

    public User retrieveUser() {
        User user = new User();

        user.setFullname(et1.getEditText().getText().toString());
        user.setEmail(et2.getEditText().getText().toString());
        user.setCode(Integer.parseInt(et3.getEditText().getText().toString()));

        return user;

    }

    //Crear usuarios Retrofit
    private void createUser(User user) {

        //Hacemos la conexion con
        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        Call<User> call = retrofit.create(UsersInterface.class).createUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 500:
                            Toast.makeText(MainActivity2.this, "500", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            try {
                                String result = response.errorBody().string();

                                JSONObject jsonObject = new JSONObject(result);
                                String message = jsonObject.getString("message");

                                switch (message) {
                                    case "Email already exist!":
                                        Toast.makeText(MainActivity2.this, "Este correo ya existe!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "Code already exist!":
                                        Toast.makeText(MainActivity2.this, "Este código ya existe!", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(MainActivity2.this, "Error en alguno de los campos!", Toast.LENGTH_SHORT).show();
                                        break;
                                }

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            Toast.makeText(MainActivity2.this, "Error" + ": " + response.code(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {

                    User user1 = response.body();

                    String _id = user1.getId();
                    String fullname = et1.getEditText().getText().toString();
                    String email = et2.getEditText().getText().toString();
                    String code = et3.getEditText().getText().toString();

                    //Toast.makeText(MainActivity2.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(MainActivity2.this);

                    adminSQLiteOpenHelper.registerUser(_id, fullname, email, code);

                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error: " + t.getMessage());
            }
        });

    }

    //Update user by id Retrofit
    private void updateUser(String fullname, String email, int code, String id) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        Call<User> updateUser = retrofit.create(UsersInterface.class).updateUser(id, fullname, email, code);

        updateUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (!response.isSuccessful()) {

                    switch (response.code()) {
                        case 500:
                            Toast.makeText(MainActivity2.this, "500", Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(MainActivity2.this, "404", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            try {
                                String result = response.errorBody().string();

                                JSONObject jsonObject = new JSONObject(result);
                                String message = jsonObject.getString("message");

                                switch (message) {
                                    case "Email already exist!":
                                        Toast.makeText(MainActivity2.this, "Este correo ya existe!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "Code already exist!":
                                        Toast.makeText(MainActivity2.this, "Este código ya existe!", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(MainActivity2.this, "Error en alguno de los campos!", Toast.LENGTH_SHORT).show();
                                        break;
                                }

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            Toast.makeText(MainActivity2.this, "Error" + ": " + response.code(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {

                    User user1 = response.body();

                    String _id = user1.getId();
                    String fullname = et1.getEditText().getText().toString();
                    String email = et2.getEditText().getText().toString();
                    String code = et3.getEditText().getText().toString();

                    AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(MainActivity2.this);

                    adminSQLiteOpenHelper.updateUser(_id, fullname, email, code);

                    Toast.makeText(MainActivity2.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();

                    finish();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }


}
