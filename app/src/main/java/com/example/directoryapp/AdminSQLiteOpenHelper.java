package com.example.directoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "BASE DE DATOS";

    public static final String TUSUARIOS = "TUSUARIOS";
    private static final String COL_0 = "ID";
    private static final String COL_1 = "FULLNAME";
    private static final String COL_2 = "EMAIL";
    private static final String COL_3 = "CODE";


    //Constructor
    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    //Crear Base de Datos
    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {

        BaseDeDatos.execSQL(
                "CREATE TABLE  " + TUSUARIOS +
                        "("
                        + COL_0 + " string PRIMARY KEY,"
                        + COL_1 + " string UNIQUE NOT NULL, "
                        + COL_2 + " string UNIQUE NOT NULL, "
                        + COL_3 + " string UNIQUE NOT NULL " +
                        ")"
        );


    }


    //Actualizar base de datos
    @Override
    public void onUpgrade(SQLiteDatabase BaseDeDatos, int versionAntigua, int versionNueva) {
        BaseDeDatos.execSQL("DROP TABLE IF EXISTS " + TUSUARIOS);
        onCreate(BaseDeDatos);
    }

    //Insertar datos a la base de datos :v
    public boolean registerUser(
            @NonNull String _id,
            @NonNull String fullname,
            @NonNull String email,
            @NonNull String code

    ) throws SQLException {
        SQLiteDatabase BaseDeDatos = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        try {
            values.put(COL_0, _id);
            values.put(COL_1, fullname);
            values.put(COL_2, email);
            values.put(COL_3, code);

            long result = BaseDeDatos.insert(TUSUARIOS, null, values);

            closeDatabase(BaseDeDatos);
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    //Close database
    public void closeDatabase(SQLiteDatabase BaseDeDatos) {
        try {
            //BaseDeDatos.close();
            //Log.i("SQLite", "Closing database");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("SQLite", "Error closing database");
        }
    }

    //Update user
    public int updateUser(
            @NonNull String _id,
            @NonNull String fullname,
            @NonNull String email,
            @NonNull String code

    ) throws SQLException {

        SQLiteDatabase BaseDeDatos = this.getWritableDatabase();

       /* //probando condicion para crear un registro si no existe en SQLite
        if (!COL_0.matches(_id)) {
            registerUser(_id, fullname, email, code);  //TODO: hacer un muevo metodo aparte
            System.out.println("Match");
        } else {
            System.out.println("Dont match");
        }*/

        try {
            ContentValues values = new ContentValues();

            values.put(COL_1, fullname);
            values.put(COL_2, email);
            values.put(COL_3, code);

            int user = BaseDeDatos.update(TUSUARIOS, values, COL_0 + " = '" + _id + "'", null);

            closeDatabase(BaseDeDatos);
            return user;
        } catch (SQLException e) {
            closeDatabase(BaseDeDatos);
            e.printStackTrace();
        }

        closeDatabase(BaseDeDatos);
        return 0;
    }

    //Check if user is already registered
    public Cursor consultUser() throws SQLException {
        SQLiteDatabase BaseDeDatos = this.getReadableDatabase();

        try {
            Cursor user = BaseDeDatos.rawQuery("SELECT * FROM " + TUSUARIOS, null);

            if (user.moveToFirst()) {
                closeDatabase(BaseDeDatos);
                return user;
            } else {
                closeDatabase(BaseDeDatos);
                return null;
            }
        } catch (SQLException e) {
            closeDatabase(BaseDeDatos);
            e.printStackTrace();
        }
        closeDatabase(BaseDeDatos);
        return null;
    }

    //Consult user by uid
    public Cursor consultUserByid(@NonNull String _id) throws SQLException {
        SQLiteDatabase BaseDeDatos = this.getReadableDatabase();

        try {

            Cursor user = BaseDeDatos.rawQuery("SELECT * FROM " + TUSUARIOS + " WHERE " + COL_0 + " = '" + _id + "'", null);

            if (user.moveToFirst()) {
                closeDatabase(BaseDeDatos);
                return user;
            } else {
                closeDatabase(BaseDeDatos);
                return null;
            }

        } catch (SQLException e) {
            closeDatabase(BaseDeDatos);
            e.printStackTrace();
        }

        closeDatabase(BaseDeDatos);
        return null;
    }

    public boolean deleteData(String _id) {
        SQLiteDatabase BaseDeDatos = this.getWritableDatabase();
        boolean correcto = false;

        try {
            BaseDeDatos.execSQL("DELETE FROM " + TUSUARIOS + " WHERE " + COL_0 + " = '" + _id + "'");
            correcto = true;
        } catch (SQLException e) {
            e.toString();
            correcto = false;
        } finally {
            closeDatabase(BaseDeDatos);
        }

        return correcto;
    }
}





