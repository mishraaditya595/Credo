package com.credo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView homeFragmentRV;
    private List<BlogPost> blogPostList;
    private FirebaseFirestore firestore;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        initialiseFields(view);

        firestore.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                assert queryDocumentSnapshots != null;
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {
                        BlogPost blogPost=doc.getDocument().toObject(BlogPost.class);
                        blogPostList.add(blogPost);
                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

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
}
