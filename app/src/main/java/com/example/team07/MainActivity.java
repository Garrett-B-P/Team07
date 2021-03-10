package com.example.team07;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

//import java.util.ArrayList;
//import java.util.List;

// Milestones: Week 10
// Titles and content now searchable
// Notes and Class files are now actually saved and remembered

public class MainActivity extends AppCompatActivity {

    //Creates an array for the different classes, Garrett
    static ArrayList<String> classes = new ArrayList<String>();
    static ArrayAdapter arrayAdapter3;

    //When the add class button is hit it will create a new intent, Garrett
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates list of classes
        ListView listView = findViewById(R.id.classList);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.course", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("course", null);



        arrayAdapter3 = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, classes);

        listView.setAdapter(arrayAdapter3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When an item in the list view is clicked it will go to that intent, Garrett
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Going from MainActivity to ClassActivity
                Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
                intent.putExtra("classId", i);
                startActivity(intent);

                Log.i("MainOnItemClick", "Opening class #" + i);
            }
        });

        //deletes a class when you press and hold on it, Garrett
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            final int itemToDelete = i;
            // To delete the data from the App
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete " + classes.get(itemToDelete) + "?")
                    .setPositiveButton("Yes", (dialogInterface, i1) -> {
                        classes.remove(itemToDelete);
                        arrayAdapter3.notifyDataSetChanged();
                        SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                        HashSet set1 = new HashSet(MainActivity.classes);
                        sharedPreferences1.edit().putStringSet("notes", set1).apply();

                        Log.i("ClassOnItemOnLongClick", "Deleted note #" + itemToDelete);
                    }).setNegativeButton("No", null).show();

            return true;

        });

    }

    //List<ClassActivity> classes = new ArrayList<>();
/*
    // Function to save the list of classes with notes
    private void saveState(){
        SharedPreferences preferences;
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