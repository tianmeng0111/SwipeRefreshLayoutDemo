package com.tm.demo.swiprefreshlayout.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tm.demo.swiprefreshlayout.DensityUtils;
import com.tm.demo.swiprefreshlayout.adapter.MyAdapter2;
import com.tm.demo.swiprefreshlayout.OnLvLoadMoreListener;
import com.tm.demo.swiprefreshlayout.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LvListenerActivity extends AppCompatActivity {

    private static final String TAG = "LvListenerActivity";

    @BindView(R.id.lv)
    ListView lv;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bar_layout)
    View barLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter2 adapter;
    private OnLvLoadMoreListener onLoadMoreListener;

    private int touchSlop;
    private int pullUpTouchSlop;
    private boolean isShow = true;//记录toolbar是否显示
    private ObjectAnimator mAnimator;

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

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        swipeRefreshLayout.setProgressViewOffset(false, DensityUtils.dp2px(LvListenerActivity.this, 90),
                DensityUtils.dp2px(LvListenerActivity.this, 130));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("refresh");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swipeRefreshLayout.setNestedScrollingEnabled(true);
        }

        onLoadMoreListener = new OnLvLoadMoreListener() {
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

        adapter = new MyAdapter2(LvListenerActivity.this, onLoadMoreListener);
//        adapter.setList(new ArrayList<String>(20));
        lv.setAdapter(adapter);
        View view  = new View(LvListenerActivity.this);
        view.setLayoutParams(new ViewGroup.LayoutParams(1, DensityUtils.dp2px(LvListenerActivity.this, 90)));
        view.setBackgroundColor(Color.TRANSPARENT);
        lv.addHeaderView(view);


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                //首次不会走setOnRefreshListener
                getData("refresh");
            }
        });

        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        pullUpTouchSlop = DensityUtils.dp2px(LvListenerActivity.this, 40);

        lv.setOnTouchListener(new View.OnTouchListener() {
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int director = -1;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startY = (int) event.getY();
                        Log.e(TAG, "onTouch: ---ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int lastY = (int) event.getY();
                        int dy = startY - lastY;
                        if (Math.abs(dy) > touchSlop) {
                            if (dy > 0) {
                                if (Math.abs(dy) > pullUpTouchSlop) {
                                    //上滑
                                    director = 1;
                                }
                            } else {
                                //下滑
                                director = 0;
                            }
                        }

                        if (director == 1) {
                            //上滑，隐藏toolbar
                            if (isShow) {
                                isShow = !isShow;
                                toolbarAnim(0);
                            }
                        } else if (director == 0){
                            //下滑，显示toolbar
                            if (!isShow){
                                isShow = !isShow;
                                toolbarAnim(1);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toolbarAnim(int type) {
        //先让之前的动画停止
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (type == 0) {
            //隐藏toolbar
            mAnimator = ObjectAnimator
                    .ofFloat(barLayout, "translationY", barLayout.getTranslationY(), - toolbar.getHeight());
            isShow = false;
        } else if (type == 1) {
            //显示toolbar
            mAnimator = ObjectAnimator
                    .ofFloat(barLayout,"translationY",barLayout.getTranslationY(),0);
            isShow = true;
        }
        mAnimator.start();
    }


    private void getData(final String type) {
        if (type.equals("loadMore")) {
            lv.setOnScrollListener(null);//不能再上拉加载
        } else if ("refresh".equals(type)){
            //重新刷新需要加上
            lv.setOnScrollListener(onLoadMoreListener);
            adapter.setRefreshReset();
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
