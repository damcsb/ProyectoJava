package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class Adapter_Messages extends RecyclerView.Adapter<Holder_Messages> {

    private List<Message> listMessage = new ArrayList<>();
    public Context c;


    public Adapter_Messages(Context c) {
        this.c = c;
    }

    public void addMessage(Message m){
        listMessage.add(m);
        notifyItemInserted(listMessage.size());
    }

    @Override
    public Holder_Messages onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_messages, parent, false);
        return new Holder_Messages(v);
    }

    @Override
    public void onBindViewHolder(final Holder_Messages holder, final int position) {
        holder.getUsername().setText(listMessage.get(position).getUsername());
        holder.getMessage().setText(listMessage.get(position).getMessage());

        //Date format message
//        Long time = listMessage.get(position).getTime();
//        Date date = new Date(time);
//        SimpleDateFormat sdf = new SimpleDateFormat("k:mm");
//
//        holder.getMsgTime().setText(sdf.format(date));
    }



    @Override
    public int getItemCount() {
        return listMessage.size();
    }
}
