package com.example.mlk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class cus_book_list extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Book> books;
    LayoutInflater layoutInflater;

    public cus_book_list(Context context, ArrayList<Book> books){
        layoutInflater = LayoutInflater.from(context);
        this.books = books;
        this.context = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_book_list, parent, false);
        RecyclerView.ViewHolder viewHolder = new VH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        VH viewhelder = (VH) holder;
        Book book = books.get(position);
        viewhelder.bookTitle.setText(book.getTitle());
        viewhelder.bookTitle.setClickable(true);
        viewhelder.bookTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Speech.class);
                intent.putExtra("position", String.valueOf(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (books.size()>0){
            return books.size();
        }else {
            return 0;
        }    }

    public class VH extends RecyclerView.ViewHolder{
        TextView bookTitle;


        public VH(@NonNull View itemView) {
            super(itemView);
            bookTitle =itemView.findViewById(R.id.book_titile);

        }
    }

}
