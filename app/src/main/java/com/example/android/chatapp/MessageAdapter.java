package com.example.android.chatapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Никита on 30.03.2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private int mGravity;


    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;
    }

    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);


        return new MessageViewHolder(v);
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImage;
        public ImageView messageImage;


        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

        }
    }

    public void onBindViewHolder(final MessageViewHolder viewHolder, int i){
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        String from_user = c.getFrom();
        String message_type = c.getType();
        if(from_user.equals(current_user_id)){
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            viewHolder.messageText.setLayoutParams(params);
            viewHolder.profileImage.setVisibility(View.INVISIBLE);
        } else {

            viewHolder.messageText.setBackgroundResource(R.drawable.message_mine_background);

        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();

               Picasso.with(viewHolder.profileImage.getContext()).load(image)
                       .placeholder(R.drawable.icon_user).into(viewHolder.profileImage);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (message_type.equals("text")){
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.icon_user).into(viewHolder.messageImage);
        }
    }
    public int getItemCount(){
        return mMessageList.size();
    }
}
