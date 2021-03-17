package com.example.team07;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashSet;


public class NotesActivity extends AppCompatActivity implements Comparable<NotesActivity> {
    //Creates a variable that holds the id of the note so that it can be saved and reloaded, Garrett
    //int noteId;
    // Member variables below are for use with app's directory
    File parent;
    File path;
    String contents;

    //For the camera
    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    Calendar createdDate = Calendar.getInstance();
    // createdDate might never be shown, but can be sorted by in the future
    Calendar lastEdit;

    public void onClick(View v) {
        finish();
    }

    // Api thing is for StandardCharsets.UTF_8 in setUpNote()
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mimageView = findViewById(R.id.imageView);

        TextView time = findViewById(R.id.time);
        lastEdit = Calendar.getInstance();
        String timeStamp = java.text.DateFormat.getDateTimeInstance().format(lastEdit.getTime());
        time.setText(timeStamp);

//Attempt 1 on saving notes

        //Finds the note title and body and sets them to variables, Garrett
        EditText noteTitle = findViewById(R.id.noteTitle);

        //I believe this gets the information that was passed through the intent, Garrett
        Intent intent = getIntent();

        //I believe this section of code is checking the noteId, Garrett
        //noteId = intent.getIntExtra("noteId", -1);

        /*
        if (noteId != -1) {
            noteTitle.setText(ClassActivity.notes_title.get(noteId));

        } else {
            ClassActivity.notes_title.add("");
            noteId = ClassActivity.notes_title.size() - 1;
            ClassActivity.arrayAdapter.notifyDataSetChanged();
        }
         */

        // This is a custom function to use app file to set up Note
        setUpNote(intent);

        //This is checking for when the note is changed and saves it when it does, for the title, Garrett
        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //ClassActivity.notes_title.set(noteId, String.valueOf(charSequence));
                ClassActivity.arrayAdapter.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                //HashSet set = new HashSet(ClassActivity.notes_title);
                //sharedPreferences.edit().putStringSet("notes", set).apply();

