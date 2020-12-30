package com.wonsang.agapp.model;

import android.net.Uri;

import java.util.List;

public class UserModel {
    private String name;
    private List<String> phoneNumber;
    private Uri photoUri;

    public UserModel(String name, List<String> phoneNumber, Uri photoUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public List<String> getPhoneNumber() {
        return phoneNumber;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
