package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Button;

import java.util.ArrayList;
/*
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
*/

public class ClassActivity extends AppCompatActivity {
/*
    EditText classTitle = findViewById(R.id.classTitle);
    List notes = new ArrayList<NotesActivity>();
*/
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
    }


    public void onClick(View v) {
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }

    /*
    private void addNote(){

    }

    private void deleteNote(){

    }

    private void editNote(View view){
        Intent intent = new Intent(this, NotesActivity.class);
        String title = "Notes Title";
        intent.putExtra("Notes Title", title);
        startActivity(intent);
    }

    public void sortNotes(){

    }

    public String getClassTitle(){
        return classTitle.getText().toString();
    }

    public List searchClassNotes(){
        // Change <String> to whatever the list objects will be later
        ArrayList<String> searchList = new ArrayList<>();
        return searchList;
    }
     */
}