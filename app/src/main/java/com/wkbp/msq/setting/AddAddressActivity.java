package com.wkbp.msq.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.result.bean.AddressBean;
import com.wkbp.msq.result.bean.GsonAddressBack;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class AddAddressActivity extends BaseActivity implements View.OnClickListener {
    private AddressBean addressBean;
    private String from;
    private String userName;
    private EditText et_add_name, et_add_phone, et_add_area;
    private CheckBox item_checkBox;
    private TextView tv_save;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    finish();
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_address);
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        from = getIntent().getStringExtra("from");
        if (from.equals("add")) {
            setTitleBar("添加收货地址");
        } else if (from.equals("edit")) {
            setTitleBar("修改收货地址");
            addressBean = (AddressBean) getIntent().getSerializableExtra("address");
        }
        initView();
    }

    private void initView() {
        et_add_name = (EditText) findViewById(R.id.et_add_name);
        et_add_phone = (EditText) findViewById(R.id.et_add_phone);
        et_add_area = (EditText) findViewById(R.id.et_add_area);
        item_checkBox = (CheckBox) findViewById(R.id.item_checkBox);
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);

        if (addressBean == null) {
            tv_save.setText("添 加");
        } else {
            tv_save.setText("修 改");
            et_add_name.setText(addressBean.getName());
            et_add_phone.setText(addressBean.getPhone());
            et_add_area.setText(addressBean.getAdress());
        }
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(et_add_name.getText().toString().trim()) || TextUtils.isEmpty
                (et_add_phone.getText().toString().trim()) || TextUtils.isEmpty(et_add_area
                .getText().toString().trim())) {
            ToastUtil.showMessageDefault(mContext, "请完善您的信息后再提交");
        } else {
            if (from.equals("add")) {
                add();
            } else if (from.equals("edit")) {
                edit();
            }
        }
    }

    /**
     * 增加地址
     */
    private void add() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "addAdress");
            obj.put("userName", userName);
            obj.put("adressID", "");
            obj.put("name", et_add_name.getText().toString().trim());
            obj.put("phone", et_add_phone.getText().toString().trim());
            obj.put("adress", et_add_area.getText().toString().trim());
            obj.put("postcode", "");
            obj.put("isDefault", item_checkBox.isChecked() ? "1" : "0");
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
     * 修改地址
     */
    private void edit() {
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "editAdress");
            obj.put("userName", userName);
            obj.put("adressID", addressBean.getAdressID());
            obj.put("name", et_add_name.getText().toString().trim());
            obj.put("phone", et_add_phone.getText().toString().trim());
            obj.put("adress", et_add_area.getText().toString().trim());
            obj.put("postcode", "");
            obj.put("isDefault", item_checkBox.isChecked() ? "1" : "0");
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

}
