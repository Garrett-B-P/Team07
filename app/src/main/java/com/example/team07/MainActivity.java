package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

//import java.util.ArrayList;
//import java.util.List;

// Milestones: Week 09
// Able to create separate classes and note files
// Classes and files display properly, including timestamps
// Enter in notes, but don't need to save information in long term

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //List<ClassActivity> classes = new ArrayList<>();

    // Function to save the list of classes with notes
    private void saveState(){
        SharedPreferences preferences;
    }


    public void onClick(View v) {
        Intent intent = new Intent(this, ClassActivity.class);
        startActivity(intent);
    }
/*
    private void selectClass(){

    }

    private void addClass(){

    }

    private void removeClass(){

    }

    public String addReminder(){

        return "Useless message.";
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
*/
}