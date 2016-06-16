package com.hugo.sharecipes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hpatural on 09/06/2016.
 * Functions for upload/download/delete datas in the json database
 */
public class FirebaseDatabaseUtils {

    public static String RECIPES_FIREBASE_REFERENCE = "recipes";
    private FirebaseDatabase mFirebaseDatabase;

    public FirebaseDatabaseUtils(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void getRecipes(final GetResultsCallback callback){
        DatabaseReference myRef = mFirebaseDatabase.getReference(RECIPES_FIREBASE_REFERENCE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Recipe> recipes = new ArrayList<>();

                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);
                    recipe.setId(dataSnapshot.getKey());
                    recipes.add(recipe);
                }
                callback.onSuccess(recipes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public void saveRecipe(Recipe recipe){
        DatabaseReference myRef = mFirebaseDatabase.getReference(RECIPES_FIREBASE_REFERENCE);
        myRef.push().setValue(recipe);

    }



    public interface GetResultsCallback{
        void onSuccess(ArrayList<Recipe> recipes);
        void onError(DatabaseError databaseError);
    }
}
