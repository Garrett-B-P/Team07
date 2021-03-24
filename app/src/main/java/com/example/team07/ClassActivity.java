package com.example.team07;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/************************************************************************************************
 * A class to facilitate the class page ui and handle transitioning to the note page. Will create
 * a directory of itself in the main directory when new class is made. Holds the titles for each
 * of the notes for listing purposes.
 ************************************************************************************************/
public class ClassActivity extends AppCompatActivity {
    //int classId;
    // Member variables below are for use with app's directory
    static File currentDirectory;

    //Creates Arrays that hold the note information, Garrett
    static ArrayList<String> notes_title = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    ListView listView;

    //Creates an intent for the NotesActivity and sends some information to the activity, Garrett
    public void onClick(View v) {
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
        //I believe this saves the note, Garrett
        //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        //HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        //Creates an example note if there are no notes, Garrett
        /*
        if (set == null) {
            notes_title.add("Example note");
        } else {
            notes_title = new ArrayList(set);
        }
         */

        //Adds the created notes to the array adapter, Garrett
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, notes_title);

        listView.setAdapter(arrayAdapter);

        //When a note is clicked on it will go to that note and passes in the info, Garrett
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                //intent.putExtra("noteId", i);
                // Below will send Class directory's filepath to be used in ClassActivity
                setExistingNote(intent, i);
                startActivity(intent);

