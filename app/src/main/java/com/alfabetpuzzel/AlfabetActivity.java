package com.alfabetpuzzel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.alfabetpuzzel.Model.ItemModel;
import com.alfabetpuzzel.Model.MyItem;
import com.alfabetpuzzel.adapter.AlfabetAdapter;

import java.util.ArrayList;

public class AlfabetActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    ArrayList<ItemModel> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alfabet);



        recyclerView = findViewById(R.id.rcy_alfabet);
        recyclerView.setHasFixedSize(true);

        recylerViewLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        data = new ArrayList<>();
        for (int i = 0; i < MyItem.iconList.length; i++) {
            data.add(new ItemModel(
                    MyItem.iconList[i],
                    MyItem.sound[i]
            ));
        }

        recyclerViewAdapter = new AlfabetAdapter(data);
//        recyclerView.setClickListener(th);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
