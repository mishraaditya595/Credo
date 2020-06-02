package com.credo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

   public List<BlogPost> blogPostList;

   public BlogRecyclerAdapter(List<BlogPost> blogPostList){
        this.blogPostList=blogPostList;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_layout, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       String descriptionText=blogPostList.get(position).getBlogDescription();
       holder.setDescriptionTV(descriptionText,holder);
    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

       private View view;
       private TextView descriptionTV;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        void setDescriptionTV(String text, ViewHolder holder){
            descriptionTV=(TextView) view.findViewById(R.id.blog_description_TV);
            descriptionTV.setText(text);
        }
    }
}
