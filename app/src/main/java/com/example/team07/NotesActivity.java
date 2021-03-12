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
    int noteId;
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
        noteId = intent.getIntExtra("noteId", -1);

        if (noteId != -1) {
            noteTitle.setText(ClassActivity.notes_title.get(noteId));

        } else {
            ClassActivity.notes_title.add("");
            noteId = ClassActivity.notes_title.size() - 1;
            ClassActivity.arrayAdapter.notifyDataSetChanged();
        }

        // This is a custom function to use app file to set up Note
        //setUpNote(intent);

        //This is checking for when the note is changed and saves it when it does, for the title, Garrett
        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ClassActivity.notes_title.set(noteId, String.valueOf(charSequence));
                ClassActivity.arrayAdapter.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet set = new HashSet(ClassActivity.notes_title);
                sharedPreferences.edit().putStringSet("notes", set).apply();
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
    /*
    @Override
    protected void onStop() {
        super.onStop();

        // If no title, either don't save note or assign title?
        TextView noteTitle = findViewById(R.id.noteTitle);
        renameFile(noteTitle.getText().toString())
        saveToFile()
    }
     */

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setUpNote(Intent i) {
        // to set up an existing note
        String filepath = getNotePath(i);
        EditText noteTitle = findViewById(R.id.noteTitle);
        if (filepath != null) {
            path = new File(filepath);
            noteTitle.setText(path.getName());
            try {
                FileInputStream fis = new FileInputStream(path);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(isr)) {
                    String line = reader.readLine();
                    while (line != null) {
                        builder.append(line).append("\n");
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    contents = builder.toString();
                }
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), "File is not found", Toast.LENGTH_SHORT);
                toast.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // I should have this check if intent sent "filePath", for existing notes,
        // and if that's null, check if it sent "parentPath", for new notes
        // I now have functions in Class to send either "filePath" or "parentPath"
    }
    public String getNotePath(Intent i) {
        String filePath = i.getStringExtra("filePath");
        String parentPath = i.getStringExtra("parentPath");
        if (filePath != null) {
            return filePath;
        } else {
            File parent = new File(parentPath);
            String newName = generateNoteTitle("Untitled", parent);
            // I want to add a function call instead of the line above to check if this name exists,
            // and if not, iterate through a while loop adding x to the end of the given title
            // This would prevent existing files from being overwritten
            File newNote = new File(parentPath, newName);
            makeNewFile(parent, newNote.getName());
            return newNote.getPath();
        }
    }
    public File makeNewFile(File parent, String name) {
        File newFile = new File(parent, name);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }
    public void renameFile(String newName) {
        if (!path.getName().equals(newName)) {
            String checkedName = generateNoteTitle(newName, parent);
            File newFileName = new File(parent, checkedName);
            path.renameTo(newFileName);
            // For some reason, renameTo() deletes the old name directory after renaming,
            // but it doesn't do the same for the old name file, so we have to do that
            path.delete();
            path = newFileName;
        }
    }
    public void saveToFile() {
        TextView body = findViewById(R.id.noteBody);
        String fileContents = body.getText().toString();
        try {
            FileWriter writer = new FileWriter(path);
            writer.append(fileContents);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String generateNoteTitle(String name, File parent) {
        Boolean answer = false;
        int y = 0;
        String newName = "";
        for (int x=0; x<parent.listFiles().length; x++) {
            if (name.equals(parent.listFiles()[x].getName())) {
                // If the name is in the directory
                answer = true;
            } else {
                return name;
            }
        }
        while (answer) {
            newName = name + y;
            // Append the number to the name
            for (int z=0; z<parent.listFiles().length; z++) {
                if (!newName.equals(parent.listFiles()[z].getName())) {
                    // If the new name isn't in the directory, exit loop
                    answer = false;
                }
            }
            y++;
        }
        return newName;
    }

}
        //dateCreated = Calendar.getInstance();




