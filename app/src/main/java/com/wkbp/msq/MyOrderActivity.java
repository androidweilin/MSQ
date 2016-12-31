package com.wkbp.msq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.adapter.MyOrderAdapter;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.result.bean.GsonMyOrderBack;
import com.wkbp.msq.result.bean.MyOrderBean;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends BaseActivity implements MyOrderAdapter.OnOrderClickListener {
    private ListView orderList;
    private String userName;
    private List<MyOrderBean> myOrderList = new ArrayList<>();
    private MyOrderAdapter myOrderAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    myOrderAdapter.notifyDataSetChanged();
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    Bundle bundle = msg.getData();
                    myOrderAdapter.notifyDataSetChanged();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    break;
                case Constant.CONFIRM_GET_ORDER_SUCCESS:
                    stopProgressDialog();
                    int position = msg.arg1;
                    ToastUtil.showMessageDefault(mContext, "请对本次交易进行评价");
                    //跳转到评价界面
                    goAssessGoods(myOrderList.get(position));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_order);
        setTitleBar("我的订单");
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOrder();
    }

    private void initView() {
        orderList = (ListView) findViewById(R.id.orderList);
        myOrderAdapter = new MyOrderAdapter(mContext, myOrderList);
        orderList.setAdapter(myOrderAdapter);
        myOrderAdapter.setOnOrderClickListener(this);
    }

    /**
     * 查询订单
     */
    private void requestOrder() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "getMyOrder");
            obj.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    Gson gson = new Gson();
                    GsonMyOrderBack orderBack = gson.fromJson(new String(bytes), new
                            TypeToken<GsonMyOrderBack>() {
                            }.getType());
                    String result = orderBack.getResult();
                    String resultNote = orderBack.getResultNote();
                    if (result.equals("0")) {
                        myOrderList.clear();
                        myOrderList.addAll(orderBack.getOrderLists());
                        handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                    } else {
                        myOrderList.clear();
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
     * 删除订单
     */
    private void deleteOrder(final int position) {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "deleteOrder");
            obj.put("userName", userName);
            obj.put("orderID", myOrderList.get(position).getOrderID());
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
                        myOrderList.remove(position);
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

    /**
     * 确认收货
     */
    private void confirmGetOrder(final int position) {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "checkGetGoods");
            obj.put("userName", userName);
            obj.put("userOrderID", myOrderList.get(position).getOrderID());
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
                        Message msg = handler.obtainMessage();
                        msg.arg1 = position;
                        msg.what = Constant.CONFIRM_GET_ORDER_SUCCESS;
                        handler.sendMessage(msg);
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
    public void onOrderClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.btn_delete:
                initDialog();
                baseDialogTitle.setText("删除订单后不可恢复,是否继续删除?");
                baseDialogLeft.setText("删除");
                baseDialogRight.setText("取消");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseDialog.dismiss();
                        deleteOrder(position);
                    }
                });
                baseDialogRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_state:
                String orderState = myOrderList.get(position).getOrderState();
                switch (orderState) {
                    case "0":

                        break;
                    case "1":

                        break;
                    case "2":// 待收货
                        initDialog();
                        baseDialogTitle.setText("为避免您的损失，请确定您已收到宝贝");
                        baseDialogLeft.setText("我已收到");
                        baseDialogRight.setText("还未收到");
                        baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                baseDialog.dismiss();
                                confirmGetOrder(position);
                            }
                        });
                        baseDialogRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                baseDialog.dismiss();
                            }
                        });
                        break;
                    case "3":// 已完成
                        break;
                    case "4":// 待发货
                        initDialog();
                        baseDialogTitle.setText("卖家还没有发货哦,请耐心等待卖家发货!");
                        baseDialogLeft.setText("确定");
                        baseDialogRight.setText("取消");
                        baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                    case "5":// 未付款 可以取消,待付款
                        initDialog();
                        baseDialogTitle.setText("下载二维码进行支付,如已支付,请耐心等待卖家处理!");
                        baseDialogLeft.setText("选择支付");
                        baseDialogRight.setText("我已支付");
                        baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                baseDialog.dismiss();
                                //打开下载二维码界面
                                Intent intent = new Intent(mContext, PayActivity.class);
                                startActivity(intent);
                            }
                        });
                        baseDialogRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                baseDialog.dismiss();
                            }
                        });
                        break;
                    case "6":// 待评价
                        //跳到评价界面
                        goAssessGoods(myOrderList.get(position));
                        break;
                }
                break;
        }
    }

    private void goAssessGoods(MyOrderBean myOrderBean) {
        Intent intent = new Intent(mContext, MyOrderAssessActivity.class);
        intent.putExtra("myOrderBean", myOrderBean);
        startActivity(intent);
    }
}