                // I know it's risky, but we gotta try it
                renameFile(String.valueOf(charSequence));

            }

            @Override
            public void afterTextChanged(Editable s) {
                //add code here

                // I'd need to add file-making text here, maybe
                // If the file-making is here, here's my custom function:
                // Found a problem: can't tell what the parent directory is
                // Possible solution: can send parent directory path through intent instead,
                // plus an existing file's name if not creating a new file
                //path = makeNewFile(parent, name);
            }
        });
    }

    // onStop() is currently only saving note body to file
    // In my program, it also renamed the title if it was different
    @Override
    protected void onStop() {
        super.onStop();

        // If no title, either don't save note or assign title?
        TextView noteTitle = findViewById(R.id.noteTitle);
        renameFile(noteTitle.getText().toString());
        saveToFile();
    }

    // compareTo() is needed for Comparable<NotesActivity>
    @Override
    public int compareTo(NotesActivity o) {
        return 0;
    }

    /*
    // To make other comparisons: Collections.sort(ListName, NotesActivity.byCreate);
    public static Comparator<NotesActivity> byCreate = new Comparator<NotesActivity>() {
        @Override
        public int compare(NotesActivity o1, NotesActivity o2) {
            //return (o1.createDateVar.compareTo(o2.createDateVar);
            return 0;
        }
    };
     */

    //For the camera
    public void takePicture(View view) {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (imageTakeIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mimageView.setImageBitmap(imageBitmap);
        }

    }

    // Below are functions to call for app's directory use
    // Api thing is for StandardCharsets.UTF_8 in InputStreamReader
    /******************************************************************
     * A function to set up NotesActivity with necessary information
     * @param i The current intent from ClassActivity
     ******************************************************************/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setUpNote(Intent i) {
        // to set up an existing note
        String filepath = getNotePath(i);
        Log.d("NotesActivity", "setUpNote: filePath has been received");
        EditText noteTitle = findViewById(R.id.noteTitle);
        path = new File(filepath);
        Log.d("NotesActivity", "setUpNote: path has been set");
        noteTitle.setText(path.getName());
        Log.d("NotesActivity", "setUpNote: noteTitle has been set");
        try {
            FileInputStream fis = new FileInputStream(path);
            Log.d("NotesActivity", "setUpNote: FileInputStream has been created");
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            Log.d("NotesActivity", "setUpNote: InputStreamReader has been created");
            StringBuilder builder = new StringBuilder();
            Log.d("NotesActivity", "setUpNote: StringBuilder has been created");
            try (BufferedReader reader = new BufferedReader(isr)) {
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line).append("\n");
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("NotesActivity", "setUpNote: Something went wrong making BufferedReader or reading to StringBuilder");
            } finally {
                contents = builder.toString();
                Log.d("NotesActivity", "setUpNote: contents have been filled");
            }
            fis.close();
            Log.d("NotesActivity", "setUpNote: FileInputStream has been closed");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "File is not found", Toast.LENGTH_SHORT);
            toast.show();
            Log.d("NotesActivity", "setUpNote: The file apparently does not exist");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("NotesActivity", "setUpNote: Closing the FileInputStream went wrong");
        }
        if (contents != "") {
            EditText body = findViewById(R.id.noteBody);
            body.setText(contents);
            Log.d("NotesActivity", "setUpNote: contents is not empty, set noteBody");
        }
        //ClassActivity.arrayAdapter.notifyDataSetChanged();
    }

    /********************************************************************************
     * A function to find or make the path of the directory NotesActivity will use
     * @param i The intent that will be tested which path it has
     * @return The string holding the filepath NotesActivity will use
     ********************************************************************************/
    public String getNotePath(Intent i) {
        String filePath = i.getStringExtra("filePath");
        String parentPath = i.getStringExtra("parentPath");
        parent = new File(parentPath);
        if (filePath != null) {
            Log.d("NotesActivity", "getNotePath: sending an existing file path");
            return filePath;
        } else {
            Log.d("NotesActivity", "getNotePath: generating a new file path");
            String newName = generateNoteTitle("Untitled", parent);
            Log.d("NotesActivity", "getNotePath: title of new file is " + newName);
            // I want to add a function call instead of the line above to check if this name exists,
            // and if not, iterate through a while loop adding x to the end of the given title
            // This would prevent existing files from being overwritten
            File newNote = new File(parentPath, newName);
            Log.d("NotesActivity", "getNotePath: generated a new file");
            makeNewFile(parent, newNote.getName());
            return newNote.getPath();
        }
    }

    /**************************************************
     * A function to make a file
     * @param parent The new file's parent directory
     * @param name The new file's title
     **************************************************/
    public void makeNewFile(File parent, String name) {
        File newFile = new File(parent, name);
        try {
            Boolean answer = newFile.createNewFile();
            if (answer) {
                Log.d("NotesActivity", "makeNewFile: " + name + " file made");
            } else {
                Log.d("NotesActivity", "makeNewFile: " + name + " file already exists");
            }
            path = newFile;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("NotesActivity", "makeNewFile: Something went wrong making the file");
        }
        //ClassActivity.arrayAdapter.notifyDataSetChanged();
    }

    /******************************************************
     * A function to rename the current file
     * @param newName The file's possibly new title
     ******************************************************/
    public void renameFile(String newName) {
        if (!path.getName().equals(newName)) {
            Log.d("NotesActivity", "renameFile: path will now be renamed");
            Log.d("NotesActivity", "parent is " + parent.getName() + ", " + parent.toString());
            String checkedName = generateNoteTitle(newName, parent);
            File newFileName = new File(parent, checkedName);
            Boolean x = path.renameTo(newFileName);
            if (x) {
                path = newFileName;
                Log.d("NotesActivity", "renameFile: path has now been renamed");
            } else {
                Log.d("NotesActivity", "renameFile: path failed to be renamed");
            }
        }
        //ClassActivity.arrayAdapter.notifyDataSetChanged();
    }

    /******************************************************************
     * A function to save NotesActivity's contents to file
     ******************************************************************/
    public void saveToFile() {
        TextView body = findViewById(R.id.noteBody);
        String fileContents = body.getText().toString();
        Log.d("NotesActivity", "saveToFile: fileContents has been filled");
        try {
            FileWriter writer = new FileWriter(path);
            Log.d("NotesActivity", "saveToFile: Created FileWriter");
            writer.append(fileContents);
            Log.d("NotesActivity", "saveToFile: Wrote fileContents to file");
            writer.flush();
            Log.d("NotesActivity", "saveToFile: Flushed stream");
            writer.close();
            Log.d("NotesActivity", "saveToFile: Closed FileWriter");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("NotesActivity", "saveToFile: FileWriter failed");
        }
    }

    /**************************************************************************************
     * A function used to check if a Note exists in this directory to not overwrite it
     * @param name The original name we're using
     * @param parentFile This note's parent directory
     * @return The name that may or may not have a number added to the end
     **************************************************************************************/
    public String generateNoteTitle(String name, File parentFile) {
        Boolean answer = false;
        int y = 0;
        String newName = "";
        for (int x=0; x<parentFile.listFiles().length; x++) {
            if (name.equals(parentFile.listFiles()[x].getName()) || newName.equals("")) {
                // If the name is in the directory
                answer = true;
            }
        }
        if (!answer) {
            Log.d("NotesActivity", "generateNoteTitle: returning name " + name);
            return name;
        }
        Log.d("NotesActivity", "generateNoteTitle: does " + name + " title exist in its parent directory? " + answer);
        while (answer) {
            newName = name + y;
            // Append the number to the name
            answer = false;
            for (int z=0; z<parentFile.listFiles().length; z++) {
                Log.d("NotesActivity", "generateNoteTitle: testing if " + newName + " exists in this directory");
                if (newName.equals(parentFile.listFiles()[z].getName())) {
                    // If the new name isn't in the directory, exit loop
                    answer = true;
                }
            }
            Log.d("NotesActivity", "generateNotesTitle: new name " + newName + " has been generated");
            y++;
        }
        Log.d("NotesActivity", "generateNoteTitle: returning new name " + newName);
        return newName;
    }

}
        //dateCreated = Calendar.getInstance();




