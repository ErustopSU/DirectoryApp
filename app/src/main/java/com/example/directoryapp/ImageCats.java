package com.example.directoryapp;


import com.google.gson.annotations.SerializedName;

public class ImageCats {
    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    //Metodos Getter y Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
