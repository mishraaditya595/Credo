package com.credo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private Uri postImageUri=null;
    private ProgressBar newPostProgressBar;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        initialiseFields();
        displayToolbar(); //to display the toolbar of the activity

        ((ImageView)findViewById(R.id.new_post_IV)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCropper();
            }
        });

        ((Button)findViewById(R.id.publish_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String blogTitle=((EditText)findViewById(R.id.new_post_title_ET)).getText().toString();
                final String blogDescription=((EditText)findViewById(R.id.new_post_description_ET)).getText().toString();

                if(!blogTitle.isEmpty() && !blogDescription.isEmpty() && postImageUri!=null)
                {
                    newPostProgressBar.setVisibility(View.VISIBLE);

                    final String randomName = UID+"_"+randomStringGenerator();
                    final StorageReference filePath=storageReference.child("blog_images").child(randomName+".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final String downloadUri=filePath.getDownloadUrl().toString();

                            if(task.isSuccessful())
                            {
                                Bitmap compressedImageFile=null;

                                try {
                                    compressedImageFile=compressBlogImageFile();
                                } catch (IOException e) {
                                    Toast.makeText(NewPostActivity.this, "Compression Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                                    return;//so that the program run stops
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbdata = baos.toByteArray();
                                final UploadTask thumbnailUploadTask = storageReference.child("blog_images/thumbs").child("thumb_"+randomName+".jpg").putBytes(thumbdata);

                                thumbnailUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        final String downloadThumbUri=thumbnailUploadTask.getResult().getStorage().getDownloadUrl().toString(); //get the download uri here

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url",downloadUri);
                                        postMap.put("thumbnail_url",downloadThumbUri);
                                        postMap.put("blog_title",blogTitle);
                                        postMap.put("blog_description",blogDescription);
                                        postMap.put("author",UID);
                                        postMap.put("timestamp",FieldValue.serverTimestamp());

                                        firestore.collection("posts").document(blogTitle+"_"+UID).set(postMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(NewPostActivity.this, "Blog published successfully.", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(NewPostActivity.this, MainActivity.class));
                                                            finish();
                                                        } else {
                                                            String errorMsg = task.getException().getMessage();
                                                            Toast.makeText(NewPostActivity.this, "Firestore Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                                                        }
                                                        newPostProgressBar.setVisibility(View.INVISIBLE );

                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewPostActivity.this, "Compression Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                newPostProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initialiseFields() {
        newPostProgressBar=findViewById(R.id.new_post_PB);
        storageReference=FirebaseStorage.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();
        UID= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void displayToolbar() {
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.new_post_toolbar));
        getSupportActionBar().setTitle("Make a new post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to add a back button to the toolbar
    }

    private void imageCropper() {
        // start crop image activity to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setAspectRatio(20,11)
                .start(NewPostActivity.this);
    }

    //this is to launch the activity to crop the selected image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                postImageUri=result.getUri();
                ((ImageView)findViewById(R.id.new_post_IV)).setImageURI(postImageUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    private static String randomStringGenerator() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(200);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private Bitmap compressBlogImageFile() throws IOException {
        File blogImageFile=new File(postImageUri.getPath());
        Bitmap compressedImageFile = new Compressor(NewPostActivity.this)
                .setQuality(5).compressToBitmap(blogImageFile);
        return compressedImageFile;
    }

}
