package com.wkbp.msq.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.R;
import com.wkbp.msq.adapter.AddressAdapter;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.customView.ListViewForScrollView;
import com.wkbp.msq.result.bean.AddressBean;
import com.wkbp.msq.result.bean.GsonAddressBack;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAddressActivity extends BaseActivity implements View.OnClickListener,
        AddressAdapter.OnClickAddressItemListener {
    private ListViewForScrollView address_list;
    private ScrollView scrollView;
    private RelativeLayout rl_address_empty;
    private TextView add_newAddress;
    private AddressAdapter myAdapter;
    private String userName;
    private List<AddressBean> addressList = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    if (addressList.size() == 0) {
                        scrollView.setVisibility(View.GONE);
                        rl_address_empty.setVisibility(View.VISIBLE);
                    } else {
                        scrollView.setVisibility(View.VISIBLE);
                        rl_address_empty.setVisibility(View.GONE);
                    }
                    stopProgressDialog();
                    myAdapter.notifyDataSetChanged();
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    Bundle bundle = msg.getData();
                    if (addressList.size() == 0) {
                        scrollView.setVisibility(View.GONE);
                        rl_address_empty.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_address);
        setTitleBar("收货地址");
        initView();
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        rl_address_empty = (RelativeLayout) findViewById(R.id.rl_address_empty);
        add_newAddress = (TextView) findViewById(R.id.add_newAddress);
        add_newAddress.setOnClickListener(this);
        address_list = (ListViewForScrollView) findViewById(R.id.address_list);
        myAdapter = new AddressAdapter(mContext, addressList);
        myAdapter.setOnClickAddressItemListener(this);
        address_list.setAdapter(myAdapter);
        address_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long
                                                   id) {
                initDialog();
                baseDialogTitle.setText("您确定要删除该地址吗?");
                baseDialogLeft.setText("确定");
                baseDialogRight.setText("取消");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseDialog.dismiss();
                        deleteAddress(position);
                    }
                });

                baseDialogRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseDialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    /**
     * 查询地址列表
     */
    @Override
    protected void load() {
        super.load();
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
                String resultNote = back.getResultNote();
                if (result.equals("0")) {
                    addressList.clear();
                    addressList.addAll(back.getAdressList());
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    try {
                        addressList.clear();
                        addressList.addAll(back.getAdressList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
     * 删除地址
     *
     * @param position
     */
    private void deleteAddress(int position) {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "deleteAdress");
            obj.put("userName", userName);
            obj.put("adressID", addressList.get(position).getAdressID());
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
                String resultNote = back.getResultNote();
                if (result.equals("0")) {
                    addressList.clear();
                    addressList.addAll(back.getAdressList());
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    try {
                        addressList.clear();
                        addressList.addAll(back.getAdressList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
     * 修改默认地址
     *
     * @param position
     */
    private void changeDefaultAddress(int position, String isDefalut) {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "editAdress");
            obj.put("userName", userName);
            obj.put("adressID", addressList.get(position).getAdressID());
            obj.put("name", addressList.get(position).getName());
            obj.put("phone", addressList.get(position).getPhone());
            obj.put("adress", addressList.get(position).getAdress());
            obj.put("postcode", addressList.get(position).getPostcode());
            obj.put("isDefault", isDefalut);
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
                String resultNote = back.getResultNote();
                if (result.equals("0")) {
                    addressList.clear();
                    addressList.addAll(back.getAdressList());
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    try {
                        addressList.clear();
                        addressList.addAll(back.getAdressList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_newAddress:
                Intent intent = new Intent(mContext, AddAddressActivity.class);
                intent.putExtra("from", "add");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClickAddressItem(View view, final int position) {
        switch (view.getId()) {
            case R.id.address_checked:
                if (addressList.get(position).getIsDefault() == 1) {

                } else {
                    initDialog();
                    baseDialogTitle.setText("确定设置该地址为默认地址吗?");
                    baseDialogLeft.setText("确定");
                    baseDialogRight.setText("取消");
                    baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            baseDialog.dismiss();
                            changeDefaultAddress(position, "1");
                        }
                    });
                    baseDialogRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            baseDialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.address_editor:
                Intent editIntent = new Intent(mContext, AddAddressActivity.class);
                editIntent.putExtra("address", addressList.get(position));
                editIntent.putExtra("from", "edit");
                startActivity(editIntent);
                break;
        }
    }
}
