package com.tm.demo.swiprefreshlayout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tm.demo.swiprefreshlayout.R;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

	private int count = 20;

	public MainRecyclerAdapter() {

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent,false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		setData(holder, position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return count;
	}

	private void setData(final ViewHolder holder, final int position) {

		holder.tv.setText(position + "");
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView tv;

		public ViewHolder(View itemView) {
			super(itemView);
			tv = itemView.findViewById(R.id.tv);
		}
	}

	public void setCount(int count) {
		this.count = count;
		notifyDataSetChanged();
	}

	public void addCount() {
		this.count += 20;
		notifyDataSetChanged();
	}

	public int getCount() {
		return count;
	}

}
