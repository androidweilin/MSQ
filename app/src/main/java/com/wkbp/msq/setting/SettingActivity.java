package com.wkbp.msq.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.bean.UserInfoBean;
import com.wkbp.msq.util.SharedPreferenceUtils;

/**
 * 基本设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_user_data, ll_change_pawd, ll_address_manage;
    private TextView exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitleBar("基本设置");
        initView();
    }

    private void initView() {
        ll_user_data = (LinearLayout) findViewById(R.id.ll_user_data);
        ll_change_pawd = (LinearLayout) findViewById(R.id.ll_change_pawd);
        ll_address_manage = (LinearLayout) findViewById(R.id.ll_address_manage);
        exit = (TextView) findViewById(R.id.exit);
        ll_user_data.setOnClickListener(this);
        ll_change_pawd.setOnClickListener(this);
        ll_address_manage.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_data:
                Intent intent01 = new Intent(mContext, PersonalDataActivity.class);
                startActivity(intent01);
                break;
            case R.id.ll_change_pawd:
                Intent intent02 = new Intent(mContext, ChangePswdActivity.class);
                startActivity(intent02);
                break;
            case R.id.ll_address_manage:
                Intent intent03 = new Intent(mContext, MyAddressActivity.class);
                startActivity(intent03);
                break;
            case R.id.exit:
                initDialog();
                baseDialogTitle.setText("是否退出当前账户?");
                baseDialogLeft.setText("确定");
                baseDialogRight.setText("取消");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoBean userInfoBean = new UserInfoBean();
                        userInfoBean.setLogin(false);
                        SharedPreferenceUtils.saveCurrentUserInfo(mContext, userInfoBean);
                        finish();
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
}
