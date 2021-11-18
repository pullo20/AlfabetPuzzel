package com.alfabetpuzzel.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.alfabetpuzzel.Model.ItemModel;
import com.alfabetpuzzel.Model.MyItem;
import com.alfabetpuzzel.R;

import java.util.ArrayList;
import java.util.List;

public class AlfabetAdapter extends RecyclerView.Adapter<AlfabetAdapter.ViewHolder> {


    private final ArrayList<ItemModel> dataItem;
    private Context context;

//    private ItemClickListener mClickListener;


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textHead;
        TextView textSubhead;
        ImageView imageIcon;
        LinearLayout ly;

        ViewHolder(View v) {

            super(v);

//            textHead = v.findViewById(R.id.text_headline);
//            textSubhead = v.findViewById(R.id.txt_suara);
//            ly = v.findViewById(R.id.lyt);
            imageIcon = v.findViewById(R.id.img_alfabet);

        }

    }

    public AlfabetAdapter(ArrayList<ItemModel> data) {

        this.dataItem = data;
    }

    @NonNull
    @Override
    public AlfabetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_alfabet, parent, false);
        //myonClickListener
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemModel contact = dataItem.get(position);

//        final Resources res = context.getResources();
        TextView textHead = holder.textHead;
        TextView textSubhead = holder.textSubhead;
        ImageView imageIcon = holder.imageIcon;

//        textHead.setText(dataItem.get(position).getName());
//        textSubhead.setText(dataItem.get(position).getsuara());
        imageIcon.setImageResource(dataItem.get(position).getImage());
        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(v.getContext());
                View view_layout = layoutInflaterAndroid.inflate(R.layout.card_sound, null);

                ImageView img = view_layout.findViewById(R.id.img_sound);
                img.setImageResource(contact.getImage());
                MediaPlayer mp = MediaPlayer.create(v.getContext(),contact.getsuara());
                mp.start();
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(v.getContext());
                alertDialogBuilderUserInput.setView(view_layout);
                final  AlertDialog alertDialog = alertDialogBuilderUserInput.create();
                alertDialog.show();


            }
        });

    }

    @Override
    public int getItemCount() {

        return dataItem.size();
    }
}
