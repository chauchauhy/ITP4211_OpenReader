package com.example.mlk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    View toolbarView;
    Toolbar toolbar;
    ImageView add_newBook;
    Context context;
    RecyclerView recyclerView;
    public static ArrayList<Book> books = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        loadData();
    }

    public void initUI(){
        context = this;
        recyclerView = findViewById(R.id.homePage_RecyclerView);
        setToolbar();
    }

    private void setRecyclerView(ArrayList<Book> books){
        cus_book_list cus_book_list1 = new cus_book_list(context, books);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(cus_book_list1);
    }

    private void setToolbar(){
        toolbarView = findViewById(R.id.initialPage_toolbar);
        toolbar = toolbarView.findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        add_newBook = toolbarView.findViewById(R.id.toolbar_add);
        add_newBook.setVisibility(View.VISIBLE);
        add_newBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddNewBook.class));
            }
        });
    }

    private void loadData(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Book");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // a method to catch the hashmap value
                if (dataSnapshot.exists()) {
                    Book book = new Book();
                    books.clear();
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for (String key : hashMap.keySet()){

                       try{
                            book.setContent((String) hashMap.get("Content"));
                            book.setTitle((String) hashMap.get("Title"));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    books.add(book);
                    setRecyclerView(books);
                }


                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
