package com.wonsang.agapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<MyViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = findViewById(R.id.recycle_view);
        this.layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}


class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private String[] address;

    MyAdapter(String[] address) {
        this.address = address;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO TextView 생성 변경
        TextView v = new TextView(parent.getContext());
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView view = holder.getTextView();
        view.setText(address[position]);
    }

    @Override
    public int getItemCount() {
        return address.length;
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;

    public TextView getTextView() {
        return textView;
    }

    public MyViewHolder(@NonNull TextView itemView) {
        super(itemView);
        this.textView = itemView;
    }
}