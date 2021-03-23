package com.example.team07;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;

/********************************************************************************************
 * A class to handle ui for the notes page. Creates a file to store the information in itself
 * in the class it currently belongs to if needed. Otherwise opens the correct file and loads
 * the information to the screen.
 ********************************************************************************************/
public class NotesActivity extends AppCompatActivity implements Comparable<File> {
    //Creates a variable that holds the id of the note so that it can be saved and reloaded, Garrett
    //int noteId;
    // Member variables below are for use with app's directory
    File parent;
    File path;
    String contents;

    //For the camera
    final int TAKE_PHOTO = 1;
    final int FROM_STORAGE = 2;

    //Calendar lastEdit;

    public void onClick(View v) {
        finish();
    }

    // Api thing is for StandardCharsets.UTF_8 in setUpNote()
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        //TextView time = findViewById(R.id.time);
        //lastEdit = Calendar.getInstance();
        //String timeStamp = java.text.DateFormat.getDateTimeInstance().format(lastEdit.getTime());
        //time.setText(timeStamp);
        // If we were to use the above, it would be below setUpNote()

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

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

    // compareTo() is needed for Comparable<File>
    @Override
    public int compareTo(File o) {
        return this.path.getName().compareTo(o.getName());
    }

    /*
    // To make other comparisons: Collections.sort(ListName, NotesActivity.byCreate);
    public static Comparator<File> byCreate = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            //return (o1.createDateVar.compareTo(o2.createDateVar);
            return 0;
        }
    };
     */
    public static Comparator<File> lastEdit = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return (int)(o1.lastModified() - o2.lastModified());
        }
    };

    //For the camera
    public void takePicture(View view) {

        selectImage();
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Toast.makeText(getApplicationContext(), "Take Photo", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, TAKE_PHOTO);
                }
                else if (items[item].equals("Choose from Library")) {
                    String[] PERMISSIONS = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    };
                    ActivityCompat.requestPermissions(NotesActivity.this, PERMISSIONS, 1);

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    Toast.makeText(getApplicationContext(), "Choose from Library", Toast.LENGTH_SHORT).show();
                    startActivityForResult(Intent.createChooser(intent, "Select File"), FROM_STORAGE);
                }
                else if (items[item].equals("Cancel")) {
                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File destination = null;
        if (resultCode == RESULT_OK) {

            if (requestCode == TAKE_PHOTO) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;

                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }

                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(thumbnail);
                MediaStore.Images.Media.insertImage(getContentResolver(), thumbnail, "" , "");

            } else if (requestCode == FROM_STORAGE) {
                Log.d("FROM_STORAGE ", " FROM_STORAGE");
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(NotesActivity.this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                String fileNameSegments[] = selectedImagePath.split("/");
                String fileName = fileNameSegments[fileNameSegments.length - 1];
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 100;
                int scale = 1;

                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;

                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bm);


            }
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
        //lastEdit.setTimeInMillis(path.lastModified());
        // Above supposedly sets a calendar date through milliseconds
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
        ClassActivity.refreshNoteList();
        Log.d("NotesActivity", "saveToFile: Class arrayAdapter notified?");
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
        if (name.equals("")) {
            Log.d("NotesActivity", "generateNoteTitle: name was empty");
            name = "Untitled";
        }
        for (int x=0; x<parentFile.listFiles().length; x++) {
            if (name.equals(parentFile.listFiles()[x].getName())) {
                // If the name is in the directory
                answer = false;
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

    /**
     * To save picture to file
     * @param bitmapImage Image to be saved
     */
    public void savePicture(Bitmap bitmapImage) {
        File picFile = new File(parent, path.getName() + ".jpg"); // Does this need to be .jpg, or do we need to specify?

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(picFile);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toast.makeText(NotesActivity.this, "Pic saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * To load picture from file and display in imageView
     */
    public void loadPicture() {
        // How to choose any one picture?
        // Maybe try to save to gallery if needed? Then can pull from there?
        try {
            File picFile = new File(parent, path.getName() + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(picFile));
            ImageView img = (ImageView)findViewById(R.id.imageView);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}


//dateCreated = Calendar.getInstance();