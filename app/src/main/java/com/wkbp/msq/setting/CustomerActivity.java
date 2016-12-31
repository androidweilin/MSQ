package com.wkbp.msq.setting;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.util.ToastUtil;

public class CustomerActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_phone, tv_qq, tv_mobile, tv_wechat;
    private ImageView call_phone, copy_qq, call_mobile, copy_wechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customer);
        setTitleBar("联系客服");
        initView();
    }

    private void initView() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_qq = (TextView) findViewById(R.id.tv_qq);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_wechat = (TextView) findViewById(R.id.tv_wechat);
        call_phone = (ImageView) findViewById(R.id.call_phone);
        copy_qq = (ImageView) findViewById(R.id.copy_qq);
        call_mobile = (ImageView) findViewById(R.id.call_mobile);
        copy_wechat = (ImageView) findViewById(R.id.copy_wechat);
        call_phone.setOnClickListener(this);
        copy_qq.setOnClickListener(this);
        call_mobile.setOnClickListener(this);
        copy_wechat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_phone:
                initDialog();
                baseDialogTitle.setText(tv_phone.getText().toString().trim());
                baseDialogLeft.setText("拨打");
                baseDialogRight.setText("取消");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call(tv_phone.getText().toString().trim());
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
            case R.id.copy_qq:
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService
                        (Context.CLIPBOARD_SERVICE);
                cmb.setText(tv_qq.getText().toString().trim());
                ToastUtil.showMessageDefault(mContext, "QQ号码复制成功！");
                break;
            case R.id.call_mobile:
                initDialog();
                baseDialogTitle.setText(tv_mobile.getText().toString().trim());
                baseDialogLeft.setText("拨打");
                baseDialogRight.setText("取消");
                baseDialogLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call(tv_mobile.getText().toString().trim());
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
            case R.id.copy_wechat:
                ClipboardManager cmb1 = (ClipboardManager) mContext.getSystemService
                        (Context.CLIPBOARD_SERVICE);
                cmb1.setText(tv_wechat.getText().toString().trim());
                ToastUtil.showMessageDefault(mContext, "微信号码复制成功！");
                break;
        }
    }

    /**
     * 打电话
     */
    private void call(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
}
