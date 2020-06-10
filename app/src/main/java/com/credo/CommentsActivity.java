package com.credo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private String blogPostDoc;
    private FirebaseFirestore firestore;
    private List<Comments> commentsList;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private RecyclerView commentsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        initialiseFields();
        displayToolbar();

        //to receive the blog post id from the recycler adapter activity
        blogPostDoc=getIntent().getStringExtra("blog_post_id");

        commentsRV.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        commentsRV.setAdapter(commentsRecyclerAdapter);

        //to post a comment
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

        //to display all comments in the recycler view
        firestore.collection("posts").document(blogPostDoc).collection("comments")
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(!queryDocumentSnapshots.isEmpty())

                        assert queryDocumentSnapshots != null;
                        for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                        {
                            if(doc.getType() == DocumentChange.Type.ADDED)
                            {
                                String commentID=doc.getDocument().getId();

                                Comments comments=doc.getDocument().toObject(Comments.class);
                                commentsList.add(comments);

                                commentsRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

    }

    private void initialiseFields() {
        firestore=FirebaseFirestore.getInstance();
        commentsList =new ArrayList<>();
        commentsRecyclerAdapter=new CommentsRecyclerAdapter(commentsList);
        commentsRV=findViewById(R.id.comments_RV);
    }

    private void displayToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.comments_section_toolbar));
        getSupportActionBar().setTitle("Comments");
    }
}