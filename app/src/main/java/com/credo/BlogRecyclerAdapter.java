package com.credo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

   public List<BlogPost> blogPostList;
   public Context context;
   private FirebaseFirestore firestore;
   private FirebaseAuth mAuth;

   public BlogRecyclerAdapter(List<BlogPost> blogPostList){
        this.blogPostList=blogPostList;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_layout, parent,false);
       context = parent.getContext();
       firestore=FirebaseFirestore.getInstance();
       mAuth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

       holder.setIsRecyclable(false); //to fix the delay

       final String blogPostID=blogPostList.get(position).blogPostID;
       final String currentUserID=mAuth.getCurrentUser().getUid();

        final String titleText=blogPostList.get(position).getBlog_title();
        holder.setBlogTitleTV(titleText);

       String descriptionText=blogPostList.get(position).getBlog_description();
       holder.setDescriptionTV(descriptionText);

       String imageURL=blogPostList.get(position).getImage_url();
       String thumbnailURl=blogPostList.get(position).getThumbnail_url();
       holder.setBlogImage(imageURL,thumbnailURl);

       String uid=blogPostList.get(position).getAuthor();
       firestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful())
               {
                   String name=task.getResult().getString("name");
                   String userImage=task.getResult().getString("profile image");
                   holder.setAuthorData(name,userImage);
               }
           }
       });

       long longTime=blogPostList.get(position).getTimestamp().getTime();
       String timestamp=android.text.format.DateFormat.format("MM/dd/yyyy",new Date(longTime)).toString();
       holder.setTimestamp(timestamp);

       //get likes count
        firestore.collection("posts").document(blogPostID).collection("likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            //this means there are likes for the post
                            holder.updateLikesCount(queryDocumentSnapshots.size());
                        }
                        else
                        {
                            //this means there are no likes for the post
                            holder.updateLikesCount(0);
                        }
                    }
                });

       //set the colour of like button for liked and not liked posts
        firestore.collection("posts").document(blogPostID).collection("likes").document(currentUserID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists())
                        {
                            //if already liked, change the like button to the accented one
                            holder.blogLikeButton.setImageDrawable(context.getDrawable(R.mipmap.ic_like_button_accented));
                        }
                        else
                        {
                            //if has not liked, keep the like button as it is
                            holder.blogLikeButton.setImageDrawable(context.getDrawable(R.mipmap.ic_like_button));
                        }
                    }
                });


       //like feature implementation here
        holder.blogLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("posts").document(blogPostID).collection("likes").document(currentUserID)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!Objects.requireNonNull(task.getResult()).exists())
                        {
                            //if user has not liked the blog post, we will add a like
                            Map<String, Object> likesMap=new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firestore.collection("posts").document(blogPostID).collection("likes")
                                    .document(currentUserID).set(likesMap);
                        }
                        else
                        {
                            //this is if the user has already liked the blog post, tapping on the button again will unlike it
                            firestore.collection("posts").document(blogPostID).collection("likes")
                                    .document(currentUserID).delete();
                        }
                    }
                });

            }
        });

        //comment feature implementation here
        holder.blogCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CommentsActivity.class)
                        .putExtra("blog_post_id",blogPostID));
            }
        });


    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

       private View view;
       private TextView descriptionTV, titleTV, usernameTV, timestampTV, username;
       private ImageView blogImageIV;
       private CircleImageView profileImageIV;
       private ImageView blogLikeButton, blogCommentsButton;
       private TextView blogLikeCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;

            blogLikeButton=view.findViewById(R.id.like_button_IV);
            blogCommentsButton=view.findViewById(R.id.comments_button);
        }

        void setBlogTitleTV(String text){
            titleTV=(TextView) view.findViewById(R.id.blog_Title_TV);
            titleTV.setText(text);
        }

        void setDescriptionTV(String text){
            descriptionTV=(TextView) view.findViewById(R.id.blog_description_TV);
            descriptionTV.setText(text);
        }

        void setBlogImage(String imageURL, String thumbnailURL){
            blogImageIV=(ImageView)view.findViewById(R.id.blog_image_IV);

            RequestOptions placeholderrequest=new RequestOptions();
            placeholderrequest.placeholder(R.drawable.com_facebook_button_icon);
            Glide.with(context).applyDefaultRequestOptions(placeholderrequest).load(imageURL).thumbnail(Glide.with(context).load(thumbnailURL)).into(blogImageIV);
        }

        public void setTimestamp(String timestamp) {
            timestampTV=(TextView)view.findViewById(R.id.timestamp_TV);
            timestampTV.setText(timestamp);
        }

        public void setAuthorData(String name, String userImage) {
            profileImageIV=(CircleImageView)view.findViewById(R.id.author_profile_image_IV);
            usernameTV=(TextView)view.findViewById(R.id.username_TV);

            usernameTV.setText(name);
            RequestOptions placeholderrequest=new RequestOptions();
            placeholderrequest.placeholder(R.drawable.com_facebook_profile_picture_blank_portrait);
            Glide.with(context).applyDefaultRequestOptions(placeholderrequest).load(userImage).into(profileImageIV);
        }

        public void updateLikesCount(int count)
        {
            blogLikeCount=view.findViewById(R.id.like_count_TV);
            blogLikeCount.setText(count+" Likes");
        }
    }
}
