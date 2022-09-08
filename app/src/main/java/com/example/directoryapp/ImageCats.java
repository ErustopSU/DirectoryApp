package com.example.directoryapp;


import com.google.gson.annotations.SerializedName;

public class ImageCats {
    @SerializedName("id")
    private static String id;

    @SerializedName("url")
    private static String url;

    @SerializedName("width")
    private static int width;

    @SerializedName("height")
    private static int height;

    //Metodos Getter y Setter
    public static String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
