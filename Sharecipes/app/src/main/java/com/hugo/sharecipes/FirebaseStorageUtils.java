package com.hugo.sharecipes;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by hpatural on 09/06/2016.
 */
public class FirebaseStorageUtils {

    //The adress of the bucket
    public static final String FIREBASE_STORAGE_URL = "gs://flickering-fire-2992.appspot.com";
    //Name of the directory where we put images
    public static final String RECIPE_IMAGES_FOLDER = "recipe_images";

    protected StorageReference mFbStorageRef;
    private Context mContext;

    public FirebaseStorageUtils(Context context){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.mFbStorageRef = storage.getReferenceFromUrl(FIREBASE_STORAGE_URL);
        this.mContext = context;
    }

    /***
     * Send an image on firebase storage
     * @param imagePath
     * @param recipeId
     */
    public void sendImage(String imagePath, String recipeId){
        // Get the file from absolute path
        Uri file = Uri.fromFile(new File(imagePath));

        // Set the reference in Firebase
        StorageReference imageRef = this.mFbStorageRef.child(RECIPE_IMAGES_FOLDER+"/"+recipeId);

        // Put the file and set to an upload task
        UploadTask uploadTask = imageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("on failure : " + exception.getMessage());
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                System.out.println("on success : " + downloadUrl);
            }
        });
    }

    /***
     * Download an image coming from firebase
     * @param recipeId
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void downloadImage(String recipeId, OnSuccessListener onSuccessListener,OnFailureListener onFailureListener){
        StorageReference pathReference = this.mFbStorageRef.child(RECIPE_IMAGES_FOLDER+"/"+recipeId);

        pathReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }



}
