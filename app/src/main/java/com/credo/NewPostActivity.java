package com.credo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        
        displayToolbar();
    }

    private void displayToolbar() {
        setSupportActionBar((androidx.appcompat.widget.Toolbar) findViewById(R.id.new_post_toolbar));
        getSupportActionBar().setTitle("Make a new post");
    }
}
