package com.tm.demo.swiprefreshlayout.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tm.demo.swiprefreshlayout.DensityUtils;
import com.tm.demo.swiprefreshlayout.adapter.MyAdapter;
import com.tm.demo.swiprefreshlayout.MySwipeRefreshLayout;
import com.tm.demo.swiprefreshlayout.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout)
    MySwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.lv)
    ListView lv;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        swipeRefreshLayout.setProgressViewOffset(false, DensityUtils.dp2px(MainActivity.this, 50), DensityUtils.dp2px(MainActivity.this, 100));
        //最多4中 一圈一种色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.resetCount();
                    }
                }, 1000);
            }
        });

        swipeRefreshLayout.setItemCount(20);//上拉加载20条

        swipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    return;
                }
                getData();
            }
        });

        adapter = new MyAdapter(MainActivity.this);
//        adapter.setList(new ArrayList<String>(20));
        lv.setAdapter(adapter);
        View view  = new View(MainActivity.this);
        view.setLayoutParams(new ViewGroup.LayoutParams(1, DensityUtils.dp2px(MainActivity.this, 50)));
        view.setBackgroundColor(Color.TRANSPARENT);
        lv.addHeaderView(view);
    }


    private void getData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setLoading(false);
                adapter.addCount(20);
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_go_activity_listview) {
            startActivity(new Intent(MainActivity.this, LvListenerActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_go_activity_lfrecycler) {
            startActivity(new Intent(MainActivity.this, LFRecyclerActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_go_activity_recycler_listener) {
            startActivity(new Intent(MainActivity.this, RecyclerListenerActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
