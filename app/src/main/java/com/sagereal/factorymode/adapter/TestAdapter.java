package com.sagereal.factorymode.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.R;

import java.util.Map;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    LayoutInflater mInflater; //声明布局填充器
    String[] list;
    Context context; //声明上下文
    Map<Integer, Integer> statusList;

    private final ClickListener myClickListener;

    public interface ClickListener {
        void onClick(int position);
    }

    //构造方法
    public TestAdapter(Context context, String[] list, Map<Integer, Integer> statusList, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context); //获取布局服务
        this.list = list;
        this.statusList = statusList;
        this.context = context;
        this.myClickListener = clickListener;
    }

    //用于创建ViewHolder实例，并加载Item布局
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_test_item, parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    //用于将获取的数据绑定到对应的控件上
    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.itemText.setText(list[position]);
        Integer integer = statusList.get(position);
        if (integer == -1) {
            holder.itemHolder.setBackgroundColor(context.getColor(R.color.white));
        } else if (integer == 0) {
            holder.itemHolder.setBackgroundColor(context.getColor(R.color.test_pass));
        } else if (integer == 1) {
            holder.itemHolder.setBackgroundColor(context.getColor(R.color.test_fail));
        }

        //添加点击事件监听
        holder.itemView.setOnClickListener(v -> myClickListener.onClick(position));
    }

    //获取列表条目的总数
    @Override
    public int getItemCount() {
        return list.length;
    }


    //自定义内部类 ViewHolder
    public static class TestViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        LinearLayout itemHolder;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
            itemHolder = itemView.findViewById(R.id.item_holder);
        }
    }
}
