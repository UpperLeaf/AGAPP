package com.wonsang.agapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.R;
import com.wonsang.agapp.model.UserModel;

public class AddressDialog extends Dialog {


    private boolean isFetched;
    private ImageView imageView;
    private TextView name;
    private TextView phoneNo;
    private TextView email;
    private UserModel userModel;
    public AddressDialog(@NonNull Context context, UserModel userModel) {
        super(context);
        this.userModel = userModel;
    }

    public boolean isFetched() {
        return isFetched;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setWindowAsBehind();
        setContentView(R.layout.address_dialog);
        /////
        imageView = findViewById(R.id.address_image);
        name = findViewById(R.id.address_name);
        phoneNo = findViewById(R.id.address_phoneNumber);
        email = findViewById(R.id.address_email);
        Glide.with(getContext()).load(userModel.getPhotoUri()).placeholder(R.drawable.blank_person).into(imageView);
        name.setText(userModel.getName());
        phoneNo.setText(userModel.getPhoneNumber().get(0));
        email.setText(userModel.getEmail());
    }

    private void setWindowAsBehind() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
    }
}
