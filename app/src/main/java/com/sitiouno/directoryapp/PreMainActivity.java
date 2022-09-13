package com.sitiouno.directoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sitiouno.directoryapp.Database.AdminSQLiteOpenHelper;
import com.sitiouno.directoryapp.Database.RetrofitClient;
import com.sitiouno.directoryapp.Database.Models.ImageCats;
import com.sitiouno.directoryapp.Database.Models.User;
import com.sitiouno.directoryapp.Database.Interfaces.UsersInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PreMainActivity extends AppCompatActivity {

    public static List<User> usersSQLite = new ArrayList<>();
    public static List<User> usersRetrofit = new ArrayList();
    public static List<ImageCats> catitos = new ArrayList<>();

    private static Context context;
    private Button botonIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);

        context = this;

        botonIngresar = findViewById(R.id.botonIngresar);

        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Load data
        getUsersSQLite();
        getUsers();
        getCatitos();
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

            System.out.println("Users SQLite size: " + usersSQLite.size());
            //populateUsers(usersSQLite);
            cursor.close();
        } else {
            System.out.println("Users SQLite size: 0");
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


                    System.out.println("Users retrofit size: " + usersRetrofit.size());

                    //populateUsers(usersRetrofit);
                }
            }

            //En el caso de que la peticion falle
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    //Method get list cats
    public static void getCatitos() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient2();

        Call<List<ImageCats>> call = retrofit.create(UsersInterface.class).getImageCats();

        call.enqueue(new Callback<List<ImageCats>>() {
            @Override
            public void onResponse(Call<List<ImageCats>> call, Response<List<ImageCats>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, (CharSequence) response.errorBody(), Toast.LENGTH_SHORT).show();
                } else {
                    catitos = response.body();

                    System.out.println("Size cats: " + catitos.size());
                }
            }

            @Override
            public void onFailure(Call<List<ImageCats>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error: " + t.getMessage());
            }
        });
    }
}