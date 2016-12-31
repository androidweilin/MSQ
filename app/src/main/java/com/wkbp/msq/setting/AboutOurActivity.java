package com.wkbp.msq.setting;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.util.Util;

public class AboutOurActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about_our);
        setTitleBar("关于我们");
        TextView tv = (TextView) findViewById(R.id.about);
        tv.setText(String.format("玛沙琪 Android %1$s 版本", Util.getVersionCode(mContext)));
    }
}
