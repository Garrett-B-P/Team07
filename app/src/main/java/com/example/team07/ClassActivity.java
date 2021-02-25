package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ClassActivity extends AppCompatActivity {

    EditText classTitle = findViewById(R.id.classTitle);
    List notes = new ArrayList<NotesActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
    }

    private void addNote(){

    }

    private void deleteNote(){

    }

    private void editNote(){

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
}