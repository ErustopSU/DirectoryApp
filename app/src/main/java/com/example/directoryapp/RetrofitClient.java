package com.example.directoryapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RetrofitClient {
    private static String BASE_URL = "https://reto-android.herokuapp.com/api/v1/";
    private static Retrofit retrofit;
    private static Gson gson;

    public static Retrofit getRetrofitClient() {

        if (retrofit == null) {

            //Creamos un interceptor y le indicamos el log level a usar
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);

            //Asociamos el interceptor a las peticiones
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(60, TimeUnit.SECONDS);
            client.readTimeout(60, TimeUnit.SECONDS);
            client.writeTimeout(60, TimeUnit.SECONDS);
            client.addInterceptor(interceptor);

            gson = new GsonBuilder()
                    .serializeNulls() //omite todos los datos nulos que no sean de tipo String
                    .setLenient() //debido a que el servidor acepta solo archivos de JSON, y esto hace que se conviertan a GSON
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.build())
                    .build();
        }
        return retrofit;

    }
}
