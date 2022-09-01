package com.example.directoryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.contentcapture.DataShareWriteAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton boton1;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private static Adapter adapter;

    List<User> usersSQLite = new ArrayList<>();
    private static List<User> usersRetrofit = new ArrayList();

    private String _id;
    private String fullname;
    private String email;
    private String code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

    @Override
    public void onResume() {
        super.onResume();

        sincronizeUsers();
    }

    private void sincronizeUsers() {
        if (UtilsNetwork.isOnline(this)) {
            Toast.makeText(this, "Si hay internet", Toast.LENGTH_SHORT).show();

            getUsers();

            //RETROFIT
            for (int i = 0; i < usersRetrofit.size(); i++) {

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
            Toast.makeText(MainActivity.this, "Cargando datos sin internet", Toast.LENGTH_LONG).show();

            getUsersSQLite();
        }
    }

    //metodo getUsers con SQLite
    public void getUsersSQLite() {
        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(MainActivity.this);

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

    //Configurar RecyclerView
    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new Adapter(MainActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    //Poblamos la data en las tarjetas mediante el adapter
    private static void populateUsers(List<User> usersList) {
        List<Datos> data = new ArrayList<>();

        for (User user : usersList) {

            data.add(new Datos(user.getId(), user.getFullname(), user.getEmail(), String.valueOf(user.getCode())));
        }

        adapter.update(data);

    }


}

