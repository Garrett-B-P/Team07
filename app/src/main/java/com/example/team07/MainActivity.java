package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

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

    }

    public String Test1() {
        String date = "Feb 18, 2021";
        return date;
    }

    public String Test2() {
        String def = "Enter Class Name";
        return def;

    }

    public int Test3() {
        int classNum = 3;
        return classNum;

    }

}