package com.example.team07;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.HashSet;


public class NotesActivity extends AppCompatActivity implements Comparable<NotesActivity> {
    //Creates a variable that holds the id of the note so that it can be saved and reloaded, Garrett
    int noteId;

    //For the camera
    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    Calendar createdDate = Calendar.getInstance();
    // createdDate might never be shown, but can be sorted by in the future
    Calendar lastEdit;

    public void onClick(View v) {
        finish();
    }

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
            }
        });
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
}
        //dateCreated = Calendar.getInstance();




