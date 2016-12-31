package com.wkbp.msq.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.util.NetWorkUtil;
import com.wkbp.msq.util.ToastUtil;

/**
 * Created by shangshuaibo on 2016/11/17 10:15
 */
public class BaseFragment extends Fragment {

    protected Activity mContext;
    private boolean superOnCreateViewCalled;

    public TextView baseDialogTitle, baseDialogLeft, baseDialogRight;
    public Dialog baseDialog, progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!superOnCreateViewCalled)
            throw new IllegalStateException(
                    "每个子类必须调用超类的onCreateView方法,来获取mFloatWindow对象");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        superOnCreateViewCalled = true;
        return super.onCreateView(inflater, container, savedInstanceState);
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
