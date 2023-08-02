package com.sitiouno.directoryapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PreMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);
        findViewById(R.id.botonIngresar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(PreMainActivity.this, MainActivity.class));
//                openFinancialApp(); //TODO Commit de respaldo
            }
        });
    }
    //TODO COMENTAR SI SE VAN A HACER PRUEBAS CONTRA EL SIMULADOR

    public void openFinancialApp() {
        String applicationId = "com.flexipos.instapago";
        String activityName = "com.pax.pay.MainActivity";
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(applicationId, activityName));

            // Agrega los parámetros al Intent
            intent.putExtra("monto", 100.0);
            intent.putExtra("numeroAfiliado", "123456789");

            // Inicia la actividad de la aplicación de destino
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("PRUEBA", e.getMessage());
        }
    }

    public void changeSysParam() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String digitel = "TDD1.CREDICARD";
        String movistar = "internet.digitel.ve";
        if (activeNetwork != null && activeNetwork.isConnected()) {
            Log.e("COMMTYPE", "CONECTADOS A : " + activeNetwork.getExtraInfo());
            if (activeNetwork.getExtraInfo().equals(digitel) || activeNetwork.getExtraInfo().equals(movistar)) {
                Toast.makeText(this, "Cambiado a privado", Toast.LENGTH_SHORT).show();
//                SysParam.getInstance().set(R.string.COMM_TYPE, SysParam.CommType.PRIVATE);
            } else {
                Toast.makeText(this, "Cambiado a PUBLICO", Toast.LENGTH_SHORT).show();
                Log.e("COMMTYPE", "CAMBIADOS A APN ACTUAL PUBLICO: " + activeNetwork.getExtraInfo());
//                SysParam.getInstance().set(R.string.COMM_TYPE, SysParam.CommType.PUBLIC);
            }

        }
    }
}