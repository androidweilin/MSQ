package com.wkbp.msq;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.base.BaseFragment;
import com.wkbp.msq.bean.UserInfoBean;
import com.wkbp.msq.customView.CircleImageView;
import com.wkbp.msq.setting.AboutOurActivity;
import com.wkbp.msq.setting.CollectOrderActivity;
import com.wkbp.msq.setting.CustomerActivity;
import com.wkbp.msq.setting.FeedbackActivity;
import com.wkbp.msq.setting.LoginActivity;
import com.wkbp.msq.setting.PersonalDataActivity;
import com.wkbp.msq.setting.SettingActivity;
import com.wkbp.msq.util.SharedPreferenceUtils;

/**
 * Created by shangshuaibo on 2016/11/17 16:58
 */
public class MyselfFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private CircleImageView userHead;//头像
    private TextView userName;
    private RelativeLayout rl_myCollect;//收藏商品
    private RelativeLayout rl_myShopcar;//购物车
    private RelativeLayout rl_myOrder;//我的订单
    private RelativeLayout rl_mySetting;//基本设置
    private LinearLayout ll_myFeedback;//在线反馈
    private LinearLayout ll_myService;//客服
    private LinearLayout ll_myAbout;//关于我们

    private UserInfoBean userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_myself, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void initView() {
        userHead = (CircleImageView) view.findViewById(R.id.userHead);
        userName = (TextView) view.findViewById(R.id.userName);
        rl_myCollect = (RelativeLayout) view.findViewById(R.id.rl_myCollect);
        rl_myShopcar = (RelativeLayout) view.findViewById(R.id.rl_myShopcar);
        rl_myOrder = (RelativeLayout) view.findViewById(R.id.rl_myOrder);
        rl_mySetting = (RelativeLayout) view.findViewById(R.id.rl_mySetting);
        ll_myFeedback = (LinearLayout) view.findViewById(R.id.ll_myFeedback);
        ll_myService = (LinearLayout) view.findViewById(R.id.ll_myService);
        ll_myAbout = (LinearLayout) view.findViewById(R.id.ll_myAbout);
        userHead.setOnClickListener(this);
        rl_myCollect.setOnClickListener(this);
        rl_myShopcar.setOnClickListener(this);
        rl_myOrder.setOnClickListener(this);
        rl_mySetting.setOnClickListener(this);
        ll_myFeedback.setOnClickListener(this);
        ll_myService.setOnClickListener(this);
        ll_myAbout.setOnClickListener(this);
    }

    @Override
    protected void load() {
        super.load();
        userInfo = SharedPreferenceUtils.getCurrentUserInfo(mContext);
        if (userInfo.isLogin()) {
           ImageLoader.getInstance().displayImage(userInfo.getUserIcon(), userHead);
            userName.setText(userInfo.getNickName());
        } else {
            userHead.setImageResource(R.drawable.default_head);
            userName.setText("非会员");
        }
    }

    @Override
    public void onClick(View v) {
        boolean isLogin = userInfo.isLogin();
        switch (v.getId()) {
            case R.id.userHead:
                if (isLogin) {
                    Intent intent01 = new Intent(mContext, PersonalDataActivity.class);
                    startActivity(intent01);
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_myCollect:
                if (isLogin) {
                    Intent intent02 = new Intent(mContext, CollectOrderActivity.class);
                    startActivity(intent02);
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_myShopcar:
                if (isLogin) {
                    Intent intent03 = new Intent(mContext, ShopcarActivity.class);
                    startActivity(intent03);
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_myOrder:
                if (isLogin) {
                    Intent intent04 = new Intent(mContext, MyOrderActivity.class);
                    startActivity(intent04);
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_mySetting:
                if (isLogin) {
                    Intent intent05 = new Intent(mContext, SettingActivity.class);
                    startActivity(intent05);
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ll_myFeedback:
                Intent intent06 = new Intent(mContext, FeedbackActivity.class);
                startActivity(intent06);
                break;
            case R.id.ll_myService:
                Intent intent07 = new Intent(mContext, CustomerActivity.class);
                startActivity(intent07);
                break;
            case R.id.ll_myAbout:
                Intent intent08 = new Intent(mContext, AboutOurActivity.class);
                startActivity(intent08);
                break;
        }
    }
}
