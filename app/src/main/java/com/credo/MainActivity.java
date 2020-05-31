package com.credo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null) {
            //if there is no one signed in, it will ask for sign up
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }
        else
        {
            //if the user is signed in, we check if the user has completed the profile setup
            firestore.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                //if task is successful means user exists
                                if(!task.getResult().exists())
                                {
                                    //this means user's profile details are not present in our database
                                    startActivity(new Intent(MainActivity.this,AccountSetupActivity.class));
                                    finish();
                                }
                            }
                            else
                            {
                                //error handling part
                                String error=task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "Firestore error: "+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseFields();
        displayToolbar(); //to display the app's toolbar

        GoogleSignInAccount signInAccount=GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount!=null)
        {
            Toast.makeText(this, signInAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
        }

        ((FloatingActionButton)findViewById(R.id.add_post_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewPostActivity.class));
            }
        });

        ((BottomNavigationView)findViewById(R.id.main_bottom_navigation_view)).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        replaceFragment(new HomeFragment());
                        return true;
                    case R.id.nav_notifications:
                        replaceFragment(new NotificationFragment());
                        return true;
                    case R.id.nav_profile:
                        replaceFragment(new ProfileFragment());
                        return true;
                }
                return false;
            }
        });


    }

    private void initialiseFields() {
        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
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
            case R.id.item_settings:
                startActivity(new Intent(MainActivity.this,AccountSetupActivity.class));
                return true;
        }

        return true;
    }

    private void userSignOut() {
        //to sign out the current user and send him/her back to the sign in activity
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,SignInActivity.class));
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container_frame, fragment);
        fragmentTransaction.commit();
    }
}