                Log.i("ClassOnItemClick", "Note #" + i + " opened");
            }
        });

        //Deletes the notes if you press and hold on the note, Garrett
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int itemToDelete = i;
                // To delete the data from the App
                new AlertDialog.Builder(ClassActivity.this)
                        .setIcon(R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete " + findNote(itemToDelete).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //notes_title.remove(itemToDelete);
                                // Below is to delete the Note from the directory
                                deleteNote(itemToDelete);
                                arrayAdapter.notifyDataSetChanged();
                                //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                                //HashSet<String> set = new HashSet(ClassActivity.notes_title);
                                //sharedPreferences.edit().putStringSet("notes", set).apply();

                                Log.i("ClassOnItemOnLongClick", "Deleted note #" + itemToDelete);
                            }
                        }).setNegativeButton("No", null).show();
                return true;
            }
        });

        //This section of code was supposed to create the different classes, Garrett
        EditText classTitle = findViewById(R.id.classTitle);
        Intent intent = getIntent();

        //classId = intent.getIntExtra("classId", -1);

        /*
        if (classId != -1) {
            classTitle.setText(MainActivity.classes.get(classId));
        } else {
            MainActivity.classes.add("");
            classId = MainActivity.classes.size() - 1;
            MainActivity.arrayAdapter3.notifyDataSetChanged();

            Log.i("ClassCreate", "Created Class #" + classId);
        }
         */

        // This custom function is for use with an app's directory
        // PLEASE NOTE, FELLOW PROGRAMMERS!! I'd made my program ask for a name in MainActivity for the new ClassActivity,
        // but don't quite know how to program it here, since the text box for new Class/Note are in ClassActivity/NoteActivity
        // instead of their "parent" activities. I'll do what I can
        setUpClass(intent);

        classTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.arrayAdapter3.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                //MainActivity.classes.set(classId, String.valueOf(charSequence));
                //MainActivity.arrayAdapter3.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                //HashSet set = new HashSet(MainActivity.classes);
                //sharedPreferences.edit().putStringSet("classes", set).apply();

                // I know it's risky, but we gotta try it
                renameDir(String.valueOf(charSequence));

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // add your code here

                // From Cajsa: I want to add code to make directories here, since the name changes here
                // I'll probably add code to test if directory exists, then if true, use the renameTo() option,
                // but if false, mkdir()
                // I was going to put it in onTextChanged() above, but that'd renameTo() and mkdir()
                // every time the text changes, which I'd imagine is BAD
                // How do I compare before it changed to after it changed? I need that to see if I can
                // search for the directory, rename it, etc.

                // To change directory's name:
                //renameDir(newName);

                // NOTE: I don't know how to get the new name from the Editable
            }
        });


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
                if (!newText.isEmpty()) {
                    filteredClasses.clear();

                    for (int i = 0; i < notes_title.size(); i++){
                        String aClass = notes_title.get(i);

                        if (aClass.toLowerCase().contains(newText.toLowerCase())) {
                            filteredClasses.add(aClass);
                        }
                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, filteredClasses);
                    listView.setAdapter(arrayAdapter);
                } else {
                    resetNotes();
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, notes_title);
                    listView.setAdapter(arrayAdapter);
                }

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
        String filePath = getClassPath(i);
        Log.d("ClassActivity", "setUpClass: filePath has been received");
        EditText classTitle = findViewById(R.id.classTitle);
        currentDirectory = new File(filePath);
        Log.d("ClassActivity", "setUpClass: currentDirectory has been set");
        classTitle.setText(currentDirectory.getName());
        Log.d("ClassActivity", "setUpClass: classTitle has been set");
        resetNotes();
        Log.d("ClassActivity", "setUpClass: noteList has been set and filled");
        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, noteList);
        Log.d("ClassActivity", "setUpClass: ArrayAdapter has been set to noteList");
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
            // I want to add a function call instead of the line above to check if this name exists,
            // and if not, iterate through a while loop adding x to the end of the given title
            // This would prevent existing files from being overwritten
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
        //i.putExtra("filePath", currentDirectory.listFiles()[place].toString());
        i.putExtra("filePath", toSend.toString());
        setNewNote(i);
    }

    /********************************************
     * A function to make a directory
     * @param title The new directory's title
     ********************************************/
    public void makeNewDir(String title) {
        File newDir = new File(getApplicationContext().getFilesDir(), title);
        Boolean answer = newDir.mkdir();
        if (answer) {
            Log.d("ClassActivity", "makeNewDir: " + title + " directory made");
        } else {
            Log.d("ClassActivity", "makeNewDir: " + title + " directory already exists");
        }
        currentDirectory = newDir;
        Log.d("ClassActivity", "makeNewDir: currentDirectory has been set");
    }

    /******************************************************
     * A function to rename the current directory
     * @param newName The directory's possibly new title
     ******************************************************/
    public void renameDir(String newName) {
        if (!currentDirectory.getName().equals(newName)) {
            Log.d("ClassActivity", "renameDir: currentDirectory will now be renamed");
            String checkedName = generateClassTitle(newName, getApplicationContext().getFilesDir());
            File newDirName = new File(currentDirectory.getParent(), checkedName);
            currentDirectory.renameTo(newDirName);
            currentDirectory = newDirName;
            Log.d("ClassActivity", "renameDir: currentDirectory has been renamed");
        }
    }

    /*********************************************************
     * A function used to delete notes
     * @param place The position the note we're deleting
     *********************************************************/
    public void deleteNote(int place) {
        Log.d("ClassActivity", "deleteNote: Note number " + place + " will now be deleted");
        //currentDirectory.listFiles()[place].delete();
        findNote(place).delete();
        File[] newNoteList = currentDirectory.listFiles();
        resetNotes();
        Log.d("ClassActivity", "deleteNote: noteList has been reset");
    }

    /**************************************************************************************
     * A function used to check if a Class exists in this directory to not overwrite it
     * @param name The original name we're using
     * @param parent This class's parent directory
     * @return The name that may or may not have a number added to the end
     **************************************************************************************/
    public String generateClassTitle(String name, File parent) {
        Boolean answer = false;
        int y = 0;
        String newName = "";
        if (name.equals("")) {
            Log.d("ClassActivity", "generateClassTitle: name was empty");
            name = "Untitled";
        }
        Log.d("ClassActivity", "generateClassTitle: about to test if name exists");
        for (int x=0; x<parent.listFiles().length; x++) {
            if (name.equals(parent.listFiles()[x].getName())) {
                // If the name is in the directory
                answer = true;
            }
        }
        if (!answer) {
            Log.d("ClassActivity", "generateClassTitle: returning name " + name);
            return name;
        }
        Log.d("ClassActivity", "generateClassTitle: does " + name + " title exist in its parent directory? " + answer);
        while (answer) {
            newName = name + y;
            // Append the number to the name
            answer = false;
            for (int z=0; z<parent.listFiles().length; z++) {
                Log.d("ClassActivity", "generateClassTitle: testing if " + newName + " exists in this directory");
                if (newName.equals(parent.listFiles()[z].getName())) {
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
        File[] fileList = currentDirectory.listFiles();
        notes_title.clear();
        for (int x=0; x<fileList.length; x++) {
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
        SearchView searchVal = findViewById(R.id.searchView2);
        File foundFile = null;
        if (!searchVal.getQuery().toString().isEmpty()) {
            // get filtered list of notes
            List<String> searchList = filterNotes(notes_title, searchVal.getQuery().toString());
            // find what filename is being selected
            String fileName = searchList.get(position);
            for (File f:currentDirectory.listFiles()) {
                if (fileName.equals(f.getName())) {
                    // if the names match, we've found our file
                    foundFile = f;
                }
            }
        } else {
            // if not searching for anything
            foundFile = currentDirectory.listFiles()[position];
        }
        return foundFile;
    }

    /**
     * Filters a list of notes for applicable names
     * @param noteList list to be filtered
     * @param searchVal string to search for
     * @return list of applicable note names
     */
    public List<String> filterNotes(List<String> noteList, String searchVal) {
        List<String> newList = new ArrayList<>();
        for (String x:noteList) {
            if (x.toLowerCase().contains(searchVal.toLowerCase())) {
                newList.add(x);
            }
        }
        return newList;
    }

    /**
     * Reset notes list for display
     */
    public void resetNotes() {
        notes_title.clear();
        File[] fileList = currentDirectory.listFiles();
        for (int x=0; x<fileList.length; x++) {
            notes_title.add(fileList[x].getName());
        }
    }
}