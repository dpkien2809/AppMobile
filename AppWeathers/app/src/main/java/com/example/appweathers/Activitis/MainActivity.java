package com.example.appweathers.Activitis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.appweathers.Adapters.HourlyAdapters;
import com.example.appweathers.Domains.Hourly;
import com.example.appweathers.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerview();
        setVariable();
    }

    private void setVariable(){
        TextView nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FutureActivity.class));
            }
        });
    }
    private void initRecyclerview(){
        ArrayList<Hourly> items=new ArrayList<>();

        items.add(new Hourly("9pm",28, "cloudy"));
        items.add(new Hourly("11pm", 29, "sunny"));
        items.add(new Hourly("12pm", 30, "wind"));
        items.add(new Hourly("1am", 29, "rainy"));
        items.add(new Hourly("2am", 27, "storm"));

        recyclerView=findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        adapterHourly=new HourlyAdapters(items);
        recyclerView.setAdapter(adapterHourly);
    }
}