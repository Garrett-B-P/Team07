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
import android.view.View;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashSet;

public class ClassActivity extends AppCompatActivity {
    int classId;

    //Creates Arrays that hold the note information, Garrett
    static ArrayList<String> notes_title = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;

    //Creates an intent for the NotesActivity and sends some information to the activity, Garrett
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
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
            }
        });


    }
}



    /*
    private void addNote(){

    }

    private void deleteNote(){

    }

    private void editNote(View view){
        Intent intent = new Intent(this, NotesActivity.class);
        String title = "Notes Title";
        intent.putExtra("Notes Title", title);
        startActivity(intent);
    }

    public void sortNotes(){

    }

    public String getClassTitle(){
        return classTitle.getText().toString();
    }

    public List searchClassNotes(){
        // Change <String> to whatever the list objects will be later
        ArrayList<String> searchList = new ArrayList<>();
        return searchList;
    }
     */
