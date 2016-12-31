package com.wkbp.msq;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.adapter.DetailTalkAdapter;
import com.wkbp.msq.adapter.ViewPagerAdapter;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.customView.ObservableScrollView;
import com.wkbp.msq.result.bean.CommodityBean;
import com.wkbp.msq.result.bean.GsonShopDetailInfoBack;
import com.wkbp.msq.setting.LoginActivity;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;
import com.wkbp.msq.util.Util;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailInfoActivity extends BaseActivity implements View.OnClickListener,
        ObservableScrollView.ScrollViewListener, ViewPagerAdapter.OnPagerItemClickListener {
    private ObservableScrollView scrollView;
    private RelativeLayout title_bar;//标题栏布局
    private TextView custom_title_text;//标题栏文字
    private FrameLayout banner_frameLayout;
    private ViewPager banner_Pager;//商品头部图片
    private ViewPager detail_pager;//详情和评价下的pager
    private DetailPagerAdapter mDetailPagerAdapter;
    private TextView order_title;//商品标题
    private TextView now_price;//现价
    private TextView old_price;//原价
    private TextView shop_address;//商品地址
    private TextView order_sales;//已售
    private TextView tv_detail;//详情和评价选择按钮的详情
    private TextView tv_evaluate;//详情和评价选择按钮的评价
    private LinearLayout ll_collect;//收藏的布局
    private ImageView iv_collect;//收藏的图标
    private TextView tv_collect;//收藏的文字
    private TextView add_shopCar;//加入购物车
    private TextView tv_buy;//立即购买

    private PopupWindow popupWindow;
    private ImageView[] mImagViews; //轮播图片
    private ImageView[] mDots; //轮播图下边小圆点
    private LinearLayout layoutDots;
    private int imageHeight;
    private boolean isScroll;

    private TextView popCount;//购买数量
    private ImageView popSub;
    private int count;//购买数量

    private CommodityBean commodity; //上个界面传过来的商品类
    private String userName;  //用户id
    private List<GsonShopDetailInfoBack.commodityImage> imageList = new ArrayList<>();
    private String webUrl;
    private WebView webView;
    private ListView talkListView;//评价的listView
    private List<View> mDetailList = new ArrayList<>();//商品和评价的View
    private List<GsonShopDetailInfoBack.Talk> mTalkList = new ArrayList<>(); //评价就列表
    private DetailTalkAdapter mTalkAdapter;
    private boolean isCollect;//是否收藏
    private int isBuy;  //1表示加入购物车,2表示立即购买
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    initViewPager();
                    initDots();
                    initAllDetailWebview();
                    if (isCollect) {
                        iv_collect.setImageResource(R.drawable.order_collect);
                    } else {
                        iv_collect.setImageResource(R.drawable.order_uncollect);
                    }
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    Bundle bundle = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    break;
                case Constant.BANNER_SCROLL:
                    int index = banner_Pager.getCurrentItem();
                    banner_Pager.setCurrentItem(index + 1);
                    handler.sendEmptyMessageDelayed(Constant.BANNER_SCROLL, 3000);
                    break;
                case Constant.COLLECT_SUCCESS:
                    stopProgressDialog();
                    if (isCollect) {
                        iv_collect.setImageResource(R.drawable.order_uncollect);
                        ToastUtil.showMessageDefault(mContext, "取消收藏成功");
                    } else {
                        iv_collect.setImageResource(R.drawable.order_collect);
                        ToastUtil.showMessageDefault(mContext, "收藏成功");
                    }
                    isCollect = !isCollect;
                    break;
                case Constant.ADD_SHOPCAR_SUCCESS:
                    stopProgressDialog();
                    popupWindow.dismiss();
                    ToastUtil.showMessageDefault(mContext, "加入购物车成功");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_detail_info);
        setTitleBar("商品详情");
        commodity = (CommodityBean) getIntent().getSerializableExtra("commodity");
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        requestShopDetailInfo();
        initView();
        initTitleChange();
    }


    private void initView() {
        scrollView = (ObservableScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new TouchListenerScrollView());
        title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        banner_frameLayout = (FrameLayout) findViewById(R.id.banner_frameLayout);
        custom_title_text = (TextView) findViewById(R.id.custom_title_text);
        layoutDots = (LinearLayout) findViewById(R.id.dotLayout);
        banner_Pager = (ViewPager) findViewById(R.id.banner_Pager);
        detail_pager = (ViewPager) findViewById(R.id.detail_pager);
        mDetailList = initDetailViews();
        mDetailPagerAdapter = new DetailPagerAdapter();
        detail_pager.setAdapter(mDetailPagerAdapter);
        detail_pager.setOnPageChangeListener(mPageChangeListener);
        detail_pager.setCurrentItem(0);

        order_title = (TextView) findViewById(R.id.order_title);
        now_price = (TextView) findViewById(R.id.now_price);
        old_price = (TextView) findViewById(R.id.old_price);
        shop_address = (TextView) findViewById(R.id.shop_address);
        order_sales = (TextView) findViewById(R.id.order_sales);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_evaluate = (TextView) findViewById(R.id.tv_evaluate);
        tv_collect = (TextView) findViewById(R.id.tv_collect);
        add_shopCar = (TextView) findViewById(R.id.add_shopCar);
        tv_buy = (TextView) findViewById(R.id.tv_buy);
        ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        tv_detail.setOnClickListener(this);
        tv_evaluate.setOnClickListener(this);
        ll_collect.setOnClickListener(this);
        add_shopCar.setOnClickListener(this);
        tv_buy.setOnClickListener(this);

        order_title.setText(commodity.getCommodityName());
        now_price.setText(String.format("现价:￥%1$s", commodity.getCommodityPrice()));
        old_price.setText(String.format("原价:￥%1$s", commodity.getCommodityMarketPrice()));
        old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        shop_address.setText(commodity.getCommodityAdress());
        order_sales.setText(String.format("销量%1$s", commodity.getCommoditySaleNum()));
    }

    /**
     * 获取商品详情的信息
     */
    private void requestShopDetailInfo() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "getShopDetailInfo");
            obj.put("commodityID", commodity.getCommodityID());
            obj.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonShopDetailInfoBack shopDetailInfo = gson.fromJson(new String(bytes), new
                        TypeToken<GsonShopDetailInfoBack>() {
                        }.getType());
                String result = shopDetailInfo.getResult();
                String resultNote = shopDetailInfo.getResultNote();
                if (result.equals("0")) {
                    imageList.clear();
                    imageList.addAll(shopDetailInfo.getCommodityImageList());
                    webUrl = shopDetailInfo.getCommodityDetail();
                    mTalkList.clear();
                    mTalkList.addAll(shopDetailInfo.getTalkList());
                    isCollect = shopDetailInfo.getIsFavourite().equals("0") ? true : false;
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("resultNote", resultNote);
                    msg.what = Constant.REFRESH_RESULT_FAIL;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(Constant.REFRESH_REQUEST_FAIL);
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                handler.sendEmptyMessage(Constant.REFRESH_NITEWORK_FAIL);
            }
        });
    }

    /**
     * 收藏商品
     */
    private void collectShop() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "saveFavourite");
            obj.put("commodityID", commodity.getCommodityID());
            obj.put("userName", userName);
            obj.put("stateType", isCollect ? "1" : "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    String result = obj.getString("result");
                    String resultNote = obj.getString("resultNote");
                    if (result.equals("0")) {
                        handler.sendEmptyMessage(Constant.COLLECT_SUCCESS);
                    } else {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("resultNote", resultNote);
                        msg.what = Constant.REFRESH_RESULT_FAIL;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(Constant.REFRESH_REQUEST_FAIL);
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                handler.sendEmptyMessage(Constant.REFRESH_NITEWORK_FAIL);
            }
        });
    }

    /**
     * 加入购物车
     */
    private void addShopCar() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "saveBuyCar");
            obj.put("commodityID", commodity.getCommodityID());
            obj.put("userName", userName);
            obj.put("commodityNum", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    String result = obj.getString("result");
                    String resultNote = obj.getString("resultNote");
                    if (result.equals("0")) {
                        handler.sendEmptyMessage(Constant.ADD_SHOPCAR_SUCCESS);
                    } else {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("resultNote", resultNote);
                        msg.what = Constant.REFRESH_RESULT_FAIL;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(Constant.REFRESH_REQUEST_FAIL);
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                handler.sendEmptyMessage(Constant.REFRESH_NITEWORK_FAIL);
            }
        });
    }

    /**
     * 获取顶部title高度后，设置滚动监听
     */
    private void initTitleChange() {
        ViewTreeObserver vto = banner_frameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                title_bar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageHeight = banner_frameLayout.getHeight();
                scrollView.setScrollViewListener(OrderDetailInfoActivity.this);
            }
        });
    }


    /**
     * 初始化banner_Pager图片
     */
    private void initViewPager() {
        mImagViews = new ImageView[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            ImageView iv = new ImageView(mContext);
           ImageLoader.getInstance().displayImage(imageList.get(i).getCommodityImageUrl(), iv);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            mImagViews[i] = iv;
        }

        ViewPagerAdapter myAdapter = new ViewPagerAdapter(mImagViews);
        myAdapter.setOnPagerItemClickListener(this);
        banner_Pager.setAdapter(myAdapter);
        banner_Pager.setCurrentItem(mImagViews.length * 50);
        handler.sendEmptyMessageDelayed(Constant.BANNER_SCROLL, 3000);

        banner_Pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                setCurrentDot(arg0 % mImagViews.length);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 初始化底部小圆点
     */
    private void initDots() {
        mDots = new ImageView[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            ImageView iv = new ImageView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
            lp.leftMargin = 20;
            lp.rightMargin = 20;
            lp.topMargin = 20;
            lp.bottomMargin = 20;
            iv.setLayoutParams(lp);
            iv.setImageResource(R.drawable.dot_normal);
            layoutDots.addView(iv);
            mDots[i] = iv;
        }
        mDots[0].setImageResource(R.drawable.dot_selected);
    }

    /**
     * 设置ViewPager当前的底部小点
     */
    private void setCurrentDot(int currentPosition) {
        for (int i = 0; i < mDots.length; i++) {
            if (i == currentPosition) {
                mDots[i].setImageResource(R.drawable.dot_selected);
            } else {
                mDots[i].setImageResource(R.drawable.dot_normal);
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        boolean isLogin = SharedPreferenceUtils.getCurrentUserInfo(mContext).isLogin();
        if (id == R.id.ll_collect || id == R.id.add_shopCar || id == R.id.tv_buy) {
            if (isLogin) {
                switch (id) {
                    case R.id.ll_collect:
                        collectShop();
                        break;
                    case R.id.add_shopCar:
                        getPopupWindow();
                        isBuy = 1;
                        break;
                    case R.id.tv_buy:
                        getPopupWindow();
                        isBuy = 2;
                        break;
                }
            } else {
                ToastUtil.showMessageDefault(mContext, "请先登录");
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        }
        switch (id) {
            case R.id.tv_detail:
                detail_pager.setCurrentItem(0);
                break;
            case R.id.tv_evaluate:
                detail_pager.setCurrentItem(1);
                break;
            case R.id.popSub:
                count--;
                popCount.setText(String.valueOf(count));
                if (count <= 1) {
                    popSub.setEnabled(false);
                }
                break;
            case R.id.popAdd:
                count++;
                popSub.setEnabled(true);
                popCount.setText(String.valueOf(count));
                break;
            case R.id.popConfirm:
                if (isBuy == 1) {
                    addShopCar();
                } else {
                    Intent intent = new Intent(mContext, ConfirmBuyActivity.class);
                    commodity.setCommodityNum(String.valueOf(count));
                    List<CommodityBean> commodityList = new ArrayList<>();
                    commodityList.add(commodity);
                    intent.putExtra("commodityList", (Serializable) commodityList);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
                break;
        }
    }

    /**
     * 加如购物车或者购买的PopupWindow
     */
    public void getPopupWindow() {
        popupWindow = new PopupWindow(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.buy_popupwindow, null);
        popupWindow.setContentView(view);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        backgroundAlpha(0.5f);

        popupWindow.setAnimationStyle(R.style.PopupWindow);
        popupWindow.showAtLocation(scrollView, Gravity.BOTTOM, 0, 0);
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopOnDismissListner());

        ImageView popIcon = (ImageView) view.findViewById(R.id.popIcon);
        popSub = (ImageView) view.findViewById(R.id.popSub);
        ImageView popAdd = (ImageView) view.findViewById(R.id.popAdd);
        TextView popTitle = (TextView) view.findViewById(R.id.popTitle);
        TextView popPrice = (TextView) view.findViewById(R.id.popPrice);

        ImageLoader.getInstance().displayImage(commodity.getCommodityIcon(), popIcon);
        popTitle.setText(commodity.getCommodityName());
        popPrice.setText(String.format("￥%1$s", commodity.getCommodityPrice()));

        popCount = (TextView) view.findViewById(R.id.popCount);
        TextView popConfirm = (TextView) view.findViewById(R.id.popConfirm);
        popSub.setOnClickListener(this);
        popAdd.setOnClickListener(this);
        popConfirm.setOnClickListener(this);

        count = 1;
        popCount.setText(String.valueOf(count));
        popSub.setEnabled(false);

    }

    /**
     * popupWindow弹出时，其他地方变暗
     *
     * @param alpha
     */
    private void backgroundAlpha(float alpha) {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = alpha;
        this.getWindow().setAttributes(params);
    }


    /**
     * popupWindow关闭事件
     */
    private class PopOnDismissListner implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    /**
     * 控制Title渐变的监听
     */
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {
            title_bar.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));
            custom_title_text.setTextColor(Color.argb((int) 255, 106, 57, 6));
        } else if (y > 0 && y <= imageHeight) {
            float scale = (float) y / imageHeight;
            //float alpha = (255 * scale);
            float alpha = (255 * (1 - scale));
            // 只是layout背景透明(仿知乎滑动效果)
            title_bar.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
            custom_title_text.setTextColor(Color.argb((int) alpha, 106, 57, 6));
        } else {
            title_bar.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
            custom_title_text.setTextColor(Color.argb((int) 0, 106, 57, 6));
        }
    }

    /**
     * 动态加载详情和评价的View布局
     *
     * @return
     */
    private List<View> initDetailViews() {
        List<View> viewsList = new ArrayList<>();
        View v1 = LayoutInflater.from(mContext).inflate(R.layout.goods_fragment_one, null);
        webView = (WebView) v1.findViewById(R.id.webView);
        webView.setOnTouchListener(new TouchListenerWebView());
        initWebViewSetting(webView.getSettings());
        viewsList.add(v1);

        View v2 = LayoutInflater.from(mContext).inflate(R.layout.goods_fragment_two, null);
        talkListView = (ListView) v2.findViewById(R.id.id_goods_buy_detail_list);
        mTalkAdapter = new DetailTalkAdapter(mContext, mTalkList);
        talkListView.setAdapter(mTalkAdapter);
        TextView tv = (TextView) v2.findViewById(R.id.id_goods_buy_detail_list_empty);
        talkListView.setEmptyView(tv);
        viewsList.add(v2);

        return viewsList;
    }

    /**
     * webView的设置
     *
     * @param ws
     */
    private void initWebViewSetting(WebSettings ws) {
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ws.setBuiltInZoomControls(false);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
    }

    /**
     * 详情和评价加载数据
     */
    private void initAllDetailWebview() {
        webView.loadUrl(webUrl);
        mTalkAdapter.notifyDataSetChanged();
        mDetailPagerAdapter.notifyDataSetChanged();
    }

    /**
     * 详细，评价内容的adapter
     */
    class DetailPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mDetailList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mDetailList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mDetailList.get(position));
            return mDetailList.get(position);
        }
    }

    /**
     * 详情和评价pager的监听
     */
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager
            .OnPageChangeListener() {


        @Override
        public void onPageSelected(int index) {
            switch (index) {
                case 0:
                    tv_detail.setBackgroundResource(R.drawable.gridview_bg_1);
                    tv_evaluate.setBackgroundResource(R.drawable.gridview_bg);
                    tv_detail.setTextColor(getResources().getColor(R.color.white));
                    tv_evaluate.setTextColor(getResources().getColor(R.color.brown));
                    break;
                case 1:
                    tv_detail.setBackgroundResource(R.drawable.gridview_bg);
                    tv_evaluate.setBackgroundResource(R.drawable.gridview_bg_1);
                    tv_detail.setTextColor(getResources().getColor(R.color.brown));
                    tv_evaluate.setTextColor(getResources().getColor(R.color.white));
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * scrollView的触摸监听
     */
    private class TouchListenerScrollView implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int scrollY = v.getScrollY();
//                    scrollY = Util.px2dip(mContext,scrollY);
//                    Log.e("shang", "scrollY = " + scrollY);
                    if (scrollY >= Util.dip2px(mContext, 348)) {
                        isScroll = true;
                    } else {
                        isScroll = false;
                    }
                    break;
            }
            return false;
        }
    }

    /**
     * webView的触摸监听
     */
    public class TouchListenerWebView implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Log.e("shang", "isScroll = " + isScroll);
            if (isScroll) {
                scrollView.requestDisallowInterceptTouchEvent(true);
            } else {
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
//            Log.e("shang", "scrollY = " + v.getScaleY());
            if (event.getAction() == MotionEvent.ACTION_UP && v.getScaleY() < 10) {
                isScroll = false;
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    /**
     * 头部轮播图的点击回调接口
     *
     * @param position
     */
    @Override
    public void onPagerItemClick(int position) {
        //pagerAdapter中图片的点击事件
    }
}
