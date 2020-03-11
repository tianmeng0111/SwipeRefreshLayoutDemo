package com.tm.demo.swiprefreshlayout.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tm.demo.swiprefreshlayout.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    private int count = 20;

    public MyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_main, null);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder.tv.setText(position + "");

        return convertView;
    }

    protected static class ViewHolder {
        @BindView(R.id.tv)
        TextView tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void addCount(int addCount) {
        count += addCount;
        notifyDataSetChanged();
    }

    public void resetCount() {
        count = 20;
        notifyDataSetChanged();
    }

    public void resetCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }
}
