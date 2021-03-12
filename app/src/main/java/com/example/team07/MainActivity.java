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

import java.io.File;
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
    // Member variables below are for use with app's directory
    static File mainDirectory;
    public String[] classList;

    //When the add class button is hit it will create and start a new intent, Garrett
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
        // Below will send parent's path to new Class instead of the Class's filepath
        //setNewClass(intent);
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

        // This function is for use with app's directory
        //setUpMain();

        arrayAdapter3 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, classes);

        listView.setAdapter(arrayAdapter3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When an item in the list view is clicked it will go to that intent, Garrett
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Going from MainActivity to ClassActivity
                Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
                intent.putExtra("classId", position);
                // Below will send Class directory's filepath to be used in ClassActivity
                //setExistingClass(intent, position);
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
                        // To delete the class from the directory
                        //deleteClass(itemToDelete);
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
    // Below are functions to call for app's directory use
    public void setUpMain() {
        mainDirectory = getApplicationContext().getFilesDir();
        File[] fileList = mainDirectory.listFiles();
        classList = new String[fileList.length];
        for (int i=0; i<fileList.length; i++) {
            classList[i] = fileList[i].getName();
        }
        arrayAdapter3 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, classList);
    }
    public Intent setNewClass(Intent i) {
        return i.putExtra("parentPath", mainDirectory.toString());
    }
    public Intent setExistingClass(Intent i, int place) {
        return i.putExtra("filePath", mainDirectory.listFiles()[place].toString());
    }
    public void deleteClass(int place) {
        // A directory with items inside it cannot be deleted, so contents will be deleted first
        File[] fullList = mainDirectory.listFiles();
        File[] toDelete = fullList[place].listFiles();
        if (toDelete.length > 0) {
            Log.d("MainActivity", "Directory to be deleted has items inside");
            for (int x=0; x<toDelete.length; x++) {
                toDelete[x].delete();
            }
        }
        fullList[place].delete();
        File[] newList = mainDirectory.listFiles();
        classList = new String[newList.length];
        for (int i=0; i<newList.length; i++) {
            classList[i] = newList[i].getName();
        }

    }
}