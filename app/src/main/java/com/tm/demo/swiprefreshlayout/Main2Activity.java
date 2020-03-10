package com.tm.demo.swiprefreshlayout;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter2 adapter;
    private OnLoadMoreListener onLoadMoreListener;

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        onLoadMoreListener = new OnLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    return;
                }
                getData("loadMore");
            }
        };
        lv.setOnScrollListener(onLoadMoreListener);

        adapter = new MyAdapter2(Main2Activity.this, onLoadMoreListener);
//        adapter.setList(new ArrayList<String>(20));
        lv.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("refresh");
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                //首次不会走setOnRefreshListener
                getData("reset");
            }
        });
    }

    private void getData(final String type) {
        if (type.equals("loadMore")) {
            lv.setOnScrollListener(null);//不能再上拉加载
        } else if ("refresh".equals(type)){
            //重新刷新需要加上
            lv.setOnScrollListener(onLoadMoreListener);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ("reset".equals(type)) {//第一次初始化
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.resetCount(3);
                } else if ("refresh".equals(type)) {//下拉刷新
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.resetCount();
                } else {//上拉加载
                    if (adapter.getCount() > 100) {
                        adapter.setNoMoreData();
                        lv.setOnScrollListener(null);//不能再上拉加载
                    } else {
                        adapter.addCount(20);
                        lv.setOnScrollListener(onLoadMoreListener);
                    }
                }
            }
        }, 2000);
    }
}
