package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import java.util.ArrayList;
import java.util.List;

// Milestones: Week 08
// All 3 screens display
// All 3 classes made, along with objects and current plans for functions stubbed
// Temporary buttons to view each of three screens

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<ClassActivity> classes [];
    }


    public void onClick(View v) {
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }

    private void selectClass(){

    }

    private void addClass(){

    }

    private void removeClass(){

    }

    public String addReminder(){
        String message = "Useless message.";

        return message;
    }

    public List searchAllNotes(){
        // Change <String> to whatever the list objects will be later
        return new ArrayList<String>();
    }

    public String Test1() {
        return "Feb 18, 2021";
    }

    public String Test2() {
        return "Enter Class Name";

    }

    public int Test3() {
        return 3;

    }

}