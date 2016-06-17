package com.hugo.sharecipes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


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


    /***
     * Get all the recipes from Firebase Database
     * @param callback
     */
    public void getRecipes(final GetResultsCallback callback){
        DatabaseReference myRef = mFirebaseDatabase.getReference(RECIPES_FIREBASE_REFERENCE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Recipe> recipes = new ArrayList<>();

                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);
                    recipe.setId(postSnapshot.getKey());
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

    /***
     * Save a recipe in firebase
     * @param recipe
     * @param callback
     */
    public void saveRecipe(final Recipe recipe, final SaveRecipesCallback callback){
        DatabaseReference myRef = mFirebaseDatabase.getReference(RECIPES_FIREBASE_REFERENCE);
        //Push creates a generated Id
        myRef.push().setValue(recipe, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError==null){
                    recipe.setId(databaseReference.getKey());
                    callback.onSuccess();
                }else{
                    callback.onError(databaseError);
                }
            }
        });
    }

    public interface GetResultsCallback{
        void onSuccess(ArrayList<Recipe> recipes);
        void onError(DatabaseError databaseError);
    }

    public interface SaveRecipesCallback{
        void onSuccess();
        void onError(DatabaseError databaseError);
    }
}
