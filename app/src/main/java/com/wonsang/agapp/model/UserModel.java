package com.wonsang.agapp.model;

import android.net.Uri;

import java.util.List;

public class UserModel {
    private String name;
    private List<String> phoneNumber;
    private Uri photoUri;
    private String email;
    private String group;

    public UserModel(String name, List<String> phoneNumber, Uri photoUri, String email, String group) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
        this.email = email;
        this.group = group;
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
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public void setGroup(String group) { this.group = group; }
    public String getGroup() { return group; }
}
