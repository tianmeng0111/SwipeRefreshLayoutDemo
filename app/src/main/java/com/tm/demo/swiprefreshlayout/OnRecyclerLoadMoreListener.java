package com.tm.demo.swiprefreshlayout;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public abstract class OnRecyclerLoadMoreListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "OnLvLoadMoreListener";

    private int countItem;
    private int lastItem;
    private boolean isScrolled = false;//是否可以滑动
    private boolean isAllScreen = false;//是否充满全屏
    private RecyclerView.LayoutManager layoutManager;

    /**
     * 加载接口
     *
     * @param countItem 总数量
     * @param lastItem  最后显示的position
     */
    protected abstract void onLoading(int countItem, int lastItem);

    protected abstract void onFirstShow(boolean isFirstItemShow);

    @SuppressLint("LongLogTag")
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//        if (newState == SCROLL_STATE_IDLE){
//            Log.e(TAG,"SCROLL_STATE_IDLE,空闲");
//        }
//        else if (newState==SCROLL_STATE_DRAGGING){
//            Log.e(TAG,"SCROLL_STATE_DRAGGING,拖拽");
//        }
//        else if (newState==SCROLL_STATE_SETTLING){
//            Log.e(TAG,"SCROLL_STATE_SETTLING,固定");
//        }
//        else{
//            Log.e(TAG,"其它");
//        }
        //拖拽或者惯性滑动时isScolled设置为true
        if (newState == SCROLL_STATE_DRAGGING  || newState == SCROLL_STATE_SETTLING) {
            isScrolled = true;
            isAllScreen = true;
        } else {
            isScrolled = false;
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            layoutManager = recyclerView.getLayoutManager();
            countItem = layoutManager.getItemCount();
            lastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            Log.e(TAG, "onScrolled: --->>" + lastItem);
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
//            Log.e(TAG, "onScrolled: firstVisibleItemPosition--->>" + firstVisibleItemPosition);
            if (firstVisibleItemPosition == 0) {
                onFirstShow(true);
            } else {
                onFirstShow(false);
            }
        }
        if (isScrolled && countItem != lastItem && lastItem == countItem - 1) {
            onLoading(countItem, lastItem);
        }
    }

    public boolean isAllScreen(){
        return isAllScreen;
    }
}
