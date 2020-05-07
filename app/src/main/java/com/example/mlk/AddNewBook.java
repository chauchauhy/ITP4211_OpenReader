package com.example.mlk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrinterCapabilitiesInfo;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.Manifest.*;

public class AddNewBook extends AppCompatActivity implements Dialog_bk_title.DialogListener {
    Context context;
    int TAKE_CARMA = 1000;
    int PICKED_IMAGE = 1111;
    Bitmap bitmap;
    ImageView imageView;
    Uri image_uri; // image path
    View view;
    Toolbar toolbar;
    ImageView re_add, back;
    TextView textView;
    String text_Str = "";
    ArrayList<String> text = new ArrayList<>();
    Book book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_book);
        initUI();
        checkPermission();
    }

    private void initUI() {
        context = this;
        book = new Book();
        imageView = findViewById(R.id.Add_ImageView);
        view = findViewById(R.id.addNewBook_toolbar);
        toolbar = view.findViewById(R.id.app_toolbar);
        textView = findViewById(R.id.ShowTheText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        re_add = view.findViewById(R.id.toolbar_add);
        re_add.setVisibility(View.VISIBLE);
        TextView title = view.findViewById(R.id.toolbar_title);
        title.setText("Add New Book");
        title.setVisibility(View.VISIBLE);
        back = view.findViewById(R.id.toolbar_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });
        re_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowTheSelection();
            }
        });


    }

    private void ShowTheSelection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final CharSequence[] options = {"Select image from library", "New Picture", "Cancel"};
        final String[] option = {"Select image from library", "New Picture", "Cancel"};
        builder.setTitle("Select option").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals(option[1])) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, TAKE_CARMA);
                } else if (options[i].equals(option[0])) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICKED_IMAGE);
                } else if (options[i].equals(option[2])) {
                    dialogInterface.dismiss();
                }
            }
        }).show();
    }

    public void makeToast(String contextForToast, Context context) {
        if (contextForToast.equals("D")) {
            Log.i("TAG", "LOSS");
        } else {
            Toast.makeText(context, contextForToast, Toast.LENGTH_LONG).show();
        }


    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ShowTheSelection();
            ActivityCompat.requestPermissions(this, new String[]{permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ShowTheSelection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_CARMA &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }
        } else if (requestCode == PICKED_IMAGE) {
            if (data != null && data.getData() != null) {
                bitmap = null;
                image_uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                    imageView.setImageBitmap(bitmap);
                    if(bitmap != null){
                        text_Recognition(bitmap);
                     }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void text_Recognition(Bitmap bitmap){
        if (bitmap != null) {
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            Task<FirebaseVisionText> result = firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String text_list = "";
                    text.clear();
                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                        Rect box = block.getBoundingBox();
                        Point[] points = block.getCornerPoints();
                        text_Str = text_Str + block.getText();
                        // this part should be add an error checking such as if else to confirm the firebasevision text array 0 position not null, if not  there will be crash
                        text.add(block.getText());
                        if(text.get(0).length()>0 && text.size()>0) {

                            for(int i = 0; i<text.size(); i++) {
                                for(int x = 1; x<text.size();x++){
                                    if (!text.get(i).equals(text.get(x)))
                                        text_list = text_list + text.get(i) + "\n";
                                }
                            }
                            textView.setText(text_list);
                            book.setContent(text_list);

                            // show text in the image
                            updataToolBar();
                        }else{
                            makeToast("Unable to recognise the text from this image  ", context);

                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    makeToast("Unable to recognise the text from this image  ", context);

                }
            });
        } else {
            // if there not any text the app will show up a dialog to note user
            makeToast("Unable to recognise the text from this image  ", context);
        }
    }
    // this function is use for update the Add UI to Save UI
    private void updataToolBar(){
        re_add.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_24dp));
        re_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog_bk_title dialog_bk_title = new Dialog_bk_title();
                dialog_bk_title.show(getSupportFragmentManager(), "Dialog");

            }
        });
    }
    // get the book title from the dialog
    @Override
    public void getTextFromDialog(String title) {
            book.setTitle(title);
            upload(book);
    }

    public void upload(Book book){
        if (book!=null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Book");
            HashMap<String, String> hashMap = convertMashMap(book);
            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        makeToast("The Data was upload", context);
                        startActivity(new Intent(context, MainActivity.class));
                    }else{
                        makeToast("Wait for few minutes to upload", context);
                    }
                }
            });

        }
    }
    public HashMap convertMashMap(Book book){
        HashMap<String, String> hashMap = new HashMap<>();
        Book newBook = book;
        hashMap.put("Title", newBook.getTitle());
        hashMap.put("Content", newBook.getContent());
//        hashMap.put("UID", newBook.getUid());

        return hashMap;
    }


}
