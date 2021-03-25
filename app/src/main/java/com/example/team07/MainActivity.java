package com.example.team07;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Milestones: Week 12 - Work on stretch goals and polish/test core features
// From Week 11: get camera pictures to automatically upload to their NotesActivity
// Reminders and due dates work in tandem with system calendar
// Sort classes/notes by dates edited/created or by title
// Further refine GUI to be more user-friendly
// Work out bugs that may have been uncovered

/**********************************************************************************************
 * A class to implement the main page ui. Creates/reopens the main directory where all the
 * information for the app will be stored. Stores the titles for each class as well for viewing
 * purposes.
 **********************************************************************************************/
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private Spinner spinnerSort;

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

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initSearchWidget();

        spinnerSort = findViewById(R.id.spinner);
        spinnerSort.setOnItemSelectedListener(this);

        String[] searchOptions = getResources().getStringArray(R.array.searchOptions);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, searchOptions);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);



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
                    .setMessage("Do you want to delete " + findClass(itemToDelete).getName() + "?")
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

    /*************************************************************************************************
     * Widget to control what is displayed when search bar is being used. Creates new list of filtered
     * items matching the search query. Also refreshes the list of classes if a new class is created
     * while the search is initiated.
     *************************************************************************************************/
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
                // reason for if/else statement: if newText is empty, switch back to normal adapter
                if (!newText.isEmpty()) {
                    filteredClasses.clear();

                    for (int i = 0; i < classes.size(); i++){
                        String aClass = classes.get(i);

                        if (aClass.toLowerCase().contains(newText.toLowerCase())) {
                            filteredClasses.add(aClass);
                        }
                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, filteredClasses);
                    listView.setAdapter(arrayAdapter);
                } else {
                    resetClasses();
                    listView.setAdapter(arrayAdapter3);
                }

                return false;
            }
        });
        filteredClasses.clear();
    }

    /****************************************************************************************
     * Function that contains clickListener for the listView. Calls setExistingClass function
     * to setup the intent with the proper information before starting the class activity.
     ****************************************************************************************/
    public void clickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When an item in the list view is clicked it will go to that intent, Garrett
            //@RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MainActivity", "listView.onItemClick: getItemAtPosition is " + listView.getItemAtPosition(position));
                Log.i("MainActivity", "listView.onItemClick: Item is " + listView.getItemAtPosition(position).toString());
                // Going from MainActivity to ClassActivity
                Intent intent = new Intent(MainActivity.this, ClassActivity.class);
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
        resetClasses();
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
    //@RequiresApi(api = Build.VERSION_CODES.N)
    public Intent setExistingClass(Intent i, int place) {
        Log.d("MainActivity", "setExistingClass: filePath is readying to send");
        File toSend = findClass(place);
        //return i.putExtra("filePath", mainDirectory.listFiles()[place].toString());
        return i.putExtra("filePath", toSend.toString());

    }

    /*****************************************************************
     * A function used to delete directories and their contents
     * @param place The position the directory we're deleting
     *****************************************************************/
    //@RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteClass(int place) {
        // A directory with items inside it cannot be deleted, so contents will be deleted first
        File toDel = findClass(place);
        //File[] fullList = mainDirectory.listFiles();
        //File[] toDelete = fullList[place].listFiles();
        File[] toDelete = toDel.listFiles();
        Log.d("MainActivity", "deleteClass: Directory " + place + " will now be deleted");
        if (toDelete.length > 0) {
            Log.d("MainActivity", "deleteClass: This directory has items inside");
            for (int x=0; x<toDelete.length; x++) {
                boolean itemDelete = toDelete[x].delete();
                if (itemDelete) {
                    Log.d("MainActivity", "deleteClass: Directory item " + x + " has been deleted");
                } else {
                    Log.d("MainActivity", "deleteClass: Directory item " + x + " failed to delete");
                }
            }
        }
        //Boolean classDelete = fullList[place].delete();
        boolean classDelete = toDel.delete();
        if (classDelete) {
            Log.d("MainActivity", "deleteClass: Directory has been deleted");
        } else {
            Log.d("MainActivity", "deleteClass: Directory was not deleted. There may be items within");
        }
        resetClasses();
        Log.d("MainActivity", "deleteClass: classList has been reset");
    }

    /**
     * Find specific directory in case of SearchView use
     * @param position The position of directory in its list
     * @return the directory File to send to other functions
     */
    //@RequiresApi(api = Build.VERSION_CODES.N)
    public File findClass(int position) {
        String name = listView.getItemAtPosition(position).toString();
        Log.i("MainActivity", "findClass: Item is " + listView.getItemAtPosition(position).toString());
        for (File f:mainDirectory.listFiles()) {
            if (name.equals(f.getName())) {
                return f;
            }
        }
        return null;
        /*
        SearchView searchVal = findViewById(R.id.searchView3);
        //Spinner spinner = findViewById(R.id.spinner);
        File foundFile = null;
        if (!searchVal.getQuery().toString().isEmpty()) {
            // get filtered list of classes
            List<String> searchList = filterClasses(classes, searchVal.getQuery().toString());
            // find what filename is being selected
            String fileName = searchList.get(position);
            for (File f:mainDirectory.listFiles()) {
                if (fileName.equals(f.getName())) {
                    // if the names match, we've found our file
                    foundFile = f;
                }
            }
        } else if (spinnerSort.getSelectedItemPosition() != 0) {
            System.out.print("getSelectedItem: " + spinnerSort.getSelectedItem());
            Log.i("MainActivity", "findClass: getSelectedItem is " + spinnerSort.getSelectedItem());
            ArrayList<File> sortList = new ArrayList<>();
            File[] classList = mainDirectory.listFiles();
            for (File f:classList) {
                sortList.add(f);
            }
            switch (spinnerSort.getSelectedItemPosition()) {
                case 1:
                    Collections.sort(sortList, NotesActivity.lastEdit.reversed());
                    break;
                case 2:
                    Collections.sort(sortList, NotesActivity.lastEdit);
                    break;
                case 3:
                    Collections.sort(sortList, NotesActivity.title);
                    break;
                case 4:
                    Collections.sort(sortList, NotesActivity.title.reversed());
                    break;
            }
            System.out.print("Found " + sortList.get(position).getName());
            foundFile = sortList.get(position);
        } else {
            // if not searching for anything
            foundFile = mainDirectory.listFiles()[position];
        }
        return foundFile;

         */
    }

    /************************************************
     * Filters a list of classes for applicable names
     * @param classList list to be filtered
     * @param searchVal string to search for
     * @return list of applicable class names
     ************************************************/
    public List<String> filterClasses(List<String> classList, String searchVal) {
        List<String> newList = new ArrayList<>();
        for (String x:classList) {
            if (x.toLowerCase().contains(searchVal.toLowerCase())) {
                newList.add(x);
            }
        }

        return newList;
    }

    /**
     * Reset classes list for display
     */
    public void resetClasses() {
        File[] fileList = mainDirectory.listFiles();
        classes.clear();
        for (File file : fileList) {
            classes.add(file.getName());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();

            File[] x = mainDirectory.listFiles();
            ArrayList<File> fileList = new ArrayList<>();
            for (File f: x) {
                fileList.add(f);
            }
            switch (position) {
                case 1: {
                    Toast.makeText(MainActivity.this, valueFromSpinner, Toast.LENGTH_SHORT).show();
                    Collections.sort(fileList, NotesActivity.lastEdit.reversed());
                    break;
                }
                case 2: {
                    Toast.makeText(MainActivity.this, valueFromSpinner, Toast.LENGTH_SHORT).show();
                    Collections.sort(fileList, NotesActivity.lastEdit);
                    break;
                }
                case 3: {
                    Toast.makeText(MainActivity.this, valueFromSpinner, Toast.LENGTH_SHORT).show();
                    Collections.sort(fileList);
                    break;
                }
                case 4: {
                    Toast.makeText(MainActivity.this, valueFromSpinner, Toast.LENGTH_SHORT).show();
                    Collections.sort(fileList, Collections.reverseOrder());
                    break;
                }
            }

            classes.clear();

            for (File f: fileList) {
                classes.add(f.getName());
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, classes);
            listView.setAdapter(arrayAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}