package com.tm.demo.swiprefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tm.demo.swiprefreshlayout.adapter.MainRecyclerAdapter;
import com.tm.demo.swiprefreshlayout.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.leefeng.lfrecyclerview.LFRecyclerView;

public class LFRecyclerActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    LFRecyclerView recyclerView;
    private MainRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {

//        LinearLayoutManager layoutManager = new LinearLayoutManager(LFRecyclerActivity.this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setRefresh(true);
        recyclerView.setLoadMore(true);
        recyclerView.setAutoLoadMore(true);//设置为可上拉加载,默认false,调用这个方法false可以去掉底部的“加载更多”
        View view = View.inflate(LFRecyclerActivity.this, R.layout.view_load_no_more, null);
        recyclerView.setNoDateView(view);

        adapter = new MainRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.setLFRecyclerViewListener(new LFRecyclerView.LFRecyclerViewListener() {
            @Override
            public void onRefresh() {
                getData("refresh");
            }

            @Override
            public void onLoadMore() {
                getData("loadMore");
            }
        });
    }

    private void getData(final String type) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ("refresh".equals(type)) {//下拉刷新
                    recyclerView.stopRefresh(true);
                    adapter.setCount(20);
                } else if ("loadMore".equals(type)){//上拉加载
                    recyclerView.stopLoadMore();
                    if (adapter.getCount() > 100) {
                        recyclerView.setNoDateShow();
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.addCount();
                    }
                }
            }
        }, 2000);
    }
}
