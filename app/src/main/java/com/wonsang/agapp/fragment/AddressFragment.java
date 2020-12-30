package com.wonsang.agapp.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wonsang.agapp.R;
import com.wonsang.agapp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class AddressFragment extends Fragment {

    private RecyclerView user = null;
    private AddressAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.address_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.user = view.findViewById(R.id.recycler);
        this.adapter = new AddressAdapter(getAllUsers(), getContext());

        user.setAdapter(adapter);
        user.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter.notifyDataSetChanged();
    }

    private List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();

        ContentResolver resolver = getContext().getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        //TODO Cursor Null Check
        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = resolver
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                List<String> phoneNumbers = new ArrayList<>();
                while (pCur.moveToNext()) {
                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumbers.add(phoneNo);
                }
                users.add(new UserModel(name, phoneNumbers));
                pCur.close();
            }
        }
        cursor.close();
        return users;
    }

    static class AddressAdapter extends RecyclerView.Adapter<AddressViewHolder> {
        private List<UserModel> users = null;
        private Context context;

        AddressAdapter(List<UserModel> users, Context context){
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.my_text_view, null);
//            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            TextView textView = new TextView(context);
//            textView.setLayoutParams(layoutParams);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            UserModel user = users.get(position);

            holder.name.setText(user.getName());
            holder.phoneNumber.setText(user.getPhoneNumber().get(0));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phoneNumber;

        public AddressViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.name);
            this.phoneNumber = view.findViewById(R.id.phoneNumber);
        }

    }
}

