package com.example.directoryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView txtBuscar;
    private FloatingActionButton boton1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private static Adapter adapter;
    //private ImageView imagen;
    //static Bitmap bitmap;
    //String urlImage = "https://api.thecatapi.com/v1/images/";


    public static String _id;
    private String fullname;
    private String email;
    private String code;


    private List<User> usersSQLite = new ArrayList<>();
    private List<User> usersRetrofit = new ArrayList();
    private List<ImageCats> catitos = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtBuscar = findViewById(R.id.txtBuscar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.recyclerview);
        boton1 = findViewById(R.id.floatingActionButton);


        //Load data
        usersSQLite = PreMainActivity.usersSQLite;
        usersRetrofit = PreMainActivity.usersRetrofit;
        catitos = PreMainActivity.catitos;
        //new GetImageFromUrl(imagen).execute(urlImage);

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
    }

    //Metodo para traernos las imagenes de la api y setearlas en el contenedor hacia la vista
    /*
    private void cargarWebServiceImagen() {

        String url = "https://api.thecatapi.com/v1/images/search";
        imagen = findViewById(R.id.imageUserGallery);
        //new URL(gatos.getUrl());

        ImageRequest imageRequest = new ImageRequest(url, new com.android.volley.Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                System.out.println("Imagenes de gatitos");
                imagen.setImageBitmap(response);
                //Uri path = imagenRequest.getUrl();
                //imagen.setImageURI(Uri.parse(url));
            }
        }, 15, 15, ImageView.ScaleType.CENTER, null, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);
    }
    */

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
            Toast.makeText(getApplicationContext(), "Si hay internet", Toast.LENGTH_SHORT).show();

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

    //Metodo para que al poblar las tarjetas se le asigne una imagen diferente a cada cardview
    /*public static void catitosCards(List<ImageCats> imageCats){
        List<ImageCats> imageCatitos = new ArrayList<>();

        for (ImageCats image: imageCats) {
            imageCatitos.add(new ImageCats(
                                image.getUrl(),


            ));

        }
    }*/

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



