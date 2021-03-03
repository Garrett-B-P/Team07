package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.HashSet;
/*
import android.widget.EditText;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
*/

public class NotesActivity extends AppCompatActivity {
    int noteId;
   /*
    EditText noteTitle = findViewById(R.id.noteTitle);
    EditText notes = findViewById(R.id.noteBody);
    Calendar dateCreated;
    Calendar lastEdited;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

/* Attempt 1 on saving notes


        EditText note = findViewById(R.id.noteBody);

        Intent intent = getIntent();

        noteId = intent.getIntExtra("noteId", -1);
        if (noteId != 1) {
            note.setText(ClassActivity.notes.get(noteId));
        } else {
            ClassActivity.notes.add("");
            noteId = ClassActivity.notes.size() - 1;
            ClassActivity.arrayAdapter.notifyDataSetChanged();
        }

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ClassActivity.notes.set(noteId, String.valueOf(charSequence));
                ClassActivity.arrayAdapter.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet(ClassActivity.notes);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            //add code here
            }
            });

            //dateCreated = Calendar.getInstance();

/*
    private void writeNote(){
        lastEdited = Calendar.getInstance();
    }

    public void upload(){

    }

    public void takePic(){

    }

    public List searchNote(){
        // Change <String> to whatever the list objects will be later
        ArrayList<String> searchList = new ArrayList<>();
        return searchList;
    }
    */

    }
}