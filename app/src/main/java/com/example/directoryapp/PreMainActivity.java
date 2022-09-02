package com.example.directoryapp;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.directoryapp.MainActivity.populateUsers;

public class PreMainActivity extends AppCompatActivity {

    public static List<User> usersSQLite = new ArrayList<>();
    public static List<User> usersRetrofit = new ArrayList();
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);

        context = this;

        botonIngresar = findViewById(R.id.botonIngresar);

        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsNetwork.isOnline(PreMainActivity.this)) {
                    Intent intent = new Intent(PreMainActivity.this, MainActivity.class);

                    startActivity(intent);

                } else {

                    Toast.makeText(PreMainActivity.this, "Necesitas internet para crear una tarjeta.", Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    //metodo getUsers con SQLite
    public static void getUsersSQLite() {
        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(context);

        Cursor cursor = adminSQLiteOpenHelper.consultUser();
        User user;

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                do {
                    user = new User();

                    user.setId(String.valueOf(cursor.getInt(0)));
                    user.setFullname(cursor.getString(1));
                    user.setEmail(cursor.getString(2));
                    user.setCode(Integer.parseInt(cursor.getString(3)));

                    boolean isUserExist = false;

                    for (int i = 0; i < usersSQLite.size(); i++) {
                        if (usersSQLite.get(i).getId().equals(user.getId())) isUserExist = true;
                    }

                    if (!isUserExist) usersSQLite.add(user);


                } while (cursor.moveToNext());
            }

            populateUsers(usersSQLite);
            cursor.close();
        }
    }

    //metodo getUsers con Retrofit
    public static void getUsers() {
        //Crear conexion al API
        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        //Creando la llamada al endpoint del api correspondiente
        Call<List<User>> call = retrofit.create(UsersInterface.class).listaUsers();

        //Ejecutamos la llamada
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                //Si la respuesta NO es satisfactoria
                if (!response.isSuccessful()) {

                    //Obtener el codigo de respuesta de la peticion para poder controlar las validaciones
                    switch (response.code()) {
                        case 500:
                            System.out.println("500");
                            break;
                        case 404:
                            System.out.println("404");
                            break;
                        default:
                            System.out.println("Error: " + response.code());
                    }

                    //Si la respuesta es satisfactoria
                } else {
                    usersRetrofit = response.body();

                    populateUsers(usersRetrofit);
                }
            }

            //En el caso de que la peticion falle
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }




}