package ca.bcit.comp3717finalexam;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MoviesAdapter adapter;
    private ArrayList<Movie> movies = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();

        // Loads the list with all Movies in the database.
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.child("movies").getChildren()) {
                    Movie movie = child.getValue(Movie.class);
                    movies.add(movie);
                }
                adapter = new MoviesAdapter(MainActivity.this, movies);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MoviesAdapter(this, movies);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertDialog();
            }
        });
    }

    /**
     * Adds the specified movie to the database.
     *
     * @param movie the movie to be added.
     */
    private void writeNewMovie(Movie movie) {
        String key = database.child("movies").push().getKey();
        database.child("movies").child(key).setValue(movie);
    }

    /**
     * Adds the specified movie to the Recycler View list.
     *
     * @param movie the movie to be added.
     */
    public void addToList(Movie movie) {
        movies.add(movie);
        recyclerView.setAdapter(adapter);
        adapter = new MoviesAdapter(MainActivity.this, movies);
    }

    /**
     * Builds the Alert Dialog that is triggered when the plus button is pressed.
     * Contains two listeners:
     *     - "ADD" button. When pressed, a movie is created in the database.
     *     - "Cancel" button. When pressed, the dialog is exited.
     */
    public void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add a Movie");

        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.builder,
                (ViewGroup) findViewById(android.R.id.content), false);

        final EditText movie_name = viewInflated.findViewById(R.id.input1);
        final EditText movie_desc = viewInflated.findViewById(R.id.input2);
        final EditText movie_link = viewInflated.findViewById(R.id.input3);

        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Movie movie = new Movie(movie_name.getText().toString(),
                        movie_desc.getText().toString(), movie_link.getText().toString());

                writeNewMovie(movie);
                addToList(movie);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
