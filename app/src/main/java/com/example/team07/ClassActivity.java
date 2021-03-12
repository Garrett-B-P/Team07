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

public class ClassActivity extends AppCompatActivity {
    int classId;
    // Member variables below are for use with app's directory
    static File currentDirectory;
    public String[] noteList;

    //Creates Arrays that hold the note information, Garrett
    static ArrayList<String> notes_title = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;

    //Creates an intent for the NotesActivity and sends some information to the activity, Garrett
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
        //setNewNote(intent);
        startActivity(intent);
        Log.i("ClassOnClick", "Note Activity Opened");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        //finds the listView in the layout and sets it to a variable, Garrett
        ListView listView = findViewById(R.id.listView2);

        //I believe this saves the note, Garrett
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        //Creates an example note if there are no notes, Garrett
        if (set == null) {
            notes_title.add("Example note");
        } else {
            notes_title = new ArrayList(set);
        }

        //Adds the created notes to the array adapter, Garrett
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, notes_title);

        listView.setAdapter(arrayAdapter);

        //When a note is clicked on it will go to that note and passes in the info, Garrett
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                intent.putExtra("noteId", i);
                // Below will send Class directory's filepath to be used in ClassActivity
                //setExistingNote(intent, i);
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
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete " + notes_title.get(itemToDelete) + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notes_title.remove(itemToDelete);
                                // Below is to delete the Note from the directory
                                //deleteNote(i);
                                arrayAdapter.notifyDataSetChanged();
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                                HashSet<String> set = new HashSet(ClassActivity.notes_title);
                                sharedPreferences.edit().putStringSet("notes", set).apply();

                                Log.i("ClassOnItemOnLongClick", "Deleted note #" + itemToDelete);
                            }
                        }).setNegativeButton("No", null).show();
                return true;
            }
        });

        //This section of code was supposed to create the different classes, Garrett
        EditText classTitle = findViewById(R.id.classTitle);
        Intent intent = getIntent();

        classId = intent.getIntExtra("classId", -1);

        if (classId != -1) {
            classTitle.setText(MainActivity.classes.get(classId));
        } else {
            MainActivity.classes.add("");
            classId = MainActivity.classes.size() - 1;
            MainActivity.arrayAdapter3.notifyDataSetChanged();

            Log.i("ClassCreate", "Created Class #" + classId);
        }

        // This custom function is for use with an app's directory
        // PLEASE NOTE, FELLOW PROGRAMMERS!! I'd made my program ask for a name in MainActivity for the new ClassActivity,
        // but don't quite know how to program it here, since the text box for new Class/Note are in ClassActivity/NoteActivity
        // instead of their "parent" activities. I'll do what I can
        //setUpClass(intent);

        classTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.arrayAdapter3.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                MainActivity.classes.set(classId, String.valueOf(charSequence));
                MainActivity.arrayAdapter3.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet set = new HashSet(MainActivity.classes);
                sharedPreferences.edit().putStringSet("classes", set).apply();


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

                // To make new directory:
                //makeNewDir(newTitle);

                // To change directory's name:
                //renameDir(newName);
            }
        });


    }

    // Function for searchView2, will apply a filter to arrayAdapter2 so only items with matching text are displayed
    @Override
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
    }
    // Below are functions to call for app's directory use
    public void setUpClass(Intent i) {
        String filePath = i.getStringExtra("filePath");
        EditText classTitle = findViewById(R.id.classTitle);
        if (filePath != null) {
            // If existing directory was clicked on and path sent, set up here
            currentDirectory = new File(filePath);
            classTitle.setText(currentDirectory.getName());
            File[] fileList = currentDirectory.listFiles();
            noteList = new String[fileList.length];
            for (int x=0; x<fileList.length; x++) {
                noteList[x] = fileList[x].getName();
            }
        }
        // I should have this check if intent sent "filePath", for existing classes,
        // and if that's null, check if it sent "parentPath", for new classes
        // I now have functions in Main to send either "filePath" or "parentPath"
    }
    public Intent setNewNote(Intent i) {
        return i.putExtra("parentPath", currentDirectory.toString());
    }
    public Intent setExistingNote(Intent i, int place) {
        return i.putExtra("filePath", currentDirectory.listFiles()[place].toString());
    }
    public void makeNewDir(String title) {
        File newDir = new File(getApplicationContext().getFilesDir(), title);
        newDir.mkdir();
        currentDirectory = newDir;
    }
    public void renameDir(String newName) {
        if (!currentDirectory.getName().equals(newName)) {
            File newDirName = new File(currentDirectory.getParent(), newName);
            currentDirectory.renameTo(newDirName);
            currentDirectory = newDirName;
        }
    }
    public void deleteNote(int place) {
        currentDirectory.listFiles()[place].delete();
        File[] newNoteList = currentDirectory.listFiles();
        noteList = new String[newNoteList.length];
        for (int x=0; x<newNoteList.length; x++) {
            noteList[x] = newNoteList[place].getName();
        }
    }
}