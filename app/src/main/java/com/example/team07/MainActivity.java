package com.example.team07;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashSet;

// Milestones: Week 10
// Titles and content now searchable
// Notes and Class files are now actually saved and remembered

public class MainActivity extends AppCompatActivity {

    //Creates an array for the different classes, Garrett
    static ArrayList<String> classes = new ArrayList<String>();
    static ArrayAdapter arrayAdapter3;
    ListView listView;

    //When the add class button is hit it will create and start a new intent, Garrett
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates list of classes
        listView = findViewById(R.id.classList);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.course", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("course", null);

        arrayAdapter3 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, classes);

        listView.setAdapter(arrayAdapter3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When an item in the list view is clicked it will go to that intent, Garrett
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Going from MainActivity to ClassActivity
                Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
                intent.putExtra("classId", position);
                startActivity(intent);

                Log.i("MainOnItemClick", "Opening class #" + position);
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

    // Function for searchView3, will apply a filter to arrayAdapter3 so only items with matching text are displayed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Initialize search view
        SearchView searchView = findViewById(R.id.searchView3);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter array list of classes
                arrayAdapter3.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}