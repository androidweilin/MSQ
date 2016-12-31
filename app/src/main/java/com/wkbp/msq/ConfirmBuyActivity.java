package com.wkbp.msq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.adapter.OrderAdapter;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.bean.ConfirmOrderBean;
import com.wkbp.msq.bean.GsonConfirmOrderCmd;
import com.wkbp.msq.customView.ListViewForScrollView;
import com.wkbp.msq.result.bean.AddressBean;
import com.wkbp.msq.result.bean.CommodityBean;
import com.wkbp.msq.result.bean.GsonAddressBack;
import com.wkbp.msq.result.bean.GsonConfirmOrderBack;
import com.wkbp.msq.setting.MyAddressActivity;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ConfirmBuyActivity extends BaseActivity implements View.OnClickListener {
    private ScrollView scrollView;
    private LinearLayout ll_add_address;//添加地址布局
    private LinearLayout ll_address;//显示地址的布局
    private TextView tv_name, tv_phone, tv_address;//收货地址的姓名、电话和地址
    private EditText et_leave;//买家留言
    private TextView total_count, total_price, confirm_order;//商品总数、商品总价和确认订单
    private ListViewForScrollView order_list;//订单商品列表
    private List<CommodityBean> orderList = new ArrayList<>();
    private List<ConfirmOrderBean> mConfirmOrderBeans = new ArrayList<>();
    private double mTotalPrice; //商品总价
    private int mItemSelectedNums; //商品总数
    private String userName;
    private List<AddressBean> addressList = new ArrayList<>();
    private boolean isDefaultAddress;//是否有默认地址
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    for (int i = 0; i < addressList.size(); i++) {
                        if (addressList.get(i).getIsDefault() == 1) {
                            isDefaultAddress = true;
                            tv_name.setText(addressList.get(i).getName());
                            tv_phone.setText(addressList.get(i).getPhone());
                            tv_address.setText(addressList.get(i).getAdress());
                        }
                    }
                    if (isDefaultAddress) {
                        ll_address.setVisibility(View.VISIBLE);
                        ll_add_address.setVisibility(View.GONE);
                    } else {
                        ll_address.setVisibility(View.GONE);
                        ll_add_address.setVisibility(View.VISIBLE);
                    }
                    stopProgressDialog();
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    ll_address.setVisibility(View.GONE);
                    ll_add_address.setVisibility(View.VISIBLE);
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    break;
                case Constant.CONFIRM_ORDER_SUCCESS:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "订单提交成功,请在我的订单中查看订单状态");
                    break;
                case Constant.CONFIRM_ORDER_FAIL:
                    ToastUtil.showMessageDefault(mContext, "订单提交失败");
                    stopProgressDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm_buy);
        setTitleBar("立即购买");
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        orderList = (List<CommodityBean>) getIntent().getSerializableExtra("commodityList");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDefaultAddress = false;
        requestAddress();
    }

    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        ll_add_address = (LinearLayout) findViewById(R.id.ll_add_address);
        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_address = (TextView) findViewById(R.id.tv_address);
        total_count = (TextView) findViewById(R.id.total_count);
        total_price = (TextView) findViewById(R.id.total_price);
        confirm_order = (TextView) findViewById(R.id.confirm_order);
        et_leave = (EditText) findViewById(R.id.et_leave);
        order_list = (ListViewForScrollView) findViewById(R.id.order_list);
        order_list.setAdapter(new OrderAdapter(mContext, orderList));
        scrollView.smoothScrollTo(0, 0);

        ll_address.setOnClickListener(this);
        ll_add_address.setOnClickListener(this);
        confirm_order.setOnClickListener(this);

        for (int i = 0; i < orderList.size(); i++) {
            mTotalPrice += Double.valueOf(orderList.get(i).getCommodityPrice()) * Integer
                    .valueOf(orderList.get(i).getCommodityNum());
            mItemSelectedNums += Integer.valueOf(orderList.get(i).getCommodityNum());
        }
        BigDecimal bg = new BigDecimal(Double.toString(mTotalPrice));
        mTotalPrice = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        total_count.setText(String.format("共%1$s件商品", mItemSelectedNums));
        total_price.setText(String.format("￥%1$s", mTotalPrice));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add_address:
            case R.id.ll_address:
                Intent intent = new Intent(mContext, MyAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.confirm_order:
                if (isDefaultAddress) {
                    for (int i = 0; i < orderList.size(); i++) {
                        CommodityBean commodityBean = orderList.get(i);
                        double sumMoney = Double.valueOf(commodityBean.getCommodityPrice()) *
                                Integer.valueOf(commodityBean.getCommodityNum());
                        BigDecimal bg = new BigDecimal(Double.toString(mTotalPrice));
                        sumMoney = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        ConfirmOrderBean confirmOrderBean = new ConfirmOrderBean(
                                commodityBean.getCommodityID(),
                                commodityBean.getCommodityNum(), String.valueOf(sumMoney),
                                et_leave.getText().toString().trim());
                        mConfirmOrderBeans.add(confirmOrderBean);
                    }
                    requestSendCommodites();
                } else {
                    ToastUtil.showMessageDefault(mContext, "请添加收货地址后再提交");
                }
                break;
        }
    }

    /**
     * 请求地址
     */
    private void requestAddress() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "getAdress");
            obj.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonAddressBack back = gson.fromJson(new String(bytes), new
                        TypeToken<GsonAddressBack>() {
                        }.getType());
                String result = back.getResult();
                if (result.equals("0")) {
                    addressList.clear();
                    addressList.addAll(back.getAdressList());
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    handler.sendEmptyMessage(Constant.REFRESH_RESULT_FAIL);
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
     * 提交订单
     */
    private void requestSendCommodites() {
        startProgressDialog();
        GsonConfirmOrderCmd gsonConfirmOrderCmd = new GsonConfirmOrderCmd(
                "sendCommodities", userName, tv_address.getText().toString(),
                "", tv_phone.getText().toString(), tv_name.getText().toString(),
                mConfirmOrderBeans);
        final Gson gson = new Gson();
        String param = gson.toJson(gsonConfirmOrderCmd);
        JSONObject obj = null;
        try {
            obj = new JSONObject(param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonConfirmOrderBack back = gson.fromJson(new String(bytes), new
                        TypeToken<GsonConfirmOrderBack>() {
                        }.getType());
                String result = back.getResult();
                if (result.equals("0")) {
                    handler.sendEmptyMessage(Constant.CONFIRM_ORDER_SUCCESS);
                } else {
                    handler.sendEmptyMessage(Constant.CONFIRM_ORDER_FAIL);
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
}
