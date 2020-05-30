package com.credo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private Uri profileImageURI=null;
    private ProgressBar setupProgressBar;
    private FirebaseFirestore firestore;
    private String UID;
    private boolean isChanged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        displayToolbar(); //to display the activity's toolbar
        initialiseFields();

        setupProgressBar.setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.submit_button)).setEnabled(false);
        firestore.collection("users").document(UID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                //this is if the user already exists in our database
                                String name = task.getResult().getString("name");
                                String profileImage = task.getResult().getString("profile image");
                                profileImageURI=Uri.parse(profileImage);

                                ((EditText)findViewById(R.id.set_name_ET)).setText(name);
                                RequestOptions placeholderrequest = new RequestOptions();
                                placeholderrequest.placeholder(R.drawable.com_facebook_profile_picture_blank_portrait);
                                Glide.with(AccountSetupActivity.this).setDefaultRequestOptions(placeholderrequest).load(profileImage).into((ImageView)findViewById(R.id.set_profile_image));
                            }
                            else
                            {
                                Toast.makeText(AccountSetupActivity.this, "Please setup your profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            String errorMsg=task.getException().getMessage();
                            Toast.makeText(AccountSetupActivity.this, "Firestore Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        setupProgressBar.setVisibility(View.INVISIBLE);
                        ((Button)findViewById(R.id.submit_button)).setEnabled(true);

                    }
                });

        /*this is when user taps on the profile image view, we check if necessary permissions
            are granted or not and then allow user to choose an image from his/her device's storage
         */
        findViewById(R.id.set_profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this condition is to check if user's device runs on Android M or later
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    //this is check if storage permissions are granted or not. if not, then request permission from the user
                    if(ContextCompat.checkSelfPermission(AccountSetupActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        ActivityCompat.requestPermissions(AccountSetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else
                    {
                        // start crop image activity to get image for cropping and then use the image in cropping activity
                        imageCropper();
                    }
                }
                //else part is for users with devices below Android M. Permissions are automatically granted in those devices
                else
                {
                    imageCropper();
                }
            }
        });

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = ((EditText) findViewById(R.id.set_name_ET)).getText().toString();
                if (!TextUtils.isEmpty(user_name) && profileImageURI != null) {
                    setupProgressBar.setVisibility(View.VISIBLE);
                    if (isChanged) {
                        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final StorageReference imagePath = storageReference.child("profile_images").child(UID + ".jpg");
                        imagePath.putFile(profileImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(imagePath, user_name);
                                } else {
                                    Toast.makeText(AccountSetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    setupProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    else
                    {
                        storeFirestore(null,user_name);
                    }
                }
                else if(user_name.isEmpty())
                {
                    Toast.makeText(AccountSetupActivity.this, "Username cannot be left empty.", Toast.LENGTH_SHORT).show();
                }
                //else if(!TextUtils.isEmpty(user_name) && profileImageURI != null)

            }
        });
    }

    private void storeFirestore(StorageReference imagePath, String user_name) {
        Task<Uri> downloadUri;
        //the map will contain the details of the user that we will store in our database
        Map<String, String> userMap=new HashMap<>();
       if (imagePath!=null)
       {
           downloadUri=imagePath.getDownloadUrl();
           userMap.put("name", user_name);
           userMap.put("profile image",downloadUri.toString());
       }
       else
       {
           userMap.put("name", user_name);
           userMap.put("profile image",profileImageURI.toString());
       }

        firestore.collection("users").document(UID).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(AccountSetupActivity.this,MainActivity.class));
                            finish();
                        }
                        else
                        {
                            String errorMsg=task.getException().getMessage();
                            Toast.makeText(AccountSetupActivity.this, "Firestore Error: "+errorMsg, Toast.LENGTH_SHORT).show();

                        }
                        setupProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void initialiseFields() {
        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        setupProgressBar=findViewById(R.id.account_setup_PB);
        UID=mAuth.getCurrentUser().getUid();
    }

    private void imageCropper() {
        // start crop image activity to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountSetupActivity.this);
    }

    //this is to launch the activity to crop the selected image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                profileImageURI = result.getUri();
                ((CircleImageView)findViewById(R.id.set_profile_image)).setImageURI(profileImageURI);
                isChanged=true;
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    private void displayToolbar() {
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar));
    }
}
