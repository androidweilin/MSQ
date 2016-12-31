package com.wkbp.msq.setting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.bean.GsonLoginCmd;
import com.wkbp.msq.bean.UserInfoBean;
import com.wkbp.msq.result.bean.GsonLoginBack;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_phoneNumber, et_passWord;
    private TextView tv_register, tv_forgetPswd, tv_login;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "登录成功");
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
        setContentView(R.layout.activity_login);
        setTitleBar("登录");
        initView();
    }

    private void initView() {
        et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        et_passWord = (EditText) findViewById(R.id.et_passWord);
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_forgetPswd = (TextView) findViewById(R.id.tv_forgetPswd);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_register.setOnClickListener(this);
        tv_forgetPswd.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    @Override
    protected void load() {
        super.load();
        startProgressDialog();
        UserInfoBean loginUser = new UserInfoBean(et_phoneNumber.getText()
                .toString(), et_passWord.getText().toString());
        GsonLoginCmd gsonLoginCmd = new GsonLoginCmd("login", loginUser);
        final Gson gson = new Gson();
        String param = gson.toJson(gsonLoginCmd);
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
                GsonLoginBack back = gson.fromJson(new String(bytes), new
                        TypeToken<GsonLoginBack>() {
                        }.getType());
                String result = back.getResult();
                String resultNote = back.getResultNote();
                if (result.equals("0")) {
                    UserInfoBean userInfo = back.getUserinfo();
                    userInfo.setLogin(true);
                    SharedPreferenceUtils.saveCurrentUserInfo(mContext, userInfo);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgetPswd:
                initDialog();
                baseDialogTitle.setText("请拨打客服电话找回密码\n是否拨打客服电话10086?");
                baseDialogLeft.setText("拨打");
                baseDialogRight.setText("取消");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call("10086");
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
            case R.id.tv_login:
                if (TextUtils.isEmpty(et_phoneNumber.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "手机号不能为空");
                } else if (TextUtils.isEmpty(et_passWord.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "密码不能为空");
                } else {
                    load();
                }
                break;
        }
    }

    /**
     * 打电话
     */
    private void call(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
}
