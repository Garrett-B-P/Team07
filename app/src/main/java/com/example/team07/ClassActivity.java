package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ClassActivity extends AppCompatActivity {

    String title;
    List notes = new ArrayList<NotesActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        title = "unchangedTitle";
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
        return title;
    }

    public List searchClassNotes(){

    }
}