package com.credo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

   public List<BlogPost> blogPostList;
   public Context context;
   private FirebaseFirestore firestore;

   public BlogRecyclerAdapter(List<BlogPost> blogPostList){
        this.blogPostList=blogPostList;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_layout, parent,false);
       context = parent.getContext();
       firestore=FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String titleText=blogPostList.get(position).getBlog_title();
        holder.setBlogTitleTV(titleText);

       String descriptionText=blogPostList.get(position).getBlog_description();
       holder.setDescriptionTV(descriptionText);

       String imageURL=blogPostList.get(position).getImage_url();
       holder.setBlogImage(imageURL);

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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        void setBlogTitleTV(String text){
            titleTV=(TextView) view.findViewById(R.id.blog_Title_TV);
            titleTV.setText(text);
        }

        void setDescriptionTV(String text){
            descriptionTV=(TextView) view.findViewById(R.id.blog_description_TV);
            descriptionTV.setText(text);
        }

        void setBlogImage(String text){
            blogImageIV=(ImageView)view.findViewById(R.id.blog_image_IV);

            RequestOptions placeholderrequest=new RequestOptions();
            placeholderrequest.placeholder(R.drawable.com_facebook_button_icon);
            Glide.with(context).load(text).into(blogImageIV);
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
    }
}
