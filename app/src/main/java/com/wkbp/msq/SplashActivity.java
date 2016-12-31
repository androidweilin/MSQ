package com.wkbp.msq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.util.Constant;

public class SplashActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout ll_timer;
    private TextView tv_timer;
    private Animation anim;
    private int tvCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        ll_timer = (LinearLayout) findViewById(R.id.ll_timer);
        ll_timer.setOnClickListener(this);
        anim = AnimationUtils.loadAnimation(mContext, R.anim.splash_anim);
        handler.sendEmptyMessageDelayed(Constant.SPLASH_TIMER, 1000);
    }

    private void getCount() {
        tvCount--;
        if (tvCount == 0) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            handler.removeMessages(Constant.SPLASH_TIMER);
            finish();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.SPLASH_TIMER) {
                getCount();
                tv_timer.setText(String.valueOf(tvCount));
                handler.sendEmptyMessageDelayed(Constant.SPLASH_TIMER,1000);
                anim.reset();
                tv_timer.startAnimation(anim);
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        handler.removeMessages(Constant.SPLASH_TIMER);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
