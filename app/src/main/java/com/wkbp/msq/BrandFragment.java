package com.wkbp.msq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.adapter.GridAdapter;
import com.wkbp.msq.base.BaseFragment;
import com.wkbp.msq.popordialog.MyPopup;
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
public class BrandFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayoutUpDown.OnRefreshListener, SwipeRefreshLayoutUpDown.OnLoadListener,
        AdapterView.OnItemClickListener {
    private View view;
    private LinearLayout ll_brandPrice, ll_brandBrand;

    private ImageView icon_price, icon_brand;

    private static final int CLICK_PRICE = 158;
    private static final int CLICK_BRAND = 159;
    private String[] priceDate = {"由低到高", "由高到低"};
    private String[] brandDate = {"玛沙琪", "古沙", "木村拓哉"};

    private int nowPage = 1;
    private int totalPage = 0;
    private List<CommodityBean> brandData = new ArrayList<>();
    private SwipeRefreshLayoutUpDown brand_swipe;
    private GridView gridView;
    private GridAdapter brandAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    brand_swipe.setRefreshing(false);
                    brandAdapter.notifyDataSetChanged();
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    brand_swipe.setRefreshing(false);
                    Bundle bundle = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    brand_swipe.setRefreshing(false);
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    brand_swipe.setRefreshing(false);
                    break;
                case Constant.LOAD_REQUEST_SUCCESS:
                    stopProgressDialog();
                    brand_swipe.setLoading(false);
                    brandAdapter.notifyDataSetChanged();
                    break;
                case Constant.LOAD_RESULT_FAIL:
                    stopProgressDialog();
                    brand_swipe.setLoading(false);
                    Bundle bundle1 = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle1.get("resultNote"));
                    break;
                case Constant.LOAD_REQUEST_FAIL:
                    stopProgressDialog();
                    brand_swipe.setLoading(false);
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.LOAD_NITEWORK_FAIL:
                    stopProgressDialog();
                    brand_swipe.setLoading(false);
                    break;
                case Constant.LOAD_NO_MROE:
                    stopProgressDialog();
                    brand_swipe.setLoading(false);
                    ToastUtil.showMessageDefault(mContext, "没有更多数据了");
                    break;
                case CLICK_PRICE:
                    MyPopup.popupWindow.dismiss();
                    icon_price.setImageResource(R.drawable.icon_down);
                    int priceItem = msg.arg1;
                    ToastUtil.showMessageDefault(mContext, priceDate[priceItem]);
                    load();
                    nowPage = 1;
                    break;
                case CLICK_BRAND:
                    MyPopup.popupWindow.dismiss();
                    icon_brand.setImageResource(R.drawable.icon_down);
                    int brandItem = msg.arg1;
                    ToastUtil.showMessageDefault(mContext, brandDate[brandItem]);
                    load();
                    nowPage = 1;
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_brand, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        load();
    }

    private void initView() {
        ll_brandPrice = (LinearLayout) view.findViewById(R.id.ll_brandPrice);
        ll_brandBrand = (LinearLayout) view.findViewById(R.id.ll_brandBrand);
        ll_brandPrice.setOnClickListener(this);
        ll_brandBrand.setOnClickListener(this);
        icon_price = (ImageView) view.findViewById(R.id.icon_price);
        icon_brand = (ImageView) view.findViewById(R.id.icon_brand);
        brand_swipe = (SwipeRefreshLayoutUpDown) view.findViewById(R.id.brand_swipe);
        gridView = (GridView) view.findViewById(R.id.gridView);
        brandAdapter = new GridAdapter(mContext, brandData);
        gridView.setAdapter(brandAdapter);
        gridView.setOnItemClickListener(this);

        brand_swipe = (SwipeRefreshLayoutUpDown) view.findViewById(R.id.brand_swipe);
        brand_swipe.setOnRefreshListener(this);
        brand_swipe.setOnLoadListener(this);
        brand_swipe.setColor( R.color.swipe1, R.color.swipe2, R.color.swipe3,R.color.swipe4);
        brand_swipe.setMode(SwipeRefreshLayoutUpDown.Mode.BOTH);
        brand_swipe.setLoadNoFull(false);
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
                    brandData.clear();
                    brandData.addAll(gsonHomeBean.getRecommendCommodityList());
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
                    brandData.addAll(gsonHomeBean.getRecommendCommodityList());
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_brandPrice:
                    icon_price.setImageResource(R.drawable.icon_up);
                    MyPopup.getPopupWindow(mContext, priceDate, ll_brandPrice, handler,
                            CLICK_PRICE,icon_price,icon_brand);
                break;
            case R.id.ll_brandBrand:
                    icon_brand.setImageResource(R.drawable.icon_up);
                    MyPopup.getPopupWindow(mContext, brandDate, ll_brandBrand, handler,
                            CLICK_BRAND,icon_price,icon_brand);
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
        Intent detailIntent = new Intent(mContext,OrderDetailInfoActivity.class);
        detailIntent.putExtra("commodity",brandData.get(position));
        startActivity(detailIntent);
    }

}
