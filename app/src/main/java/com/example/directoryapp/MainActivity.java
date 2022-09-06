package com.example.directoryapp;

import static com.example.directoryapp.PreMainActivity.getUsers;
import static com.example.directoryapp.PreMainActivity.getUsersSQLite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView txtBuscar;
    FloatingActionButton boton1;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private static Adapter adapter;

    List<User> usersSQLite = new ArrayList<>();
    public static List<User> usersRetrofit = new ArrayList();

    private String _id;
    private String fullname;
    private String email;
    private String code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtBuscar = findViewById(R.id.txtBuscar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.recyclerview);
        boton1 = findViewById(R.id.floatingActionButton);

        setRecyclerView();
        sincronizeUsers();

        //Se Asigna una función de <actualizar> a la pantalla principal haciendo swipedown(deslizar hacia abajo)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                sincronizeUsers();
            }

        });

        //  getUsers();

        //Se asigna al boton "+" pasar de la primera pantalla a la segunda, (Sería la acción "CREAR")
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsNetwork.isOnline(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);

                    intent.putExtra("method", "CREATE");

                    startActivity(intent);

                } else {

                    Toast.makeText(MainActivity.this, "Necesitas internet para crear una tarjeta.", Toast.LENGTH_LONG).show();
                }

            }
        });

        txtBuscar.setOnQueryTextListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        sincronizeUsers();
    }

    private void sincronizeUsers() {
        if (UtilsNetwork.isOnline(this)) {
            Toast.makeText(getApplicationContext(), "Si hay internet", Toast.LENGTH_SHORT).show();
            getUsers();
            System.out.println(usersRetrofit.size());
            System.out.println("Probandooo");

            //RETROFIT
            for (int i = 0; i < usersRetrofit.size(); i++) {
                System.out.println("ID ANTES DEL for: " + usersRetrofit.get(i).getId().length());
                //SQLITE
                for (int j = 0; j < usersSQLite.size(); j++) {

                    String RETROFIT = usersRetrofit.get(i).getId();
                    String fullname = usersRetrofit.get(i).getFullname();
                    String email = usersRetrofit.get(i).getEmail();
                    String code = String.valueOf(usersRetrofit.get(i).getCode());
                    String SQL = usersSQLite.get(j).getId();

                    System.out.println("ID: " + SQL + ", FULLNAME: " + fullname + ", EMAIL: " + email + ", CODE: " + code + ".");
                    System.out.println("Sincronizando...");
                    System.out.println(RETROFIT);
                    System.out.println("123");
                    System.out.println(SQL);
                    System.out.println(usersRetrofit.get(i).getFullname());


                    if (usersSQLite.contains(RETROFIT)) {
                        System.out.println("User found");
                    } else {
                        System.out.println("User not found");
                        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this);

                        adminSQLiteOpenHelper.registerUser(_id, fullname, email, code);
                        //TODO: Method create user in SQLite -> Retrofit
                    }
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Cargando datos sin internet", Toast.LENGTH_LONG).show();

            getUsersSQLite();
        }
    }


    //Configurar RecyclerView
    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new Adapter(MainActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    //Poblamos la data en las tarjetas mediante el adapter
    public static void populateUsers(List<User> usersList) {
        List<Datos> data = new ArrayList<>();

        for (User user : usersList) {

            data.add(new Datos(user.getId(), user.getFullname(), user.getEmail(), String.valueOf(user.getCode())));
        }

        adapter.update(data);

    }


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

