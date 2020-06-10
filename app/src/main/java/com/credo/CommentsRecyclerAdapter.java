package com.credo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firestore;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_layout, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage=commentsList.get(position).getMessage();
        holder.setCommentMessage(commentMessage);

        String uid = commentsList.get(position).getUserID();
        firestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String name=task.getResult().getString("name");
                    String userImage=task.getResult().getString("profile image");
                    holder.setCommentatorData(name,userImage);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
       if(commentsList!=null)
           return commentsList.size();
       else
           return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView commentMessageTV, usernameTV;
        private CircleImageView profileImageIV;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setCommentMessage(String commentMessage) {
            commentMessageTV=view.findViewById(R.id.comment_message_TV);
            commentMessageTV.setText(commentMessage);
        }

        public void setCommentatorData(String name, String userImage) {
            profileImageIV=(CircleImageView)view.findViewById(R.id.profile_image_comments_IV);
            usernameTV=(TextView)view.findViewById(R.id.username_comments_TV);

            usernameTV.setText(name);
            RequestOptions placeholderrequest=new RequestOptions();
            placeholderrequest.placeholder(R.drawable.com_facebook_profile_picture_blank_portrait);
            Glide.with(context).applyDefaultRequestOptions(placeholderrequest).load(userImage).into(profileImageIV);
        }
    }
}
