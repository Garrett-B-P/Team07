package com.example.team07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

//import java.util.ArrayList;
//import java.util.List;

// Milestones: Week 09
// Able to create separate classes and note files
// Classes and files display properly, including timestamps
// Enter in notes, but don't need to save information in long term

public class MainActivity extends AppCompatActivity {

    static List<ClassActivity> classes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView classList = (ListView) findViewById(R.id.classList);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, classes);

        classList.setAdapter(arrayAdapter);
        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
                intent.putExtra("ID", position);
                startActivity(intent);
            }
        });
    }

    // Function to save the list of classes with notes
    private void saveState(){
        SharedPreferences preferences;
    }


    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
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