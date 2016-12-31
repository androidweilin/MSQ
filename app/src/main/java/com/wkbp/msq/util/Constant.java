package com.wkbp.msq.util;

import android.os.Environment;

/**
 * Created by shangshuaibo on 2016/11/22 10:48
 */
public class Constant {
    public static final int SPLASH_TIMER = 999999;//欢迎界面倒计时
    public static final int BANNER_SCROLL = 1000000;//轮播图滚动
    public static final int REFRESH_REQUEST_SUCCESS = 1000001;//刷新请求成功
    public static final int REFRESH_REQUEST_FAIL = 1000002;//刷新请求失败
    public static final int REFRESH_RESULT_FAIL = 1000003;//刷新返回失败
    public static final int REFRESH_NITEWORK_FAIL = 1000004;//刷新网络不给力
    public static final int LOAD_REQUEST_SUCCESS = 1000005;//加载请求成功
    public static final int LOAD_REQUEST_FAIL = 1000006;//加载请求失败
    public static final int LOAD_RESULT_FAIL = 1000007;//加载返回失败
    public static final int LOAD_NITEWORK_FAIL = 1000008;//加载网络不给力
    public static final int LOAD_NO_MROE = 1000009;//没有更多数据
    public static final int COLLECT_SUCCESS = 1000010;//收藏/取消收藏成功
    public static final int ADD_SHOPCAR_SUCCESS = 1000011;//加入购物车成功
    public static final int CONFIRM_ORDER_SUCCESS = 1000012;//提交订单成功
    public static final int CONFIRM_ORDER_FAIL = 1000013;//提交订单成功
    public static final int CONFIRM_GET_ORDER_SUCCESS = 1000014;//确认收货成功

    public static final String APP_PATH = Environment.getExternalStorageDirectory() + "/mashaqi/";
    //public static final String APP_FILEPATH = APP_PATH + "/file/";
}
