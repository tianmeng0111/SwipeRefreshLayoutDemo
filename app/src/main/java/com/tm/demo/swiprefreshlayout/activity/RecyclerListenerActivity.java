package com.tm.demo.swiprefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tm.demo.swiprefreshlayout.DensityUtils;
import com.tm.demo.swiprefreshlayout.OnRecyclerLoadMoreListener;
import com.tm.demo.swiprefreshlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：JTL
 */
public class RecyclerListenerActivity extends AppCompatActivity {
    private static final String TAG = "RecyclerListenerActivity";

    private MyAdapter myAdapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Handler handler;
    private OnRecyclerLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener mListenerNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_listener);
        init();
    }

    private void init() {
        myAdapter = new MyAdapter();
        handler = new Handler();
        layoutManager = new LinearLayoutManager(this);

        refreshLayout = findViewById(R.id.swiperefreshlayout);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

        refreshLayout.setProgressViewOffset(false,
                DensityUtils.dp2px(RecyclerListenerActivity.this, 90),
                DensityUtils.dp2px(RecyclerListenerActivity.this, 130));
        //设置下拉时圆圈的颜色（可以尤多种颜色拼成）
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        //设置下拉时圆圈的背景颜色（这里设置成白色）
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("refresh");
            }
        });
        mOnLoadMoreListener = new OnRecyclerLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
//                recyclerView.addOnScrollListener(mListenerNull);
                recyclerView.removeOnScrollListener(mOnLoadMoreListener);
                getData("loadMore");
            }

            @Override
            protected void onFirstShow(boolean isFirstItemShow) {
//                if (isFirstItemShow) {
//                    recyclerView.setNestedScrollingEnabled(false);
//                } else {
//                    recyclerView.setNestedScrollingEnabled(true);
//                }
            }
        };

        mListenerNull = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        };
        recyclerView.addOnScrollListener(mOnLoadMoreListener);

        getData("reset");
    }


    private void getData(final String type) {
        if ("reset".equals(type)) {
            myAdapter.setCount(3);
        } else if ("refresh".equals(type)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myAdapter.setCount(20);
                    recyclerView.removeOnScrollListener(mOnLoadMoreListener);
                    recyclerView.addOnScrollListener(mOnLoadMoreListener);
                    myAdapter.refreshReset();
                }
            }, 2000);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (myAdapter.getCount() > 100) {
                        myAdapter.setNoMoreData();
                        recyclerView.removeOnScrollListener(mOnLoadMoreListener);
                    } else {
                        myAdapter.addCount();
                        recyclerView.addOnScrollListener(mOnLoadMoreListener);
                    }
                }
            }, 2000);

        }

        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_CONTENT = 0;//正常内容
        private final static int TYPE_FOOTER = 1;//加载View
        private final static int TYPE_HEADER = 2;//加载View

        private boolean canLoadMore = true;
        private int count = 0;

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (position == getItemCount() - 1 && mOnLoadMoreListener.isAllScreen()) {
                return TYPE_FOOTER;
            }
            return TYPE_CONTENT;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main_foot, parent, false);
                return new FootViewHolder(view);
            } else {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_include_header, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder(view);
                return myViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (getItemViewType(position) == TYPE_FOOTER) {
                FootViewHolder viewHolder = (FootViewHolder) holder;
                if (canLoadMore) {
                    viewHolder.tvNoMoreData.setVisibility(View.GONE);
                    viewHolder.contentLoadingProgressBar.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvNoMoreData.setVisibility(View.VISIBLE);
                    viewHolder.contentLoadingProgressBar.setVisibility(View.GONE);
                }
            } else if (getItemViewType(position) == TYPE_HEADER) {
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.viewHeader.setVisibility(View.VISIBLE);
                viewHolder.textView.setVisibility(View.GONE);
            } else {
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.viewHeader.setVisibility(View.GONE);
                viewHolder.textView.setVisibility(View.VISIBLE);
                viewHolder.textView.setText("第" + position + "行");
            }
        }

        @Override
        public int getItemCount() {
            return count + 2;
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

        public void setNoMoreData() {
            this.canLoadMore = false;
            notifyDataSetChanged();
        }

        public void refreshReset() {
            this.canLoadMore = true;
            notifyDataSetChanged();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private View viewHeader;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textItem);
            viewHeader = itemView.findViewById(R.id.view_header);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        ContentLoadingProgressBar contentLoadingProgressBar;
        private TextView tvNoMoreData;

        public FootViewHolder(View itemView) {
            super(itemView);
            contentLoadingProgressBar = itemView.findViewById(R.id.pb_progress);
            tvNoMoreData = itemView.findViewById(R.id.tv_no_more_data);
        }
    }
}
