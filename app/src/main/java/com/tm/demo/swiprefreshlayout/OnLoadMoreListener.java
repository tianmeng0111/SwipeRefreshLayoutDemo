package com.tm.demo.swiprefreshlayout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @作者JTL.
 * @日期2017/12/1.
 * @说明：加载更多接口
 */

public abstract class OnLoadMoreListener implements ListView.OnScrollListener {
    private static final String TAG = "OnLoadMoreListener";

    private int countItem;
    private int lastItem;
    private boolean isScrolled = false;//是否可以滑动
    private boolean isAllScreen = false;//是否充满全屏

    /**
     * 加载接口
     *
     * @param countItem 总数量
     * @param lastItem  最后显示的position
     */
    protected abstract void onLoading(int countItem, int lastItem);

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        countItem = totalItemCount;
//        lastItem = firstVisibleItem + visibleItemCount;

        lastItem = view.getLastVisiblePosition();

        Log.e(TAG, "onScroll: --->>" + lastItem);

        if (isScrolled && countItem != lastItem && lastItem == countItem - 1) {
            onLoading(countItem, lastItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            isScrolled = true;
            isAllScreen =true;
            Log.e(TAG, "onScrollStateChanged: ------true");
        } else {
            isScrolled = false;
            Log.e(TAG, "onScrollStateChanged: ------false");
        }
    }

    public boolean isAllScreen(){
        return isAllScreen;
    }
}
