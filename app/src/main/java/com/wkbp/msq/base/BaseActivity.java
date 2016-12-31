package com.wkbp.msq.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.util.NetWorkUtil;
import com.wkbp.msq.util.ToastUtil;

/**
 * Created by shangshuaibo on 2016/11/17 10:04
 */
public class BaseActivity extends FragmentActivity {

    protected Activity mContext;
    public TextView baseDialogTitle, baseDialogLeft, baseDialogRight;
    public Dialog baseDialog,progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        CrashApplication.allActivity.add(this);
        mContext = this;
    }

    /**
     * 初始化自定义标题栏
     * @param titleName
     */
    protected void setTitleBar(String titleName){
        findViewById(R.id.custom_back_image).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.custom_title_text)).setText(titleName);
    }

    
    /**
     * 有加载事件
     */
    protected void load() {
    }

    /**
     * 加载更多
     */
    protected void loadMore() {
        if (!NetWorkUtil.isNetworkConnected(CrashApplication.context)) {
            ToastUtil.showMessageDefault(CrashApplication.context, "网络不给力");
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CrashApplication.allActivity.remove(this);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    /**
     * 自定义dialog
     */
    public void initDialog() {
        if (baseDialog == null) {
            baseDialog = new Dialog(mContext, R.style.CustomDialog);
        }
        baseDialog.setCanceledOnTouchOutside(true);
        baseDialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_dialog, null);

        baseDialog.setContentView(view);
        WindowManager.LayoutParams params = baseDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        baseDialog.getWindow().setAttributes(params);
        baseDialog.show();

        baseDialogTitle = (TextView) view.findViewById(R.id.tvTitle);
        baseDialogLeft = (TextView) view.findViewById(R.id.btnLeft);
        baseDialogRight = (TextView) view.findViewById(R.id.btnRight);
    }

    /**
     * loaddingDialog
     */
    public void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(mContext, R.style.progressDialog);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.progress_dialog, null);

        progressDialog.setContentView(view);
        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        progressDialog.getWindow().setAttributes(params);
        progressDialog.show();
    }

    /**
     * 结束loadding
     */
    public void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
