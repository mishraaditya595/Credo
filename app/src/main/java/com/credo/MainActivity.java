package com.credo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            //if there is no one signed in, it will ask for sign up
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayToolbar(); //to display the app's toolbar

        GoogleSignInAccount signInAccount=GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount!=null)
        {
            Toast.makeText(this, signInAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayToolbar() {
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar));
        getSupportActionBar().setTitle("Credo Space");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //to inflate the options menu when the 3 dot button is pressed
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_sign_out:
                userSignOut();
                return true;
        }

        return true;
    }

    private void userSignOut() {
        //to sign out the current user and send him/her back to the sign in activity
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,SignInActivity.class));
    }
}
