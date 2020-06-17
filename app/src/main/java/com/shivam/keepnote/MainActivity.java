package com.shivam.keepnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.shivam.keepnote.database.Note;
import com.shivam.keepnote.note_adapter.Adapter;
import com.shivam.keepnote.view_model.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecyclerView recyclerView;
    FloatingActionButton actionButton;

    Adapter adapter = new Adapter();

    public static final int ADD_NOTE_REQUEST = 1;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog();
            }
        });

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });

        adapter.setOnClickListener(new Adapter.onItemClickListener() {
            @Override
            public void onDeleteItem(final Note note) {
                final AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
                myDialog.setMessage("Are you sure you want to delete ?\n"+"This cannot be undone")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteViewModel.delete(note);
                                Toast.makeText(MainActivity.this, "Successfully Deleted....", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = myDialog.create();
                dialog.show();
            }
        });

    }

    private void CustomDialog() {

        AlertDialog.Builder alertBuilder =new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myview = inflater.inflate(R.layout.custom_dialog_room,null);

        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setView(myview);
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFade;
        alertDialog.show();

        final EditText name = myview.findViewById(R.id.name);
        final EditText email = myview.findViewById(R.id.email);
        final NumberPicker picker = myview.findViewById(R.id.number_picker_priority);
        picker.setMinValue(1);
        picker.setMaxValue(10);

        Button addButton = myview.findViewById(R.id.saveInfo);
        Button cancelButton = myview.findViewById(R.id.cancel);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                int priority = picker.getValue();

                if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mEmail)){
                    Toast.makeText(MainActivity.this, "Empty Credentials...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Note note = new Note(mName,mEmail,priority);
                    noteViewModel.insert(note);
                    Toast.makeText(MainActivity.this, "Data Inserted....", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteAll:

                if (adapter.getItemCount()!=0){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Sure to Delete All Data ?\nThis cannot be undone");
                    builder.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    noteViewModel.deleteAllNotes();
                                    Snackbar.make(findViewById(android.R.id.content),"Deleted All Successfully",Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"No Item to Delete",Snackbar.LENGTH_LONG).show();
                }

        }

        return super.onOptionsItemSelected(item);
    }
}
