package com.example.directoryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView txtBuscar;
    private FloatingActionButton boton1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private static Adapter adapter;
    public static ProgressBar progressBar;

    public static String _id;
    private String fullname;
    private String email;
    private String code;

    //Data
    private List<User> usersSQLite = new ArrayList<>();
    private List<User> usersRetrofit = new ArrayList();
    private List<ImageCats> catitos = new ArrayList<>();

    //Pagination
    public static int page = 1, limit = 10;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtBuscar = findViewById(R.id.txtBuscar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.recyclerview);
        boton1 = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progress_bar);


        //Load data
        usersSQLite = PreMainActivity.usersSQLite;
        usersRetrofit = PreMainActivity.usersRetrofit;
        catitos = PreMainActivity.catitos;

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
        txtBuscar.onActionViewExpanded();
        txtBuscar.clearFocus();
        txtBuscar.setQueryHint("Buscar");

        //PreMainActivity.getUsers(page, limit);

        /*
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //when reach last item position

                    //Increase page size
                    page++;

                    //show progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    //call method
                    PreMainActivity.getUsers(page, limit);

                    System.out.println("Se esta ejecutando la paginacion");
                }
            }
        });
        */
    }

    @Override
    public void onResume() {
        super.onResume();

        sincronizeUsers();

        /* PreMainActivity.getUsers();
        PreMainActivity.getUsersSqlite();
        PreMainActivity.getCatitos();

        //Load data
        usersSQLite = PreMainActivity.usersSQLite;
        usersRetrofit = PreMainActivity.usersRetrofit;
        catitos = PreMainActivity.catitos;*/
    }

    private void sincronizeUsers() {
        if (UtilsNetwork.isOnline(this)) {
            //Toast.makeText(getApplicationContext(), "Si hay internet", Toast.LENGTH_SHORT).show();
            System.out.println("Si hay internet");
            // Retrofit
            for (int i = 0; i < usersRetrofit.size(); i++) {
                System.out.println("ID ANTES DEL for: " + usersRetrofit.get(i).getId().length());

                // SQLite
                for (int j = 0; j < usersSQLite.size(); j++) {

                    String retrofitId = usersRetrofit.get(i).getId();
                    String retrofitFullname = usersRetrofit.get(i).getFullname();
                    String retrofitEmail = usersRetrofit.get(i).getEmail();
                    String retrofitCode = String.valueOf(usersRetrofit.get(i).getCode());

                    String SqlId = usersSQLite.get(j).getId();

                    if (usersSQLite.contains(retrofitId)) {
                        System.out.println("User found");
                    } else {
                        System.out.println("User not found");

                        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this);

                        adminSQLiteOpenHelper.registerUser(_id, fullname, email, code);
                        //TODO: Method create user in SQLite -> Retrofit
                    }
                }

                populateUsers(usersRetrofit, catitos);
            }

        } else {
            Toast.makeText(getApplicationContext(), "Cargando datos sin internet", Toast.LENGTH_LONG).show();
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



