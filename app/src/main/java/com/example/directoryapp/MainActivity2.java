package com.example.directoryapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity2 extends AppCompatActivity {

    private TextView tv1;
    private TextInputLayout et1, et2, et3;
    private Button boton1, boton2;

    private String id, fullname, email, code, method;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tv1 = findViewById(R.id.txtv1);
        et1 = findViewById(R.id.cajaet1);
        et2 = findViewById(R.id.cajaet2);
        et3 = findViewById(R.id.cajaet3);
        boton2 = findViewById(R.id.cancelar);
        boton1 = findViewById(R.id.boton1);

        id = getIntent().getStringExtra("id");
        fullname = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        code = getIntent().getStringExtra("code");
        method = getIntent().getStringExtra("method");

        et1.getEditText().setText(fullname);
        et2.getEditText().setText(email);
        et3.getEditText().setText(code);


        tv1.setText(method);
        boton1.setText(method);

        switch (method) {
            case "OBTENER":
                boton1.setVisibility(View.GONE);

                tv1.setText("VISTA DE LA TARJETA");
                boton2.setText("REGRESAR");

                TextInputLayout til1 = (TextInputLayout) findViewById(R.id.cajaet1);
                til1.setHelperTextEnabled(false);
                til1.setHint("Nombre");

                TextInputLayout til2 = findViewById(R.id.cajaet2);
                til2.setHelperTextEnabled(false);
                til2.setHint("Correo");

                TextInputLayout til3 = findViewById(R.id.cajaet3);
                til3.setHelperTextEnabled(false);
                til3.setHint("Codigo");
                til3.setCounterEnabled(false);

                et1.setEnabled(false);
                et2.setEnabled(false);
                et3.setEnabled(false);

                break;
            case "ACTUALIZAR":
                boton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fullname = et1.getEditText().getText().toString();
                        String email = et2.getEditText().getText().toString();
                        int code = Integer.parseInt(et3.getEditText().getText().toString());

                        updateUser(fullname, email, code, id);
                    }
                });
                break;
            case "CREATE":

                tv1.setText("CREA UNA TARJETA");
                boton1.setText("CREAR");

                boton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String compruebaname = et1.getEditText().getText().toString();
                        final String compruebaemail = et2.getEditText().getText().toString().trim();
                        final String regex = "(?:[^<>()\\[\\].,;:\\s@\"]+(?:\\.[^<>()\\[\\].,;:\\s@\"]+)*|\"[^\\n\"]+\")@(?:[^<>()\\[\\].,;:\\s@\"]+\\.)+[^<>()\\[\\]\\.,;:\\s@\"]{2,63}";
                        final String solotexto = "[a-zA-Z ]+";

                        if (et1.getEditText().getText().toString().isEmpty() || et2.getEditText().getText().toString().isEmpty() || et3.getEditText().getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity2.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                        } else if ((!compruebaname.matches(solotexto))) {
                            Toast.makeText(MainActivity2.this, "Asegurate de ingresar solo texto en el campo de nombre", Toast.LENGTH_SHORT).show();
                        } else if (et3.getEditText().getText().length() > 5) {
                            Toast.makeText(MainActivity2.this, "El codigo es mayor a 5 digitos", Toast.LENGTH_SHORT).show();
                        } else if ((!compruebaemail.matches(regex))) {
                            Toast.makeText(MainActivity2.this, "Por favor, introduce un correo valido", Toast.LENGTH_LONG).show();
                        } else {
                            createUser(retrieveUser());

                            // ponemos los campos a vacío para insertar el siguiente usuario
                            //et1.getEditText().setText("");
                            //et2.getEditText().setText("");
                            //et3.getEditText().setText("");

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

                                switch(message){
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
                    return;
                } else {

                    User user1 = response.body();

                    String _id = user1.getId();
                    String fullname = et1.getEditText().getText().toString();
                    String email = et2.getEditText().getText().toString();
                    String code = et3.getEditText().getText().toString();

                    Toast.makeText(MainActivity2.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
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

                                switch(message){
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
                    return;

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

            }
        });
    }


}
