package com.wkbp.msq;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.adapter.ShopcarAdapter;
import com.wkbp.msq.base.BaseFragment;
import com.wkbp.msq.result.bean.CommodityBean;
import com.wkbp.msq.result.bean.GsonShopDetailback;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/17 16:58
 */
public class ShopcarFragment extends BaseFragment implements View.OnClickListener, ShopcarAdapter
        .OnShopCarItemClickListner {
    private View view;
    private ListView orderList;
    private CheckBox all_checkBox;
    private TextView total_price, confirm_pay;
    private List<CommodityBean> shopcarList = new ArrayList<>();
    private ShopcarAdapter shopcarAdapter;
    private String userName;
    private double mTotalPrice; //购物车商品总价
    private int mItemSelectedNums; //购物车商品总数
    private List<Boolean> checkState = new ArrayList<>();//保存选中的状态
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    bottomResult();
                    shopcarAdapter.notifyDataSetChanged();
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    Bundle bundle = msg.getData();
                    shopcarAdapter.notifyDataSetChanged();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shopcar, container, false);
        }
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkShopCar();
    }

    private void initView() {
        all_checkBox = (CheckBox) view.findViewById(R.id.all_checkBox);
        total_price = (TextView) view.findViewById(R.id.total_price);
        confirm_pay = (TextView) view.findViewById(R.id.confirm_pay);
        all_checkBox.setOnClickListener(this);
        confirm_pay.setOnClickListener(this);
        orderList = (ListView) view.findViewById(R.id.pay_list);
        shopcarAdapter = new ShopcarAdapter(mContext, shopcarList);
        shopcarAdapter.setOnShopCarItemClickListner(this);
        orderList.setAdapter(shopcarAdapter);
    }

    /**
     * 查询购物车信息
     */
    private void checkShopCar() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "getShopCar");
            obj.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    Gson gson = new Gson();
                    GsonShopDetailback shopDetailback = gson.fromJson(new String(bytes), new
                            TypeToken<GsonShopDetailback>() {
                            }.getType());
                    String result = shopDetailback.getResult();
                    String resultNote = shopDetailback.getResultNote();
                    if (result.equals("0")) {
                        shopcarList.clear();
                        shopcarList.addAll(shopDetailback.getCommodityList());
                        all_checkBox.setChecked(false); //如果是从确认订单界面返回,需要清空全选状态
                        setCheckState();
                        handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                    } else {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("resultNote", resultNote);
                        msg.what = Constant.REFRESH_RESULT_FAIL;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_checkBox:
                if (all_checkBox.isChecked()) {
                    for (CommodityBean item : shopcarList) {
                        item.setCheck(true);
                    }
                } else {
                    for (CommodityBean item : shopcarList) {
                        item.setCheck(false);
                    }
                }
                shopcarAdapter.notifyDataSetChanged();
                setCheckState();
                bottomResult();
                break;
            case R.id.confirm_pay:
                List<CommodityBean> commodityList = new ArrayList<>();
                for (int i = 0; i < shopcarList.size(); i++) {
                    if (shopcarList.get(i).isCheck()) {
                        commodityList.add(shopcarList.get(i));
                    }
                }
                Intent intent = new Intent(mContext, ConfirmBuyActivity.class);
                intent.putExtra("commodityList", (Serializable) commodityList);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onShopCarItemClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.item_checkBox:
                itemSelecte();
                break;
            case R.id.iv_orderSub:
                itemSub(position);
                break;
            case R.id.iv_orderAdd:
                itemAdd(position);
                break;
            case R.id.iv_orderDelete:
                initDialog();
                baseDialogTitle.setText("您确定要狠心删除该商品吗?");
                baseDialogLeft.setText("我意已决");
                baseDialogRight.setText("不,我点错了");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemDelete(position);
                        baseDialog.dismiss();
                    }
                });
                baseDialogRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseDialog.dismiss();
                    }
                });
                break;
        }
    }

    /**
     * 点击item的选择按钮
     */
    private void itemSelecte() {
        int flag = 0;
        for (int i = 0; i < shopcarList.size(); i++) {
            if (shopcarList.get(i).isCheck()) {
                flag++;
            }
        }
        if (shopcarList.size() == flag) {
            all_checkBox.setChecked(true);
        } else {
            all_checkBox.setChecked(false);
        }
        setCheckState();
        bottomResult();
    }

    /**
     * 点击减号
     *
     * @param position
     */
    private void itemSub(int position) {
        View itemView = orderList.getChildAt(position);
        TextView tv_orderTotal = (TextView) itemView.findViewById(R.id.tv_orderTotal);
        int itemCount = Integer.valueOf(tv_orderTotal.getText().toString());
        if (itemCount <= 1) {
            ToastUtil.showMessageDefault(mContext, "哎呦,不能再点了呦");
        } else {
            itemCount -= 1;
            requestEditShopCar(position, String.valueOf(itemCount));
        }
    }

    /**
     * 点击加号
     *
     * @param position
     */
    private void itemAdd(int position) {
        View itemView = orderList.getChildAt(position);
        TextView tv_orderTotal = (TextView) itemView.findViewById(R.id.tv_orderTotal);
        int itemCount = Integer.valueOf(tv_orderTotal.getText().toString());
        itemCount += 1;
        requestEditShopCar(position, String.valueOf(itemCount));
    }

    /**
     * 点击删除
     *
     * @param position
     */
    private void itemDelete(int position) {
        checkState.remove(position);
        requestEditShopCar(position, "");
    }

    /**
     * 修改购物车
     */
    private void requestEditShopCar(int position, String number) {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "editShopCar");
            obj.put("userName", userName);
            obj.put("commodityID", shopcarList.get(position).getCommodityID());
            obj.put("commodityNum", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    Gson gson = new Gson();
                    GsonShopDetailback shopDetailback = gson.fromJson(new String(bytes), new
                            TypeToken<GsonShopDetailback>() {
                            }.getType());
                    String result = shopDetailback.getResult();
                    String resultNote = shopDetailback.getResultNote();
                    if (result.equals("0")) {
                        shopcarList.clear();
                        shopcarList.addAll(shopDetailback.getCommodityList());
                        getCheckState();
                        handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                    } else {
                        shopcarList.clear();
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("resultNote", resultNote);
                        msg.what = Constant.REFRESH_RESULT_FAIL;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
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
     * 根据选中状态计算结果并更新界面
     */
    private void bottomResult() {
        mTotalPrice = 0;
        mItemSelectedNums = 0;
        for (int i = 0; i < shopcarList.size(); i++) {
            if (shopcarList.get(i).isCheck()) {
                mTotalPrice += Double.valueOf(shopcarList.get(i).getCommodityPrice()) * Integer
                        .valueOf(shopcarList.get(i).getCommodityNum());
                mItemSelectedNums += Integer.valueOf(shopcarList.get(i).getCommodityNum());
            }
        }
        BigDecimal bg = new BigDecimal(Double.toString(mTotalPrice));
        mTotalPrice = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        updateBtnState();
    }

    /**
     * 设置购物车界面显示
     */
    private void updateBtnState() {
        total_price.setText(String.valueOf(mTotalPrice));
        confirm_pay.setText(String.format("结算(%1$s)", mItemSelectedNums));
        if (mItemSelectedNums == 0) {
            confirm_pay.setEnabled(false);
            confirm_pay.setBackgroundColor(Color.parseColor("#999999"));
        } else {
            confirm_pay.setEnabled(true);
            confirm_pay.setBackgroundColor(getResources().getColor(R.color.brown));
        }
    }


    /**
     * 保存选中状态
     */
    private void setCheckState() {
        checkState.clear();
        for (int i = 0; i < shopcarList.size(); i++) {
            checkState.add(shopcarList.get(i).isCheck());
        }
    }

    /**
     * 取出选中状态
     */
    private void getCheckState() {
        for (int i = 0; i < shopcarList.size(); i++) {
            shopcarList.get(i).setCheck(checkState.get(i));
        }
    }
}
