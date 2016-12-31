package com.wkbp.msq;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.util.BitMapUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PayActivity extends BaseActivity implements AdapterView.OnLongClickListener {
    private ImageView pay_wechat, pay_alipay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay);
        setTitleBar("选择支付");
        initView();
    }

    private void initView() {
        pay_wechat = (ImageView) findViewById(R.id.pay_wechat);
        pay_alipay = (ImageView) findViewById(R.id.pay_alipay);
        pay_wechat.setOnLongClickListener(this);
        pay_alipay.setOnLongClickListener(this);
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.pay_wechat:
                savePay("wechat");
                break;
            case R.id.pay_alipay:
                savePay("alipay");
                break;
        }
        return false;
    }

    private void savePay(final String pay) {
        initDialog();
        baseDialogTitle.setText("是否保存二维码到手机");
        baseDialogLeft.setText("保存");
        baseDialogRight.setText("取消");
        baseDialogLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseDialog.dismiss();
                readByte(pay);
            }
        });
        baseDialogRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseDialog.dismiss();
            }
        });
    }

    public void readByte(String name) {
        // 判断path有没有
        File filePath = new File(Constant.APP_PATH + "pay/");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        //判断file有没有
        File file = new File(Constant.APP_PATH + "pay/", name + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        try {
            AssetManager am = mContext.getAssets();
            InputStream is = am.open(name);
            FileOutputStream out = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int n;
            // 读写数据
            while ((n = is.read(b)) != -1) {
                out.write(b, 0, n);
            }
            is.close();
            out.close();
            BitMapUtils.galleryAddPic(mContext, Constant.APP_PATH + "pay/" + name + ".jpg");
            ToastUtil.showMessageDefault(mContext, "保存成功,请到支付宝或微信进行支付");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
