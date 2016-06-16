package com.hugo.sharecipes;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by hpatural on 09/06/2016.
 */
public class FirebaseStorageUtils {
    public static final String FIREBASE_STORAGE_URL = "gs://flickering-fire-2992.appspot.com";
    public static final String RECIPE_IMAGES_FOLDER = "recipe_images";

    protected StorageReference mFbStorageRef;
    private Context mContext;

    public FirebaseStorageUtils(Context context){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        this.mFbStorageRef = storage.getReferenceFromUrl(FIREBASE_STORAGE_URL);
        this.mContext = context;
    }


    public void sendImage(String imagePath, String imageName){

        // Get the file from absolute path
        Uri file = Uri.fromFile(new File(imagePath));

        // Set the reference in Firebase
        StorageReference imageRef = this.mFbStorageRef.child(RECIPE_IMAGES_FOLDER+"/"+imageName);

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
                System.out.println("on success : " + taskSnapshot.getDownloadUrl());
            }
        });
    }



}
