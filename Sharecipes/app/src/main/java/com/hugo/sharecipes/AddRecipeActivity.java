package com.hugo.sharecipes;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hpatural on 06/06/2016.
 */
public class AddRecipeActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mDescription;
    private Button mAddButton;
    private Button mAddPictureButton;
    private ImageView mPictureImageView;
    private static int RESULT_LOAD_IMAGE = 1;
    private String mPicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_recipe_activity);
        mTitle = (EditText)findViewById(R.id.recipeTitle);
        mDescription = (EditText)findViewById(R.id.recipeDescription);
        mAddButton = (Button)findViewById(R.id.addButton);
        mAddPictureButton = (Button)findViewById(R.id.addPictureButton);
        mPictureImageView = (ImageView)findViewById(R.id.pictureImageView);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFields()) {
                    Recipe recipe = new Recipe();
                    recipe.setDescription(mDescription.getText().toString());
                    recipe.setTitle(mTitle.getText().toString());
                    FirebaseDatabaseUtils firebaseDatabaseUtils = new FirebaseDatabaseUtils();
                    firebaseDatabaseUtils.saveRecipe(recipe);
                    recipeAdded();


                }else{
                    Toast.makeText(getApplicationContext(), "Please fill all the blanks !",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        mAddPictureButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Log.v("a","Permission is granted");
                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }else{
                    Toast.makeText(getApplicationContext(), "You don't have permit access storage", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void recipeAdded(){
        Toast.makeText(getApplicationContext(), "The recipe has been correctly added !", Toast.LENGTH_LONG).show();
        mTitle.setText("");
        mDescription.setText("");
    }

    private boolean checkFields(){
        if(!mTitle.getText().toString().equals("") && !mDescription.getText().toString().equals("")){
            return true;
        }else{
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mPicturePath = cursor.getString(columnIndex);
            cursor.close();
            mPictureImageView.setImageBitmap(resizeBitmap(BitmapFactory.decodeFile(mPicturePath)));
        }
    }

    public Bitmap resizeBitmap(Bitmap bitmap){
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        return scaled;
    }


    public boolean isStoragePermissionGranted() {
        System.out.println("isStoragePermissionGranted : ");
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                System.out.println("perm granted : ");
                return true;
            } else {
                System.out.println("perm revoked : ");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            System.out.println("perm granted : ");
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            System.out.println("onRequestPermissionsResult : "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}
