package com.example.mlk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class Speech extends AppCompatActivity {

    Book book;
    TextView title;
    ListView contentList;
    ListAdapter listAdapter;
    View view;
    ArrayList<String> content = new ArrayList<>();
    Context context;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        initUI();
        loadLocalData();



    }
    private void initUI(){
        context = this;
        title = findViewById(R.id.speech_title);
        contentList = findViewById(R.id.speech_bookContent_list);
        view = findViewById(R.id.speech_toolBar);
        ImageView back = view.findViewById(R.id.toolbar_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });
        setTextToSpeech();
        contentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String toSpeak = content.get(i);
                    if (!toSpeak.isEmpty()){
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void setTextToSpeech() {
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                }
            });

    }
    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }






    private void loadLocalData(){
        Intent intent = getIntent();
        String position = intent.getStringExtra("position");
        book = MainActivity.books.get(Integer.valueOf(position));
        title.setText("Book Name : " + book.getTitle());
        String[] tokens = book.getContent().split("\n");
        for (String token : tokens){
            content.add(token);
        }
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, content);
        contentList.setAdapter(listAdapter);


       }
}
