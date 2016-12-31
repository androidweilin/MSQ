package com.wkbp.msq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.adapter.GridAdapter;
import com.wkbp.msq.adapter.ViewPagerAdapter;
import com.wkbp.msq.base.BaseFragment;
import com.wkbp.msq.customView.GridViewForScrollView;
import com.wkbp.msq.result.bean.CommodityBean;
import com.wkbp.msq.result.bean.GsonHomeBean;
import com.wkbp.msq.swipe.SwipeRefreshLayoutUpDown;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/17 16:58
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayoutUpDown.OnRefreshListener, SwipeRefreshLayoutUpDown.OnLoadListener,
        AdapterView.OnItemClickListener, ViewPagerAdapter.OnPagerItemClickListener {
    private View view;
    private ViewPager pager;
    private ViewPagerAdapter myPagerAdapter;
    private EditText serach_edit;
    private ImageView[] mImagViews;
    private ImageView[] mDots;
    private LinearLayout layoutDots;
    private LinearLayout ll_mashaqi, ll_gusha, ll_mucun;
    private int nowPage = 1;
    private int totalPage = 0;
    private List<CommodityBean> homeData = new ArrayList<>();
    private List<CommodityBean> bannerData = new ArrayList<>();
    private ScrollView scrollView;
    private GridViewForScrollView homeGridView;
    private GridAdapter homeAdapter;
    private SwipeRefreshLayoutUpDown home_swipe;
    private boolean isFirst;//判断是不是已经请求过数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    home_swipe.setRefreshing(false);
                    if (isFirst) {
                        homeAdapter.notifyDataSetChanged();
                        scrollView.smoothScrollTo(0, 0);
                    } else {
                        initViewPager();
                        initDots();
                        homeAdapter.notifyDataSetChanged();
                        scrollView.smoothScrollTo(0, 0);
                        isFirst = true;
                    }

                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    home_swipe.setRefreshing(false);
                    Bundle bundle = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    home_swipe.setRefreshing(false);
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    home_swipe.setRefreshing(false);
                    break;
                case Constant.LOAD_REQUEST_SUCCESS:
                    stopProgressDialog();
                    home_swipe.setLoading(false);
                    homeAdapter.notifyDataSetChanged();
                    break;
                case Constant.LOAD_RESULT_FAIL:
                    stopProgressDialog();
                    home_swipe.setLoading(false);
                    Bundle bundle1 = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle1.get("resultNote"));
                    break;
                case Constant.LOAD_REQUEST_FAIL:
                    stopProgressDialog();
                    home_swipe.setLoading(false);
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.LOAD_NITEWORK_FAIL:
                    stopProgressDialog();
                    home_swipe.setLoading(false);
                    break;
                case Constant.LOAD_NO_MROE:
                    stopProgressDialog();
                    home_swipe.setLoading(false);
                    ToastUtil.showMessageDefault(mContext, "没有更多数据了");
                    break;
                case Constant.BANNER_SCROLL:
                    int index = pager.getCurrentItem();
                    pager.setCurrentItem(index + 1);
                    handler.sendEmptyMessageDelayed(Constant.BANNER_SCROLL, 3000);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        layoutDots = (LinearLayout) view.findViewById(R.id.dotLayout);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        load();
    }

    private void initView() {
        ll_mashaqi = (LinearLayout) view.findViewById(R.id.ll_mashaqi);
        ll_gusha = (LinearLayout) view.findViewById(R.id.ll_gusha);
        ll_mucun = (LinearLayout) view.findViewById(R.id.ll_mucun);
        ll_mashaqi.setOnClickListener(this);
        ll_gusha.setOnClickListener(this);
        ll_mucun.setOnClickListener(this);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        homeGridView = (GridViewForScrollView) view.findViewById(R.id.gridView);
        homeAdapter = new GridAdapter(mContext, homeData);
        homeGridView.setAdapter(homeAdapter);
        homeGridView.setOnItemClickListener(this);

        home_swipe = (SwipeRefreshLayoutUpDown) view.findViewById(R.id.home_swipe);
        home_swipe.setOnRefreshListener(this);
        home_swipe.setOnLoadListener(this);
        home_swipe.setColor(R.color.swipe1, R.color.swipe2, R.color.swipe3, R.color.swipe4);
        home_swipe.setMode(SwipeRefreshLayoutUpDown.Mode.BOTH);
        home_swipe.setLoadNoFull(false);

        serach_edit = (EditText) view.findViewById(R.id.serach_edit);
        serach_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = serach_edit.getText().toString().trim();
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent
                        .ACTION_DOWN) {
                    if (!TextUtils.isEmpty(text)) {
                        //serach_edit失去焦点
                        //serach_edit.setFocusable(false);
                        load();
                        nowPage = 1;
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService
                                (Context.INPUT_METHOD_SERVICE);
                        // 隐藏软键盘
                        imm.hideSoftInputFromWindow(mContext.getWindow().getDecorView()
                                .getWindowToken(), 0);
                    } else {
                        ToastUtil.showMessageDefault(mContext, "请输入搜索内容");
                    }
                }
                return false;
            }
        });
        //当点击edittext时获取焦点,并弹出软键盘
       /* serach_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serach_edit.setFocusable(true);
                serach_edit.setFocusableInTouchMode(true);
                serach_edit.requestFocus();
                serach_edit.findFocus();

                InputMethodManager inputManager = (InputMethodManager) serach_edit.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(serach_edit, 0);
            }
        });*/
    }

    @Override
    protected void load() {
        super.load();
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "adCommodity");
            obj.put("cityID", "999");
            obj.put("pageSize", "10");
            obj.put("nowPage", String.valueOf(nowPage));
            obj.put("userLat", "0");
            obj.put("userLat", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonHomeBean gsonHomeBean = gson.fromJson(new String(bytes), new
                        TypeToken<GsonHomeBean>() {
                        }.getType());
                String result = gsonHomeBean.getResult();
                String resultNote = gsonHomeBean.getResultNote();
                if (result.equals("0")) {
                    homeData.clear();
                    bannerData.clear();
                    homeData.addAll(gsonHomeBean.getRecommendCommodityList());
                    bannerData.addAll(gsonHomeBean.getScrollCommodityList());
                    totalPage = gsonHomeBean.getTotalPage();
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

    @Override
    protected void loadMore() {
        super.loadMore();
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "adCommodity");
            obj.put("cityID", "999");
            obj.put("pageSize", "10");
            obj.put("nowPage", String.valueOf(nowPage));
            obj.put("userLat", "0");
            obj.put("userLat", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonHomeBean gsonHomeBean = gson.fromJson(new String(bytes), new
                        TypeToken<GsonHomeBean>() {
                        }.getType());
                String result = gsonHomeBean.getResult();
                String resultNote = gsonHomeBean.getResultNote();
                if (result.equals("0")) {
                    homeData.addAll(gsonHomeBean.getRecommendCommodityList());
                    totalPage = gsonHomeBean.getTotalPage();
                    handler.sendEmptyMessage(Constant.LOAD_REQUEST_SUCCESS);
                } else {
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("resultNote", resultNote);
                    msg.what = Constant.LOAD_RESULT_FAIL;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(Constant.LOAD_REQUEST_FAIL);
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                handler.sendEmptyMessage(Constant.LOAD_NITEWORK_FAIL);
            }
        });
    }


    /**
     * 初始化ViewPager图片
     */
    private void initViewPager() {
        mImagViews = new ImageView[bannerData.size()];
        for (int i = 0; i < bannerData.size(); i++) {
            ImageView iv = new ImageView(mContext);
            ImageLoader.getInstance().displayImage(bannerData.get(i).getCommodityIcon(), iv);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            mImagViews[i] = iv;
        }

        pager = (ViewPager) view.findViewById(R.id.banner_Pager);
        myPagerAdapter = new ViewPagerAdapter(mImagViews);
        pager.setAdapter(myPagerAdapter);
        myPagerAdapter.setOnPagerItemClickListener(this);
        pager.setCurrentItem(mImagViews.length * 50);
        handler.sendEmptyMessageDelayed(Constant.BANNER_SCROLL, 3000);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        mDots = new ImageView[bannerData.size()];
        for (int i = 0; i < bannerData.size(); i++) {
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
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (v.getId()) {
            case R.id.ll_mashaqi:
                homeActivity.intentBread();
                ToastUtil.showMessageDefault(mContext, "进入玛沙琪品牌馆");
                break;
            case R.id.ll_gusha:
                homeActivity.intentBread();
                ToastUtil.showMessageDefault(mContext, "进入古沙品牌馆");
                break;
            case R.id.ll_mucun:
                homeActivity.intentBread();
                ToastUtil.showMessageDefault(mContext, "进入木村拓哉品牌馆");
                break;
        }
    }

    @Override
    public void onRefresh() {
        nowPage = 1;
        load();
    }

    @Override
    public void onLoad() {
        if (nowPage < totalPage) {
            nowPage++;
            loadMore();
        } else {
            handler.sendEmptyMessageDelayed(Constant.LOAD_NO_MROE, 500);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent detailIntent = new Intent(mContext, OrderDetailInfoActivity.class);
        detailIntent.putExtra("commodity", homeData.get(position));
        startActivity(detailIntent);
    }

    @Override
    public void onPagerItemClick(int position) {
        Intent detailIntent = new Intent(mContext, OrderDetailInfoActivity.class);
        detailIntent.putExtra("commodity", bannerData.get(position));
        startActivity(detailIntent);
    }
}
