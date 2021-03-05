package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;


public class NotesActivity extends AppCompatActivity implements Comparable<NotesActivity> {
    int noteId;
    int bodyId;
    Calendar createdDate;
    // createdDate might never be shown, but can be sorted by in the future
    Calendar lastEdit;


    public void onClick(View v) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet(ClassActivity.notes_title);
        sharedPreferences.edit().putStringSet("notes", set).apply();

        String message = "Note Saved";
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        TextView time = findViewById(R.id.time);

        createdDate = Calendar.getInstance();
        lastEdit = Calendar.getInstance();
        String timeStamp = java.text.DateFormat.getDateTimeInstance().format(lastEdit.getTime());
        time.setText(timeStamp);


//Attempt 1 on saving notes


        EditText noteTitle = findViewById(R.id.noteTitle);
        EditText noteBody = findViewById(R.id.noteBody);

        Intent intent = getIntent();

        noteId = intent.getIntExtra("noteId", -1);

        if (noteId != -1) {
            noteTitle.setText(ClassActivity.notes_title.get(noteId));

        } else {
            ClassActivity.notes_title.add("");
            noteId = ClassActivity.notes_title.size() - 1;
            ClassActivity.arrayAdapter.notifyDataSetChanged();
        }

        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ClassActivity.notes_title.set(noteId, String.valueOf(charSequence));
                ClassActivity.arrayAdapter.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet(ClassActivity.notes_title);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //add code here
            }
        });

        noteBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ClassActivity.notes_body.set(bodyId, String.valueOf(charSequence));
                ClassActivity.arrayAdapter2.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet(ClassActivity.notes_body);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //add code here
            }
        });

    }

    // compareTo() is needed for Comparable<NotesActivity>
    @Override
    public int compareTo(NotesActivity o) {
        return 0;
    }

    /*
    // To make other comparisons: Collections.sort(ListName, NotesActivity.byCreate);
    public static Comparator<NotesActivity> byCreate = new Comparator<NotesActivity>() {
        @Override
        public int compare(NotesActivity o1, NotesActivity o2) {
            //return (o1.createDateVar.compareTo(o2.createDateVar);
            return 0;
        }
    };
     */
}
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



