package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    String noteTitle;
    String notes;
    Calendar dateCreated;
    Calendar lastEdited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dateCreated = Calendar.getInstance();
    }

    private void writeNote(){
        lastEdited = Calendar.getInstance();
    }

    public void upload(){

    }

    public void takePic(){

    }

    public List<String> searchNote(){
        List<String> results = new ArrayList<>();

        return results;
    }
}