package com.credo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_layout, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);



        String commentMessage=commentsList.get(position).getMessage();
        holder.setCommentMessage(commentMessage);
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
        private TextView commentMessageTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setCommentMessage(String commentMessage) {
            commentMessageTV=view.findViewById(R.id.comment_message_TV);
            commentMessageTV.setText(commentMessage);
        }
    }
}
