package com.sagereal.factorymode.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.adapter.MyAdapter;

public class SingleTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_test);

        ImageButton backButton = findViewById(R.id.back);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.back:
                        finish();
                }
            }
        };
        backButton.setOnClickListener(onClickListener);

        final String[] citys = {"上海","北京","天津","江苏","河南","西藏","新疆","湖南","湖北"};

        RecyclerView recyclerView =findViewById(R.id.recyview);

        //设置线性布局LinearLayoutManager,也可以是GridLayoutManager(网格布局),StaggeredGridLayoutManager(瀑布流)
        LinearLayoutManager layoutManager= new LinearLayoutManager(SingleTestActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //设置适配器
        MyAdapter adapter =new MyAdapter(this,citys);
        recyclerView.setAdapter(adapter);

    }
}