package com.zhiyu.fire.util;

import android.view.View;

/**
 * Created by Administrator on 2017/6/9 0009.
 * RecyclerView的点击事件监听接口
 */

public interface OnItemClickListener {
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}