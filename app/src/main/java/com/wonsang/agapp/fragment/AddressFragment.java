package com.wonsang.agapp.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wonsang.agapp.R;

import java.util.ArrayList;

public class AddressFragment extends Fragment {

    private RecyclerView user = null;
    private MyViewAdapter adapter = null;
    private ArrayList<User> list = new ArrayList<User>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.address_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.user = view.findViewById(R.id.recycler);
        this.adapter = new MyViewAdapter(list);
        user.setAdapter(adapter);
        user.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//        while{
//            addUser();
//        }
        adapter.notifyDataSetChanged();
    }

    public void addUser(String name, String phoneNumber){
        User user = new User();

        user.setName(name);
        user.setPhoneNumber(phoneNumber);

        list.add(user);
    }


    public class MyViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<User> data = null;

        MyViewAdapter(ArrayList<User> list){
            data = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(layoutParams);
            MyViewHolder vh = new MyViewHolder(textView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            User user = data.get(position);

            holder.name.setText(user.getName());
            holder.phoneNumber.setText(user.getPhoneNumber());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phoneNumber;


        public MyViewHolder(@NonNull TextView itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.phoneNumber = itemView.findViewById(R.id.phoneNumber);
        }

    }
}

