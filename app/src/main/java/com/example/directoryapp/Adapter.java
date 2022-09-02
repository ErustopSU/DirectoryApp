package com.example.directoryapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolderAdapter> {

    public List<Datos> data;
    private Context context;


    AdminSQLiteOpenHelper adminSQLiteOpenHelper;

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
        return new MyViewHolderAdapter(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false));
    }

    //Asignar los datos y asignar funciones a los metodos en el caso de que sea necesario
    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolderAdapter holder, int position) {

        if (data != null && data.size() > 0) {
            Datos datas = data.get(position);

            String id = data.get(position).getId();

            holder.fullname.setText("Nombre: " + datas.getFullname());
            holder.email.setText("Correo: " + datas.getEmail());
            holder.code.setText("Código: " + datas.getCode());

            //Se Asignan funciones de acción al cardview
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getUser(id, "OBTENER");
                }
            });

            holder.editboton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (UtilsNetwork.isOnline(context)) {

                        getUser(id, "ACTUALIZAR");

                    } else {
                        Toast.makeText(context, "No puedes editar tarjetas sin conexión a internet.", Toast.LENGTH_LONG).show();


                    }
                }
            });

            holder.deleteboton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog(id).show();
                }
            });
        }

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

        public MyViewHolderAdapter(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.card_fullname);
            email = itemView.findViewById(R.id.card_email);
            code = itemView.findViewById(R.id.card_code);
            editboton = itemView.findViewById(R.id.editbutton);
            deleteboton = itemView.findViewById(R.id.deletebutton);
        }
    }

    //Get user by id RETROFIT
    private void getUser(String id, String method) {

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

                    context.startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

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

            }
        });
    }

    //Ventana emergenete para eliminar un usuario
    public AlertDialog alertDialog(String id) {

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

