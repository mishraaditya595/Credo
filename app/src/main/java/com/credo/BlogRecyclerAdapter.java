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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

   public List<BlogPost> blogPostList;
   public Context context;

   public BlogRecyclerAdapter(List<BlogPost> blogPostList){
        this.blogPostList=blogPostList;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_layout, parent,false);
       context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String titleText=blogPostList.get(position).getBlog_title();
        holder.setBlogTitleTV(titleText);

       String descriptionText=blogPostList.get(position).getBlog_description();
       holder.setDescriptionTV(descriptionText);

       String imageURL=blogPostList.get(position).getImage_url();
       holder.setBlogImage(imageURL);

       String username=blogPostList.get(position).getAuthor();
       holder.setusername(username);

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
       private TextView descriptionTV, titleTV, usernameTV, timestampTV;
       private ImageView blogImageIV;

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
            Glide.with(context).load(text).into(blogImageIV);
        }

        public void setusername(String username) {
            usernameTV=(TextView)view.findViewById(R.id.username_TV);
            final String[] authorName = new String[1];

            FirebaseFirestore db=FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(username);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            authorName[0] =document.get("name").toString();
                        }
                    }
            }});

            usernameTV.setText(authorName[0]);
        }

        public void setTimestamp(String timestamp) {
            timestampTV=(TextView)view.findViewById(R.id.timestamp_TV);
            timestampTV.setText(timestamp);
        }
    }
}
