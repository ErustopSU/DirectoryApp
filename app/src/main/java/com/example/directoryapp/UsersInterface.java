package com.example.directoryapp;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsersInterface {

     //Obtiene la lista completa de usuarios disponibles en la base de datos
    @GET ("users")
    Call<List<User>> listaUsers();

    //Obtiene un usuario por id
    @GET ("users/{id}")
    Call<User> getUser(@Path("id") String id);

    //Crea un usuario con todos los campos llenos (fullname, email, code)
    @POST ("users/create")
    Call<User> createUser(@Body User user);

    //Actualiza datos de un usuario en especifico, puede ser dato por dato o actualizar todo
    @FormUrlEncoded
    @PUT ("users/update/{id}")
    Call<User> updateUser(@Path("id") String id, @Field("fullname") String fullname, @Field("email") String email, @Field("code") int code);

    //Elimina un usuario en especifico
    @DELETE ("users/delete/{id}")
    Call<User> deleteUser(@Path("id") String id);

}