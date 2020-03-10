package com.tm.demo.swiprefreshlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter2 extends BaseAdapter {

    private Context context;
    private OnLoadMoreListener listener;

    private int count = 0;

    private final static int TYPE_CONTENT = 0;//正常内容
    private final static int TYPE_FOOTER = 1;//加载View

    private boolean canLoadMore = true;

    public MyAdapter2(Context context, OnLoadMoreListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == count && listener.isAllScreen()) {
            return TYPE_FOOTER;
        }
        return TYPE_CONTENT;
    }

    @Override
    public int getCount() {
        if (count >= 20) {
            return count + 1;
        } else {
            return count;
        }
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

        int viewType = getItemViewType(position);
        if (viewType == TYPE_FOOTER) {
            View view;
            if (canLoadMore) {
                view = LayoutInflater.from(context).inflate(R.layout.view_load_more, parent, false);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.view_load_no_more, parent, false);
            }
            return view;
        } else {
            convertView = View.inflate(context, R.layout.item_main, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            viewHolder.tv.setText(position + "");
            return convertView;
        }
    }

    protected static class ViewHolder {
        @BindView(R.id.tv)
        TextView tv;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void     addCount(int addCount) {
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

    public void setNoMoreData() {
        this.canLoadMore = false;
        notifyDataSetChanged();
    }
}
