package com.sitiouno.directoryapp.Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RetrofitClient {
    private static String BASE_URL = "https://reto-android.herokuapp.com/api/v1/";
    private static Retrofit retrofit;
    private static String BASE_URL_GATITOS = " https://api.thecatapi.com/v1/images/";
    private static Retrofit retrofit2;
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

    public static Retrofit getRetrofitClient2() {

        if (retrofit2 == null) {

            //Creamos un interceptor y le indicamos el log level a usar
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);

            //Asociamos el interceptor a las peticiones
            OkHttpClient.Builder cliente = new OkHttpClient.Builder();
            cliente.connectTimeout(60, TimeUnit.SECONDS);
            cliente.readTimeout(60, TimeUnit.SECONDS);
            cliente.writeTimeout(60, TimeUnit.SECONDS);
            cliente.addInterceptor(interceptor);

            /*
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/images/search?api_key=live_OpXv3IKTw1SebnMhH1y3XdOay0AVAc2wIxzi7TrMZOUuB43kIvnLSKh2xJPoMYoa")
                    .method("GET", body).build();
            Response response1 = cliente.build().newCall(request).execute();
            */

            gson = new GsonBuilder()
                    .serializeNulls() //omite todos los datos nulos que no sean de tipo String
                    .setLenient() //debido a que el servidor acepta solo archivos de JSON, y esto hace que se conviertan a GSON
                    .create();


            retrofit2 = new Retrofit.Builder()
                    .baseUrl(BASE_URL_GATITOS)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(cliente.build())
                    .build();
        }
        return retrofit2;

    }


}
