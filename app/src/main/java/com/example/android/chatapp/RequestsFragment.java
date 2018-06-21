package com.example.android.chatapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRequestsLists;

    private View mMainView;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private DatabaseReference mRequestsDatabase;
    private DatabaseReference mUsersDatabase;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestsLists = (RecyclerView) mMainView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mRequestsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mRequestsLists.setHasFixedSize(true);
        mRequestsLists.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> requestsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(
                Requests.class,
                R.layout.users_single_layout,
                RequestsViewHolder.class,
                mRequestsDatabase
        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder requestsViewHolder, Requests requests, int i) {
                requestsViewHolder.setDate(requests.getDate());

                final String list_user_id = getRef(i).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {
                            String userOnline =  dataSnapshot.child("online").getValue().toString();
                            requestsViewHolder.setUserOnline(userOnline);
                        }
                        requestsViewHolder.setName(userName);
                        requestsViewHolder.setUserImage(userThumb, getContext());

                        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        };
        mRequestsLists.setAdapter(requestsRecyclerViewAdapter);
    }
    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RequestsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDate(String date){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);
        }
        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public  void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.icon_user).into(userImageView);
        }
        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }
    }

}
