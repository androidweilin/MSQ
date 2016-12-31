package com.wkbp.msq.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.HomeActivity;
import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.base.CrashApplication;
import com.wkbp.msq.bean.GsonUserAuthCmd;
import com.wkbp.msq.bean.UserAuthCodeBean;
import com.wkbp.msq.bean.UserInfoBean;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangePswdActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_userName, et_newPswd, et_again_newPswd;
    private TextView tv_change;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    UserInfoBean userInfoBean = new UserInfoBean();
                    userInfoBean.setLogin(false);
                    SharedPreferenceUtils.saveCurrentUserInfo(mContext, userInfoBean);
                    for (Activity activity : CrashApplication.allActivity) {
                        if (activity instanceof HomeActivity){

                        }else {
                            activity.finish();
                        }
                    }
                    Intent intent = new Intent(mContext,LoginActivity.class);
                    startActivity(intent);
                    ToastUtil.showMessageDefault(mContext, "密码修改成功,请重新登录");
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
        setContentView(R.layout.activity_change_pswd);
        setTitleBar("修改密码 ");
        initView();
    }

    private void initView() {
        et_userName = (EditText) findViewById(R.id.et_userName);
        et_newPswd = (EditText) findViewById(R.id.et_newPswd);
        et_again_newPswd = (EditText) findViewById(R.id.et_again_newPswd);
        tv_change = (TextView) findViewById(R.id.tv_change);
        tv_change.setOnClickListener(this);
        et_userName.setText(SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName());
    }

    @Override
    protected void load() {
        super.load();
        startProgressDialog();
        UserAuthCodeBean authCodeBean = new UserAuthCodeBean(et_userName.getText().toString()
                .trim(), et_newPswd.getText().toString().trim(), et_again_newPswd.getText()
                .toString().trim());
        GsonUserAuthCmd userAuthCmd = new GsonUserAuthCmd("changePassword", authCodeBean);
        final Gson gson = new Gson();
        String param = gson.toJson(userAuthCmd);
        JSONObject obj = null;
        try {
            obj = new JSONObject(param);
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
                        handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_change:
                if (TextUtils.isEmpty(et_userName.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "用户名不能为空");
                } else if (TextUtils.isEmpty(et_newPswd.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "新密码不能为空");
                } else if (TextUtils.isEmpty(et_again_newPswd.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "请再次输入新密码");
                } else if (!et_again_newPswd.getText().toString().trim().equals(et_newPswd
                        .getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "两次新密码输入不一致");
                } else {
                    load();
                }
                break;
        }
    }
}
