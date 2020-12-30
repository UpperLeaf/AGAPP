package com.wonsang.agapp.model;

import android.net.Uri;
import android.provider.ContactsContract;

public class ImageModel {
    private Uri path;

    public ImageModel(Uri path){
        this.path = path;
    }

    public Uri getPath() {
        return path;
    }
    public void setPath(Uri path) {
        this.path = path;
    }
}
