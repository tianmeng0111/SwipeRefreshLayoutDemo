package com.tm.demo.swiprefreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * 创建盛放ViewFooter的View
     */
    private View mViewFooter;

    /**
     * listview实例
     */
    private ListView mListView;

    /**
     * RecyclerView实例
     */
    private RecyclerView mRecyclerView;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * 加载更多的监听器
     */
    public static interface OnLoadListener {
        public void onLoad();
    }
    /**
     * 设置加载监听
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    public MySwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }


    public MySwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取达到最下方的时候需要滑动的像素点
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        //获取ViewFooter的实例
        mViewFooter = LayoutInflater.from(context).inflate(R.layout.view_load_more, null, false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView,RecyclerView对象
        if (mListView == null || mRecyclerView == null) {
            getView();
        }
    }
    /**
     * 获取ListView , RecyclerView对象
     */
    private void getView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                //获取ListView实例
                mListView = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        //执行加载操作,具体操作后面会继续详解
                        if (canLoad()) {
                            loadData();
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                });

            }else if (childView instanceof RecyclerView){
                //获取RecyclerView实例
                mRecyclerView = (RecyclerView) childView;
                // 设置滚动监听器给RecyclerView, 使得滚动的情况下也可以自动加载
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        //执行加载操作
                        if (canLoad()) {
                            loadData();
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
            }
        }
    }

    /**
     * 按下坐标
     * dX按下X的坐标
     * dY按下Y的坐标
     * uX抬起X的坐标
     * uY抬起Y的坐标
     */
    private int dX = 0, dY = 0, uX = 0, uY = 0;
    /**
     * 是否为点击,避免点击时触发滑动效果
     */
    private boolean isMove = false;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    /**
     * 首页加载条数
     */
    private int mItemCount = -1;

    //根据dispatchTouchEvent获取按下抬起时的坐标值
    //根据MotionEvent获取按下抬起时的值
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                dX = (int) event.getX();
                dY = (int) event.getY();
                Log.e("TAG", "dX: " + dX + "   dY : " + dY);
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = false;
                // 移动
                if (canLoad()) {
                    loadData();
                }
                break;

            case MotionEvent.ACTION_UP:
                uX = (int) event.getX();
                uY = (int) event.getY();
                //如果不是点击时滑动的话将isMove设置为true
                if (uX != dX && uY != dY){
                    isMove = true;
                }
                Log.e("TAG", "uX: " + uX + "   uY : " + uY);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     *     设置可上滑的条数
     */
    public void setItemCount(int itemCount) {
        this.mItemCount = itemCount;
    }

    /**
     * 是否处于上滑状态
     * 在外部可以调用此办法判断是否在加载中
     * @return
     */
    public boolean getIsLoading(){
        return isLoading;
    }

    /**
     * 是否可以加载更多, 条件是否到了最底部, 是否正在执行上拉加载, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        boolean b = false;
        if (mListView != null && mListView.getAdapter() != null) {
            if (mItemCount > 0) {
                if (mListView.getAdapter().getCount() < mItemCount) {
                    // 第一页未满，禁止下拉
                    b = false;
                }else {
                    b = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
                }
            } else {
                // 未设置数据长度，则默认第一页数据不满时也可以上拉
                b = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
            }
            return b;
        }
        return false;
    }

    /**
     * 是否是上拉操作
     * 根据按下的Y轴坐标和抬起的Y轴坐标进行判断
     * 查看按下时Y轴坐标和抬起Y轴坐标是否大于最小滑动距离
     *
     * @return
     */
    private boolean isPullUp() {
        return (dY - uY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (isMove){
            if (mOnLoadListener != null) {
                // 设置状态
                setLoading(true);
                //执行加载操作
                mOnLoadListener.onLoad();
            }
        }
    }

    /**
     * @param loading
     * @方法说明:设置刷新
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            mListView.addFooterView(mViewFooter);
        } else {
            //设置取消
            mListView.removeFooterView(mViewFooter);
            uY = 0;
            dY = 0;
        }
    }



}
