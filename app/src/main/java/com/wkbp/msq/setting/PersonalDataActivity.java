package com.wkbp.msq.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.bean.EditUserInfoBean;
import com.wkbp.msq.bean.GsonEditUserCmd;
import com.wkbp.msq.bean.UserInfoBean;
import com.wkbp.msq.customView.CircleImageView;
import com.wkbp.msq.result.bean.GsonLoginBack;
import com.wkbp.msq.util.BitMapUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 个人资料
 */
public class PersonalDataActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_user_hand, ll_nickName;
    private CircleImageView user_head;
    private TextView save, tv_userName, tv_nickName;
    private Dialog dialog;
    private EditText et_nickName;
    private PopupWindow popupWindow;
    private static int TYPE_CAMERA = 1000;  //照相请求相应码
    private static int TYPE_IMAGE = 2000;  //相册请求相应码
    private String mPhotoPath; //拍照获得图片路径  或者相册选择的图片路径
    private static final int ICON_WIDTH_AND_HEIGHT = 200;  //压缩图片宽高
    private String strImg; //压缩后的照片str

    private UserInfoBean userInfo;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "修改成功");
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    Bundle bundle = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal_data);
        setTitleBar("个人资料");
        userInfo = SharedPreferenceUtils.getCurrentUserInfo(mContext);
        initView();
    }

    private void initView() {
        ll_user_hand = (LinearLayout) findViewById(R.id.ll_user_hand);
        ll_nickName = (LinearLayout) findViewById(R.id.ll_nickName);
        ll_user_hand.setOnClickListener(this);
        ll_nickName.setOnClickListener(this);
        user_head = (CircleImageView) findViewById(R.id.user_head);
        save = (TextView) findViewById(R.id.save);
        tv_nickName = (TextView) findViewById(R.id.tv_nickName);
        tv_userName = (TextView) findViewById(R.id.tv_userName);
        save.setOnClickListener(this);

        tv_userName.setText(userInfo.getUserName());
        tv_nickName.setText(userInfo.getNickName());
        ImageLoader.getInstance().displayImage(userInfo.getUserIcon(), user_head);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_hand:
                getPopupWindow();
                break;
            case R.id.ll_nickName:
                getNickDialog();
                break;
            case R.id.save:
                saveUserInfo();
                break;
            case R.id.btn_camera:
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //指定图片保存的位置
                File file = BitMapUtils.createImageFile();
                //得到图片路径
                mPhotoPath = file.getAbsolutePath();
                //需要将拍摄的照片存储在SDcard的时候,必须加上下边这句代码，但是加上以后onActivityResult参数中 intent 为null
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, TYPE_CAMERA);
                popupWindow.dismiss();
                break;
            case R.id.btn_photo:
                BitMapUtils.openPhoto(this, TYPE_IMAGE);
                popupWindow.dismiss();
                break;
            case R.id.btn_cancel:
                popupWindow.dismiss();
                break;
            case R.id.btnLeft:
                if (!TextUtils.isEmpty(et_nickName.getText().toString().trim())) {
                    tv_nickName.setText(et_nickName.getText().toString().trim());
                }
                dialog.dismiss();
                break;
            case R.id.btnRight:
                dialog.dismiss();
                break;
        }
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {
        startProgressDialog();
        try {
            if (mPhotoPath != null) {
                strImg = BitMapUtils.bitMapToString(BitMapUtils.revitionImageSize(mPhotoPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditUserInfoBean editUser = new EditUserInfoBean(userInfo.getUserName(), strImg, tv_nickName
                .getText().toString());
        GsonEditUserCmd gsonEditCmd = new GsonEditUserCmd("editUserInfo", editUser);
        final Gson gson = new Gson();
        String param = gson.toJson(gsonEditCmd);
        JSONObject obj = null;
        try {
            obj = new JSONObject(param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonLoginBack back = gson.fromJson(new String(bytes), new
                        TypeToken<GsonLoginBack>() {
                        }.getType());
                String result = back.getResult();
                String resultNote = back.getResultNote();
                if (result.equals("0")) {
                    userInfo = back.getUserinfo();
                    userInfo.setLogin(true);
                    SharedPreferenceUtils.saveCurrentUserInfo(mContext, userInfo);
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("resultNote", resultNote);
                    msg.what = Constant.REFRESH_RESULT_FAIL;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(Constant.REFRESH_REQUEST_FAIL);
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                handler.sendEmptyMessage(Constant.REFRESH_NITEWORK_FAIL);
            }
        });
    }

    /**
     * 修改用户昵称dialog
     */
    public void getNickDialog() {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.CustomDialog);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.edit_dialog, null);

        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.show();
        et_nickName = (EditText) view.findViewById(R.id.nick_name);
        TextView left = (TextView) view.findViewById(R.id.btnLeft);
        TextView right = (TextView) view.findViewById(R.id.btnRight);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    /**
     * 初始化PopupWindow
     */
    public void getPopupWindow() {
        popupWindow = new PopupWindow(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.custom_popupwindow, null);
        popupWindow.setContentView(view);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        backgroundAlpha(0.5f);

        popupWindow.setAnimationStyle(R.style.PopupWindow);
        popupWindow.showAtLocation(ll_user_hand, Gravity.BOTTOM, 0, 0);
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopOnDismissListner());

        Button btn_camera = (Button) view.findViewById(R.id.btn_camera);
        Button btn_photo = (Button) view.findViewById(R.id.btn_photo);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    /**
     * popupWindow弹出时，其他地方变暗
     *
     * @param alpha
     */
    private void backgroundAlpha(float alpha) {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = alpha;
        this.getWindow().setAttributes(params);
    }


    /**
     * popupWindow关闭事件
     */
    private class PopOnDismissListner implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TYPE_CAMERA) {
            if (resultCode == RESULT_OK) {
                BitMapUtils.galleryAddPic(this, mPhotoPath);
                Bitmap bitmap = BitMapUtils.loadBitmap(mPhotoPath, ICON_WIDTH_AND_HEIGHT,
                        ICON_WIDTH_AND_HEIGHT);
                user_head.setImageBitmap(bitmap);
            } else {
                //取消照相后，删除已经创建的临时文件
                BitMapUtils.deleteImage();
            }
        } else if (requestCode == TYPE_IMAGE) {
            if (resultCode == RESULT_OK) {
                mPhotoPath = BitMapUtils.getPhotoPathByLocalUri(this, data);
                Bitmap bitmap = BitMapUtils.loadBitmap(mPhotoPath, ICON_WIDTH_AND_HEIGHT,
                        ICON_WIDTH_AND_HEIGHT);
                user_head.setImageBitmap(bitmap);
                //bitmap.recycle();
            } else {

            }
        }
    }

    /**
     * 点击屏幕软键盘退出
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
