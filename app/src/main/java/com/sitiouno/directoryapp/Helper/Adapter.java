package com.sitiouno.directoryapp.Helper;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.sitiouno.directoryapp.Database.AdminSQLiteOpenHelper;
import com.sitiouno.directoryapp.Database.Models.ImageCats;
import com.sitiouno.directoryapp.Database.RetrofitClient;
import com.sitiouno.directoryapp.MainActivity;
import com.sitiouno.directoryapp.MainActivity2;
import com.sitiouno.directoryapp.PreMainActivity;
import com.sitiouno.directoryapp.R;
import com.sitiouno.directoryapp.Database.Models.User;
import com.sitiouno.directoryapp.Database.Interfaces.UsersInterface;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolderAdapter> {

    public List<Datos> data;
    private Context context;
    public List<Datos> dataBuscador;

    //Constructor
    public Adapter(Context context, ArrayList<Datos> data) {
        this.context = context;
        this.data = data;
    }

    //Actualizar los datos
    public void update(List<Datos> list) {
        data = list;
        notifyDataSetChanged();
    }

    //Retornamos una nueva vista del elemento que queremos cargar, en este caso, el card view
    @NonNull
    @Override
    public Adapter.MyViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false));
    }

    //Asignar los datos y asignar funciones a los metodos en el caso de que sea necesario
    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolderAdapter holder, int position) {
        if (data != null && data.size() > 0) {
            //Para que no de problemas con la red y el como se ejecuta la cantidad de llamadas que vienen con abrir la aplicacion
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Datos datas = data.get(position);

            String id = data.get(position).getId();

            holder.fullname.setText(datas.getFullname());
            holder.email.setText(datas.getEmail());
            holder.code.setText(datas.getCode());

            System.out.println("URL: " + datas.getUrl());

            Bitmap resized = Bitmap.createScaledBitmap(getImageBitmap(datas.getUrl()), 50, 50, false);

            holder.imagen.setImageBitmap(resized);

            //GET
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getUser(id, "OBTENER", position);
                }
            });

            //UPDATE
            holder.editboton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UtilsNetwork.isOnline(context)) {
                        getUser(id, "ACTUALIZAR", position);
                    } else {
                        Toast.makeText(context, "No puedes editar tarjetas sin conexión a internet.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            //DELETE
            holder.deleteboton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    deleteDialog(id).show();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filtrado(String txtBuscar) {

        if (txtBuscar.isEmpty()) {
            MainActivity.getUsers();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Datos> coleccion = data.stream().filter(i -> i.getFullname().toLowerCase()
                        .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());

                data.clear();
                data.addAll(coleccion);
            } else {
                for (Datos d : dataBuscador) {
                    if (d.getFullname().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        data.add(d);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    //Retorna la cantidad total de datos que tengamos, si tenemos 1 un usuario retorna 1
    @Override
    public int getItemCount() {
        return data.size();
    }

    //Referencia con la vista del componente, en este caso el card view
    public class MyViewHolderAdapter extends RecyclerView.ViewHolder {
        TextView fullname, email, code;
        ImageButton editboton, deleteboton;
        ImageView imagen, vistaimagen;

        public MyViewHolderAdapter(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.card_fullname);
            email = itemView.findViewById(R.id.card_email);
            code = itemView.findViewById(R.id.card_code);
            editboton = itemView.findViewById(R.id.editbutton);
            deleteboton = itemView.findViewById(R.id.deletebutton);
            imagen = itemView.findViewById(R.id.imageUserGallery);

        }
    }

    //Un bitmap es un formato de imagen que permite adaptar la imagen al requerimento que tu app tiene
    private Bitmap getImageBitmap(String url) {
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            bufferedInputStream.close();
            bufferedInputStream.close();

            return bitmap;
        } catch (IOException e) {
            Log.e("Image error", "Error getting bitmap", e);
            return null;
        }
    }

    //Get user by id RETROFIT
    public void getUser(String id, String method, int position) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        Call<User> getUser = retrofit.create(UsersInterface.class).getUser(id);

        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (!response.isSuccessful()) {

                    switch (response.code()) {

                        case 404:
                            Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
                            break;
                        case 500:
                            Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    User user = response.body();

                    String id = user.getId();
                    String name = user.getFullname();
                    String email = user.getEmail();
                    String code = String.valueOf(user.getCode());

                    Intent intent = new Intent(context, MainActivity2.class);

                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("code", code);
                    intent.putExtra("method", method);
                    intent.putExtra("position", position);

                    context.startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Delete user by id
    private void deleteUser(String id) {
        //Creamos conexion con Retrofit
        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        //Hacemos la llamada al endpoint mediante la interfaz
        Call<User> deleteUser = retrofit.create(UsersInterface.class).deleteUser(id);

        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(context);
        adminSQLiteOpenHelper.deleteData(id);

        deleteUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 500:
                            Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    MainActivity.getUsers();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Ventana emergenete para eliminar un usuario
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AlertDialog deleteDialog(String id) {

        if (UtilsNetwork.isOnline(context)) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(context);
            constructor.setTitle("Eliminar");
            constructor.setIcon(context.getDrawable(R.drawable.ic_baseline_deleteop80_24));
            constructor.setMessage(R.string.Eliminar1);

            constructor.setPositiveButton(R.string.Eliminarb, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteUser(id);
                }
            });

            constructor.setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            return constructor.create();
        } else {
            AlertDialog.Builder constructor = new AlertDialog.Builder(context);
            constructor.setTitle("¡No tan rápido!");
            constructor.setIcon(context.getDrawable(R.drawable.ic_baseline_block_24));
            constructor.setMessage("No puedes eliminar tarjetas sin conexión a internet.");

            constructor.setNegativeButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            return constructor.create();
        }


    }
}

