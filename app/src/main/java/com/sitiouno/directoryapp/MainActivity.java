package com.sitiouno.directoryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sitiouno.directoryapp.Database.AdminSQLiteOpenHelper;
import com.sitiouno.directoryapp.Database.Interfaces.UsersInterface;
import com.sitiouno.directoryapp.Database.RetrofitClient;
import com.sitiouno.directoryapp.Helper.Datos;
import com.sitiouno.directoryapp.Helper.Adapter;
import com.sitiouno.directoryapp.Helper.UtilsNetwork;
import com.sitiouno.directoryapp.Database.Models.ImageCats;
import com.sitiouno.directoryapp.Database.Models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //Data
    private static List<User> usersSQLite = new ArrayList<>();
    private static List<User> usersRetrofit = new ArrayList();
    public static List<ImageCats> catitos = new ArrayList<>();

    //Context
    private static Context context;

    //Search
    private static SearchView txtBuscar;

    //
    private static FloatingActionButton floatingActionButton;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static ProgressBar progressBar;

    //Recycler view and adapter
    private static RecyclerView recyclerView;
    private static Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Conexion de la parte logica con la grafica
        txtBuscar = findViewById(R.id.txtBuscar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.recyclerview);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progress_bar);

        //Clear focus in search
        txtBuscar.clearFocus();

        //Enable progress bar and disable input and FAB
        txtBuscar.setEnabled(false);
        floatingActionButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        //Set recycler view
        setRecyclerView();
        getUsers();

        //Se Asigna una función de <actualizar> a la pantalla principal haciendo swipedown(deslizar hacia abajo)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

                //Enable progress bar and disable input and FAB
                txtBuscar.setEnabled(false);
                floatingActionButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                getUsers();
            }
        });

        //Se asigna al boton "+" pasar de la primera pantalla a la segunda, (Sería la acción "CREAR")
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsNetwork.isOnline(MainActivity.this)) {
                    txtBuscar.setQuery(null, false);
                    txtBuscar.clearFocus();
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("method", "CREATE");
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "Necesitas internet para crear una tarjeta.", Toast.LENGTH_LONG).show();
                }

            }
        });

        txtBuscar.setOnQueryTextListener(this);
        txtBuscar.onActionViewExpanded();
        txtBuscar.clearFocus();
        txtBuscar.setQueryHint("Buscar");

    }

    @Override
    public void onResume() {
        super.onResume();

        //Enable progress bar and disable input and FAB
        txtBuscar.setEnabled(false);
        txtBuscar.clearFocus();
        floatingActionButton.setEnabled(false);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        getUsers();
    }



    //Configurar RecyclerView
    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new Adapter(MainActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
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

//                    getCatitos();
                }
            }

            //En el caso de que la peticion falle
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method get list cats
    public static void getCatitos() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient2();

        Call<List<ImageCats>> call = retrofit.create(UsersInterface.class).getImageCats(String.valueOf(usersRetrofit.size()), "live_OpXv3IKTw1SebnMhH1y3XdOay0AVAc2wIxzi7TrMZOUuB43kIvnLSKh2xJPoMYoa ");

        call.enqueue(new Callback<List<ImageCats>>() {
            @Override
            public void onResponse(Call<List<ImageCats>> call, Response<List<ImageCats>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, (CharSequence) response.errorBody(), Toast.LENGTH_SHORT).show();
                } else {
                    catitos = response.body();

                    System.out.println("Size cats: " + catitos.size());

                    txtBuscar.setEnabled(true);
                    floatingActionButton.setEnabled(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);


                    populateUsers(usersRetrofit, catitos);
                }
            }

            @Override
            public void onFailure(Call<List<ImageCats>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Poblamos la data en las tarjetas mediante el adapter
    public static void populateUsers(List<User> usersList, List<ImageCats> imageCats) {
        List<Datos> data = new ArrayList<>();

        for (User user : usersList) {

            data.add(new Datos(
                    user.getId(),
                    user.getFullname(),
                    user.getEmail(),
                    String.valueOf(user.getCode()),
                    null
            ));
        }

        for (int i = 0; i < data.size(); i++) {
            data.get(i).setUrl(imageCats.get(i).getUrl());
        }

        adapter.update(data);

    }

    //Search data in cards
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filtrado(s);
        return false;
    }

}



