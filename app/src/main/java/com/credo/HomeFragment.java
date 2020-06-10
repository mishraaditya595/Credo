package com.credo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView homeFragmentRV;
    private List<BlogPost> blogPostList;
    private FirebaseFirestore firestore;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private DocumentSnapshot lastVisible;
    private boolean isFirstLoaded=true;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        initialiseFields(view);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            homeFragmentRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom=!recyclerView.canScrollVertically(1);
                    if(reachedBottom)
                    {
                        loadMorePosts();
                    }
                }
            });

            //the following line is to make the newest posts appear first and only 4 posts will be loaded at a time
            Query firstQuery=firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(4);

            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //getActivity() will attach the query to the activity so when we switch queries and come
                //back to this activity again, it will start from the beginning again
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if(isFirstLoaded)
                    {
                        //the last visible will be updated only if data is retrieved for the first time
                        lastVisible=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        blogPostList.clear();
                    }


                    assert queryDocumentSnapshots != null;
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                    {
                        if(doc.getType() == DocumentChange.Type.ADDED)
                        {
                            String blogPostID=doc.getDocument().getId();

                            BlogPost blogPost=doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                            if(isFirstLoaded)
                            {
                                //if data is retrieved for the first time, show it
                                blogPostList.add(blogPost);
                            }
                            else
                            {
                                //if existing data is already retrieved and we are retrieving the new data
                                blogPostList.add(0,blogPost); //the 0 here indicates the position where new daa will be shown
                                //0 means the top or first
                            }
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }

                    isFirstLoaded=false; //this is to indicate that data is retrieved once
                }
            });

        }

        return view;
    }

    private void initialiseFields(View view) {
        homeFragmentRV=view.findViewById(R.id.home_fragment_RV);
        blogPostList=new ArrayList<>();
        firestore=FirebaseFirestore.getInstance();
        blogRecyclerAdapter=new BlogRecyclerAdapter(blogPostList);
        homeFragmentRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeFragmentRV.setAdapter(blogRecyclerAdapter);
    }

    public void loadMorePosts(){
        //the following line is to make the newest posts appear first and only 4 posts will be loaded at a time
        Query nextQuery=firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(4);

        nextQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
            //getActivity() will attach the query to the activity so when we switch queries and come
            //back to this activity again, it will start from the beginning again
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty())
                {
                    //we will only load more posts if they exist else we will not

                    lastVisible=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);

                    assert queryDocumentSnapshots != null;
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                    {
                        if(doc.getType() == DocumentChange.Type.ADDED)
                        {
                            String blogPostID=doc.getDocument().getId();

                            BlogPost blogPost=doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                            blogPostList.add(blogPost);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}
