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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.wonsang.agapp.R;
import com.wonsang.agapp.utils.ContentInfoProvider;

import java.util.ArrayList;

public class ContactDialog extends Dialog {

    private EditText nameText;
    private EditText phoneNumberText;
    private Button contactAddButton;
    private ContentInfoProvider contentInfoProvider;

    private boolean isFetched;

    public ContactDialog(@NonNull Context context, ContentInfoProvider contentInfoProvider) {
        super(context);
        isFetched = false;
        this.contentInfoProvider = contentInfoProvider;
    }

    public boolean isFetched() {
        return isFetched;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setWindowAsBehind();
        setContentView(R.layout.contact_dialog);

        nameText = findViewById(R.id.contact_dialog_name);
        phoneNumberText = findViewById(R.id.contact_dialog_phone);
        phoneNumberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        contactAddButton = findViewById(R.id.contact_add_button);
        contactAddButton.setOnClickListener((v) -> {
            String name = nameText.getText().toString();
            String phoneNumber = phoneNumberText.getText().toString();

            if(name.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "이름이나 핸드폰 번호는 비어있을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            isFetched = contentInfoProvider.addContactInfo(getContext().getContentResolver(), name, phoneNumber);
            dismiss();
        });
    }

    private void setWindowAsBehind() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
    }

}
