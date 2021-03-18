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

// Milestones: Week 11
// From Week 10: Titles and content now searchable
// Camera now accessible and automatically uploads picture taken
// Pictures and pdfs able to be uploaded to notes/class

/**********************************************************************************************
 * A class to implement the main page ui. Creates/reopens the main directory where all the
 * information for the app will be stored. Stores the titles for each class as well for viewing
 * purposes.
 **********************************************************************************************/
public class MainActivity extends AppCompatActivity {

    //Creates an array for the different classes, Garrett
    static ArrayList<String> classes = new ArrayList<>();
    static ArrayAdapter arrayAdapter3;
    ListView listView;
    // Member variables below are for use with app's directory
    static File mainDirectory;

    //When the add class button is hit it will create and start a new intent, Garrett
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
        // Below will send parent's path to new Class instead of the Class's filepath
        setNewClass(intent);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initSearchWidget();

        // Creates list of classes
        listView = findViewById(R.id.classList);
        //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.course", Context.MODE_PRIVATE);
        //HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("course", null);

        // This function is for use with app's directory
        setUpMain();

        arrayAdapter3 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, classes);

        listView.setAdapter(arrayAdapter3);
        clickListener();


        //deletes a class when you press and hold on it, Garrett
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            final int itemToDelete = i;
            // To delete the data from the App
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete " + classes.get(itemToDelete) + "?")
                    .setPositiveButton("Yes", (dialogInterface, i1) -> {
                        //classes.remove(itemToDelete);
                        // To delete the class from the directory
                        deleteClass(itemToDelete);
                        arrayAdapter3.notifyDataSetChanged();
                        //SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                        //HashSet set1 = new HashSet(MainActivity.classes);
                        //sharedPreferences1.edit().putStringSet("notes", set1).apply();

                        Log.i("ClassOnItemOnLongClick", "Deleted note #" + itemToDelete);
                    }).setNegativeButton("No", null).show();

            return true;

        });

    }

    // Function for searchView3, will apply a filter to arrayAdapter3 so only items with matching text are displayed

    /**********************************************************************************
     * Used to implement the search bar and apply a filter on the arrayAdapter3 to only
     * display searched items.
     *
     * @return Auto generated return statement
     **********************************************************************************/
   /* @Override
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
    }*/

    public void initSearchWidget() {
        SearchView searchView = findViewById(R.id.searchView3);
        ArrayList<String> filteredClasses = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                for (int i = 0; i < classes.size(); i++){
                    String aClass = classes.get(i);

                    if (aClass.toLowerCase().contains(newText.toLowerCase())) {
                        filteredClasses.add(aClass);
                    }
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, filteredClasses);
                listView.setAdapter(arrayAdapter);

                return false;
            }
        });
    }

    public void clickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When an item in the list view is clicked it will go to that intent, Garrett
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Going from MainActivity to ClassActivity
                Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
                //intent.putExtra("classId", position);
                // Below will send Class directory's filepath to be used in ClassActivity
                setExistingClass(intent, position);
                startActivity(intent);

                Log.i("MainOnItemClick", "Opening class #" + position);
            }
        });
    }

    // Below are functions to call for app's directory use

    /******************************************************************
     * A function to set up MainActivity with necessary information
     ******************************************************************/
    public void setUpMain() {
        mainDirectory = getApplicationContext().getFilesDir();
        Log.d("MainActivity", "setUpMain: mainDirectory has been set");
        File[] fileList = mainDirectory.listFiles();
        classes.clear();
        for (int i=0; i<fileList.length; i++) {
            classes.add(fileList[i].getName());
        }
        Log.d("MainActivity", "setUpMain: classList has been set and filled");
        //arrayAdapter3 = new ArrayAdapter(MainActivity.this, android.R.layout.simple_expandable_list_item_1, classList);
        Log.d("MainActivity", "setUpMain: ArrayAdapter has been set to classList");
    }

    /****************************************************************************************
     * A function to update the intent to prepare to make a new class
     * @param i The current intent
     * @return Updated intent with the parent directory of the new class
     ****************************************************************************************/
    public Intent setNewClass(Intent i) {
        Log.d("MainActivity", "setNewClass: parentPath is readying to send");
        return i.putExtra("parentPath", mainDirectory.toString());
    }

    /****************************************************************************************
     * A function to update the intent to the selected class and load the class's information
     * @param i The current intent
     * @param place The position of the Class in the directory
     * @return Updated intent with the current class's information
     ****************************************************************************************/
    public Intent setExistingClass(Intent i, int place) {
        Log.d("MainActivity", "setExistingClass: filePath is readying to send");
        return i.putExtra("filePath", mainDirectory.listFiles()[place].toString());
    }

    /*****************************************************************
     * A function used to delete directories and their contents
     * @param place The position the directory we're deleting
     *****************************************************************/
    public void deleteClass(int place) {
        // A directory with items inside it cannot be deleted, so contents will be deleted first
        File[] fullList = mainDirectory.listFiles();
        File[] toDelete = fullList[place].listFiles();
        Log.d("MainActivity", "deleteClass: Directory " + place + " will now be deleted");
        if (toDelete.length > 0) {
            Log.d("MainActivity", "deleteClass: This directory has items inside");
            for (int x=0; x<toDelete.length; x++) {
                Boolean itemDelete = toDelete[x].delete();
                if (itemDelete) {
                    Log.d("MainActivity", "deleteClass: Directory item " + x + " has been deleted");
                } else {
                    Log.d("MainActivity", "deleteClass: Directory item " + x + " failed to delete");
                }
            }
        }
        Boolean classDelete = fullList[place].delete();
        if (classDelete) {
            Log.d("MainActivity", "deleteClass: Directory has been deleted");
        } else {
            Log.d("MainActivity", "deleteClass: Directory was not deleted. There may be items within");
        }
        File[] newList = mainDirectory.listFiles();
        classes.clear();
        for (int i=0; i<newList.length; i++) {
            classes.add(newList[i].getName());
        }
        Log.d("MainActivity", "deleteClass: classList has been reset");
    }
}