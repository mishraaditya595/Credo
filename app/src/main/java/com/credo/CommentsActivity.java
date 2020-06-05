package com.credo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private String blogPostDoc;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        initialiseFields();
        displayToolbar();

        //to receive the blog post id from the recycler adapter activity
        blogPostDoc=getIntent().getStringExtra("blog_title") + "_" + getIntent().getStringExtra("blog_post_id");

        ((CircleImageView)findViewById(R.id.send_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment=((EditText)findViewById(R.id.comments_ET)).getText().toString();

                if(!comment.isEmpty())
                {
                    Map<String, Object> commentMap=new HashMap<>();
                    commentMap.put("message",comment);
                    commentMap.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    commentMap.put("timestamp", FieldValue.serverTimestamp());
                    firestore.collection("posts").document(blogPostDoc).collection("comments")
                            .add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this, "Firestore Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                ((EditText)findViewById(R.id.comments_ET)).setText("");
                            }
                        }
                    });
                }
            }
        });
    }

    private void initialiseFields() {

        firestore=FirebaseFirestore.getInstance();

    }

    private void displayToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.comments_section_toolbar));
        getSupportActionBar().setTitle("Comments");
    }
}