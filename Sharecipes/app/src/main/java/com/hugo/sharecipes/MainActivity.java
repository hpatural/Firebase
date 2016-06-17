package com.hugo.sharecipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected ArrayList<Recipe> mRecipes;
    protected RecipesAdapter mRecipesAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddRecipeActivity.class);
                startActivity(intent);
            }
        });

        mRecipes = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recipesRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecipesAdapter = new RecipesAdapter(mRecipes, getApplication());
        mRecyclerView.setAdapter(mRecipesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecipesFromFirebase();

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

    /***
     * Get the recipes from firebase database
     */
    public void getRecipesFromFirebase(){
        FirebaseDatabaseUtils firebaseDatabaseUtils = new FirebaseDatabaseUtils();
        firebaseDatabaseUtils.getRecipes(new FirebaseDatabaseUtils.GetResultsCallback() {
            @Override
            public void onSuccess(ArrayList<Recipe> recipes) {
                mRecipes = recipes;
                if(mRecipesAdapter!=null){
                    mRecipesAdapter.setList(mRecipes);
                }
                getRecipesImage();
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /***
     * Get the recipes images
     */
    public void getRecipesImage(){
        FirebaseStorageUtils firebaseStorageUtils = new FirebaseStorageUtils(getApplicationContext());
        for(final Recipe recipe : mRecipes){
            firebaseStorageUtils.downloadImage(recipe.getId(), new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    System.out.println("success : recipes image");
                    Bitmap bitmap = BitmapFactory.decodeByteArray((byte[])o , 0, ((byte[])o).length);
                    if(bitmap !=null){
                        System.out.println("success différent nul: recipes image");
                        recipe.setPicture(resizeBitmap(bitmap));
                        mRecipesAdapter.notifyDataSetChanged();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    public Bitmap resizeBitmap(Bitmap bitmap){
        int nh = (int) ( bitmap.getHeight() * (64.0 / bitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 64, nh, true);
        return scaled;
    }

}
