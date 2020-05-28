package com.credo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AccountSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        displayToolbar(); //to display the activity's toolbar

        //this is when user taps on the profile image view
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
                }
            }
        });
    }

    private void displayToolbar() {
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar));
    }
}
