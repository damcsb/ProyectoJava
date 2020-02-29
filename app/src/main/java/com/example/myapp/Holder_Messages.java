package com.example.myapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class Holder_Messages extends RecyclerView.ViewHolder {

    private TextView username;
    private TextView message;
    private TextView msgTime;


    public Holder_Messages(View itemView) {
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.msgUser);
        message = (TextView) itemView.findViewById(R.id.msgMessage);
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public TextView getMessage() {
        return message;
    }

    public void setMessage(TextView message) {
        this.message = message;
    }

}
