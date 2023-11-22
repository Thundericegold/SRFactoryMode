package com.sagereal.factorymode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    LayoutInflater mInflater; //声明布局填充器
    String[] list;
    Context context; //声明上下文


    //构造方法
    public MyAdapter(Context context, String[] list) {
        mInflater = LayoutInflater.from(context); //获取布局服务
        this.list = list;
        this.context = context;
    }

    //用于创建ViewHolder实例，并加载Item布局
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_test_item, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    //用于将获取的数据绑定到对应的控件上
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txt.setText(list[position]);

        //添加点击事件监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, list[position], Toast.LENGTH_SHORT).show();
            }
        });
    }


    //获取列表条目的总数
    @Override
    public int getItemCount() {
        return list.length;
    }


    //自定义内部类 ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
        }


    }
}
