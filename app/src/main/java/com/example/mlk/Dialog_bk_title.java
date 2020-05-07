package com.example.mlk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog_bk_title extends AppCompatDialogFragment {
    EditText title;
    private DialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_book, null);
        builder.setView(view).setTitle("Please Enter the Book title").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String bk_title = title.getText().toString().trim();
                listener.getTextFromDialog(bk_title);
            }
        });
        title = view.findViewById(R.id.dialog_title);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (DialogListener) context;

        } catch (ClassCastException e) {
           throw new ClassCastException( context.toString() + "must implement dialog");
        }
    }

    public interface DialogListener{
        void getTextFromDialog(String title);
    }
}
