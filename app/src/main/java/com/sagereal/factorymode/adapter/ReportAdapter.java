package com.sagereal.factorymode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    LayoutInflater mInflater; //声明布局填充器
    List<String> list;
    Context context; //声明上下文

    //构造方法
    public ReportAdapter(Context context, List<String> list) {
        mInflater = LayoutInflater.from(context); //获取布局服务
        this.list = list;
        this.context = context;
    }

    //用于创建ViewHolder实例，并加载Item布局
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.test_report_item, parent, false);
        ReportViewHolder reportViewHolder = new ReportViewHolder(view);
        return reportViewHolder;
    }

    //用于将获取的数据绑定到对应的控件上
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.itemText.setText(list.get(position));
    }

    //获取列表条目的总数
    @Override
    public int getItemCount() {
        return list.size();
    }


    //自定义内部类 ViewHolder
    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
        }
    }
}
