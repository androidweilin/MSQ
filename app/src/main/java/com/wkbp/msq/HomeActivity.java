package com.wkbp.msq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.setting.LoginActivity;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_home, ll_brand, ll_shopcar, ll_myself;
    private ImageView iv_home, iv_brand, iv_shopcar, iv_myself;

    private int index;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        initView();
        initFragments();
    }

    private void initFragments() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");
        ft.commit();
    }

    private void initView() {
        iv_home = (ImageView) findViewById(R.id.iv_home);
        iv_brand = (ImageView) findViewById(R.id.iv_brand);
        iv_shopcar = (ImageView) findViewById(R.id.iv_shopcar);
        iv_myself = (ImageView) findViewById(R.id.iv_myself);

        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        ll_brand = (LinearLayout) findViewById(R.id.ll_brand);
        ll_shopcar = (LinearLayout) findViewById(R.id.ll_shopcar);
        ll_myself = (LinearLayout) findViewById(R.id.ll_myself);

        ll_home.setOnClickListener(this);
        ll_brand.setOnClickListener(this);
        ll_shopcar.setOnClickListener(this);
        ll_myself.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit);
        switch (v.getId()) {
            case R.id.ll_home:
                index = 0;
                ft.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");
                break;
            case R.id.ll_brand:
                index = 1;
                ft.replace(R.id.fragment_container, new BrandFragment(), "BrandFragment");
                break;
            case R.id.ll_shopcar:
                boolean isLogin = SharedPreferenceUtils.getCurrentUserInfo(mContext).isLogin();
                if (isLogin) {
                    index = 2;
                    ft.replace(R.id.fragment_container, new ShopcarFragment(), "ShopcarFragment");
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ll_myself:
                index = 3;
                ft.replace(R.id.fragment_container, new MyselfFragment(), "MyselfFragment");
                break;
        }
        ft.commit();
        updateBottom(index);
    }

    private void updateBottom(int position) {
        switch (position) {
            case 0:
                iv_home.setImageResource(R.drawable.home_selecte);
                iv_brand.setImageResource(R.drawable.brand_unselecte);
                iv_shopcar.setImageResource(R.drawable.shopcar_unselecte);
                iv_myself.setImageResource(R.drawable.myself_unselecte);
                break;
            case 1:
                iv_home.setImageResource(R.drawable.home_unselecte);
                iv_brand.setImageResource(R.drawable.brand_selecte);
                iv_shopcar.setImageResource(R.drawable.shopcar_unselecte);
                iv_myself.setImageResource(R.drawable.myself_unselecte);
                break;
            case 2:
                iv_home.setImageResource(R.drawable.home_unselecte);
                iv_brand.setImageResource(R.drawable.brand_unselecte);
                iv_shopcar.setImageResource(R.drawable.shopcar_selecte);
                iv_myself.setImageResource(R.drawable.myself_unselecte);
                break;
            case 3:
                iv_home.setImageResource(R.drawable.home_unselecte);
                iv_brand.setImageResource(R.drawable.brand_unselecte);
                iv_shopcar.setImageResource(R.drawable.shopcar_unselecte);
                iv_myself.setImageResource(R.drawable.myself_selecte);
                break;
        }
    }

    /**
     * 跳到BrandFragment
     */
    public void intentBread() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit);
        ft.replace(R.id.fragment_container, new BrandFragment(), "BrandFragment");
        ft.commit();
        updateBottom(1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime >= 2000) {
                ToastUtil.showMessageDefault(this, "再点一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
