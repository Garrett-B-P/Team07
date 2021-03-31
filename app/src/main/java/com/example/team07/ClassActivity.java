package com.example.team07;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/************************************************************************************************
 * A class to facilitate the class page ui and handle transitioning to the note page. Will create
 * a directory of itself in the main directory when new class is made. Holds the titles for each
 * of the notes for listing purposes.
 ************************************************************************************************/
public class ClassActivity extends AppCompatActivity {
    // Member variables below are for use with app's directory
    static File currentDirectory;

    static ArrayList<String> notes_title = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    ListView listView;

    // Below is for runOnUiThread() from background thread
    private final Activity activity = this;

    public void addNote() {
        Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
        setNewNote(intent);
        startActivity(intent);
        Log.i("ClassOnClick", "Note Activity Opened");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        initSearchWidget();

        listView = findViewById(R.id.listView2);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, notes_title);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
            // Below will send Class directory's filepath to be used in ClassActivity
            setExistingNote(intent, i);
            startActivity(intent);

            Log.i("ClassOnItemClick", "Note #" + i + " opened");
        });

        //Deletes the notes if you press and hold on the note, Garrett
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            final int itemToDelete = i;
            // To delete the data from the App
            new AlertDialog.Builder(ClassActivity.this)
                    .setIcon(R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete " + findNote(itemToDelete).getName() + "?")
                    .setPositiveButton("Yes", (dialogInterface, i12) -> {
                        // Below is to delete the Note from the directory
                        deleteNote(itemToDelete);
                        arrayAdapter.notifyDataSetChanged();

                        Log.i("ClassOnItemOnLongClick", "Deleted note #" + itemToDelete);
                    }).setNegativeButton("No", null).show();
            return true;
        });

        //This section of code was supposed to create the different classes, Garrett
        EditText classTitle = findViewById(R.id.classTitle);
        Intent intent = getIntent();

        setUpClass(intent);

        classTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.classArrayAdapter.notifyDataSetChanged();

                renameDir(String.valueOf(charSequence));

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // add your code here
            }
        });

        FloatingActionButton newNote = findViewById(R.id.addNote);
        newNote.setOnClickListener(v -> addNote());

    }

    public void initSearchWidget() {
        SearchView searchView = findViewById(R.id.searchView2);
        ArrayList<String> filteredClasses = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // reason for if/else statement: if newText is empty, switch back to normal adapter
                // I set runOnUiThreads for the ArrayAdapters in case that counts as a UI change
                Thread thread = new Thread(() -> {
                    if (!newText.isEmpty()) {
                        filteredClasses.clear();

                        for (int i = 0; i < notes_title.size(); i++){
                            String aClass = notes_title.get(i);

                            if (aClass.toLowerCase().contains(newText.toLowerCase())) {
                                filteredClasses.add(aClass);
                            }
                        }
                        activity.runOnUiThread(() -> {
                            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, filteredClasses);
                            listView.setAdapter(arrayAdapter);
                        });
                    } else {
                        resetNotes();
                        activity.runOnUiThread(() -> {
                            arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, notes_title);
                            listView.setAdapter(arrayAdapter);
                        });
                    }


                });
                thread.start();
                return false;
            }
        });
        filteredClasses.clear();
    }

    // Function for searchView2, will apply a filter to arrayAdapter2 so only items with matching text are displayed
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Initialize search view
        SearchView searchView = findViewById(R.id.searchView2);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter array list of classes
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }*/
    // Below are functions to call for app's directory use

    /******************************************************************
     * A function to set up ClassActivity with necessary information
     * @param i The current intent from MainActivity
     ******************************************************************/
    public void setUpClass(Intent i) {
        Thread thread = new Thread(() -> {
            String filePath = getClassPath(i);
            Log.d("ClassActivity", "setUpClass: filePath has been received");
            currentDirectory = new File(filePath);
            activity.runOnUiThread(() -> {
                EditText classTitle = findViewById(R.id.classTitle);
                Log.d("ClassActivity", "setUpClass: currentDirectory has been set");
                classTitle.setText(currentDirectory.getName());
                Log.d("ClassActivity", "setUpClass: classTitle has been set");
            });
            resetNotes();
            Log.d("ClassActivity", "setUpClass: noteList has been set and filled");
            //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, noteList);
            Log.d("ClassActivity", "setUpClass: ArrayAdapter has been set to noteList");

        });
        thread.start();
    }

    /********************************************************************************
     * A function to find or make the path of the directory ClassActivity will use
     * @param i The intent that will be tested which path it has
     * @return The string holding the filepath ClassActivity will use
     ********************************************************************************/
    public String getClassPath(Intent i) {
        String filePath = i.getStringExtra("filePath");
        String parentPath = i.getStringExtra("parentPath");
        if (filePath != null) {
            Log.d("ClassActivity", "getClassPath: sending an existing file path");
            return filePath;
        } else {
            Log.d("ClassActivity", "getClassPath: generating a new file path");
            File parent = new File(parentPath);
            String newName = generateClassTitle("Untitled", parent);
            Log.d("ClassActivity", "getClassPath: title of new file is " + newName);
            File newClass = new File(parentPath, newName);
            Log.d("ClassActivity", "getClassPath: generated a new File");
            makeNewDir(newClass.getName());
            return newClass.getPath();
        }
    }

    /****************************************************************************************
     * A function to update the intent to prepare to make a new note
     * @param i The current intent
     ****************************************************************************************/
    public void setNewNote(Intent i) {
        Log.d("ClassActivity", "setNewNote: parentPath is readying to send");
        i.putExtra("parentPath", currentDirectory.toString());
    }

    /****************************************************************************************
     * A function to update the intent to the selected note and load the note's information
     * @param i The current intent
     * @param place The position of the Note in the directory
     ****************************************************************************************/
    public void setExistingNote(Intent i, int place) {
        Log.d("ClassActivity", "setExistingNote: filePath is readying to send");
        File toSend = findNote(place);
        i.putExtra("filePath", toSend.toString());
        setNewNote(i);
    }

    /********************************************
     * A function to make a directory
     * @param title The new directory's title
     ********************************************/
    public void makeNewDir(String title) {
        Thread thread = new Thread(() -> {
            File newDir = new File(getApplicationContext().getFilesDir(), title);
            boolean answer = newDir.mkdir();
            if (answer) {
                Log.d("ClassActivity", "makeNewDir: " + title + " directory made");
            } else {
                Log.d("ClassActivity", "makeNewDir: " + title + " directory already exists");
            }
            currentDirectory = newDir;
            Log.d("ClassActivity", "makeNewDir: currentDirectory has been set");
        });
        thread.start();
    }

    /******************************************************
     * A function to rename the current directory
     * @param newName The directory's possibly new title
     ******************************************************/
    public void renameDir(String newName) {
        Thread thread = new Thread(() -> {
            if (!currentDirectory.getName().equals(newName)) {
                Log.d("ClassActivity", "renameDir: currentDirectory will now be renamed");
                String checkedName = generateClassTitle(newName, getApplicationContext().getFilesDir());
                File newDirName = new File(currentDirectory.getParent(), checkedName);
                currentDirectory.renameTo(newDirName);
                currentDirectory = newDirName;
                Log.d("ClassActivity", "renameDir: currentDirectory has been renamed");
            }
        });
        thread.start();
    }

    /*********************************************************
     * A function used to delete notes
     * @param place The position the note we're deleting
     *********************************************************/
    public void deleteNote(int place) {
        Thread thread = new Thread(() -> {
            Log.d("ClassActivity", "deleteNote: Note number " + place + " will now be deleted");
            findNote(place).delete();
            resetNotes();
            Log.d("ClassActivity", "deleteNote: noteList has been reset");
        });
        thread.start();
    }

    /**************************************************************************************
     * A function used to check if a Class exists in this directory to not overwrite it
     * @param name The original name we're using
     * @param parent This class's parent directory
     * @return The name that may or may not have a number added to the end
     **************************************************************************************/
    public String generateClassTitle(String name, File parent) {
        // Accessing variable "name" within a new thread throws errors, so no multithreading here
        boolean answer = false;
        int y = 0;
        String newName = "";
        if (name.equals("")) {
            Log.d("ClassActivity", "generateClassTitle: name was empty");
            name = "Untitled";
        }
        Log.d("ClassActivity", "generateClassTitle: about to test if name exists");
        for (int x = 0; x< Objects.requireNonNull(parent.listFiles()).length; x++) {
            if (name.equals(Objects.requireNonNull(parent.listFiles())[x].getName())) {
                // If the name is in the directory
                answer = true;
            }
        }
        if (!answer) {
            Log.d("ClassActivity", "generateClassTitle: returning name " + name);
            return name;
        }
        Log.d("ClassActivity", "generateClassTitle: does " + name + " title exist in its parent directory? ");
        while (answer) {
            newName = name + y;
            // Append the number to the name
            answer = false;
            for (int z = 0; z< Objects.requireNonNull(parent.listFiles()).length; z++) {
                Log.d("ClassActivity", "generateClassTitle: testing if " + newName + " exists in this directory");
                if (newName.equals(Objects.requireNonNull(parent.listFiles())[z].getName())) {
                    // If the new name is in the directory
                    Log.d("ClassActivity", "generateClassTitle: new name " + newName + " is in this directory");
                    answer = true;
                }
            }
            Log.d("ClassActivity", "generateClassTitle: new name " + newName + " has been generated");
            y++;
        }
        Log.d("ClassActivity", "generateClassTitle: returning new name " + newName);
        return newName;
    }

    /**
     * Refreshes class's note list
     */
    public static void refreshNoteList() {
        // Can't use non-static variable "activity" here for runOnUiThread
        File[] fileList = currentDirectory.listFiles();
        notes_title.clear();
        for (int x = 0; x< Objects.requireNonNull(fileList).length; x++) {
            notes_title.add(fileList[x].getName());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * Find specific note in case of SearchView use
     * @param position The position of the file in its list
     * @return The file to send to other functions
     */
    public File findNote(int position) {
        // finding a file in a thread to return out of the thread requires some sort of gymnastics
        String name = listView.getItemAtPosition(position).toString();
        for (File f: Objects.requireNonNull(currentDirectory.listFiles())) {
            if (name.equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    /**
     * Reset notes list for display
     */
    public void resetNotes() {
        Thread thread = new Thread(() -> {
            notes_title.clear();
            File[] fileList = currentDirectory.listFiles();
            for (int x = 0; x< Objects.requireNonNull(fileList).length; x++) {
                notes_title.add(fileList[x].getName());
            }
        });
        thread.start();
    }
}