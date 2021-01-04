package com.wonsang.agapp.model;

import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import com.wonsang.agapp.dialog.ImageDialog;

public class ImageModel {
    private Uri path;

    public ImageModel(Uri path){
        this.path = path;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof ImageModel){
            ImageModel imageModel = (ImageModel)obj;
            return path.equals(imageModel.getPath());
        }
        return false;
    }

    public Uri getPath() {
        return path;
    }
    public void setPath(Uri path) {
        this.path = path;
    }
}
