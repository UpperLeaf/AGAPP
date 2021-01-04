package com.wonsang.agapp.dialog;

import android.app.Dialog;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.R;
import com.wonsang.agapp.utils.ContentInfoProvider;

import java.util.ArrayList;

public class ImageDialog extends Dialog {

    private boolean isFetched;
    private ImageView imageView;
    private Uri uri;
    public ImageDialog(@NonNull Context context, Uri uri) {
        super(context);
        this.uri = uri;
    }

    public boolean isFetched() {
        return isFetched;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setWindowAsBehind();
        setContentView(R.layout.image_dialog);
        imageView = findViewById(R.id.dialog_image);
        Glide.with(getContext()).load(uri).into(imageView);
    }

    private void setWindowAsBehind() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
    }

}
