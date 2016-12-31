package com.wkbp.msq.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;

public class AgreementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_agreement);
        setTitleBar("用户协议");
    }
}
