package com.wkbp.msq.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPagerAdapter extends PagerAdapter {

    private ImageView[] mImagViews;
    private OnPagerItemClickListener onPagerItemClickListener;

    public ViewPagerAdapter(ImageView[] mImagView) {
        super();
        this.mImagViews = mImagView;
    }

    public interface OnPagerItemClickListener {
        void onPagerItemClick(int position);
    }

    public void setOnPagerItemClickListener(OnPagerItemClickListener onPagerItemClickListener) {
        this.onPagerItemClickListener = onPagerItemClickListener;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mImagViews.length == 1) {
            container.removeView(mImagViews[0]);
        } else {
            container.removeView(mImagViews[position % mImagViews.length]);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (mImagViews.length == 1) {
            container.addView(mImagViews[0]);
            return mImagViews[0];
        } else {
            container.addView(mImagViews[position % mImagViews.length]);

            mImagViews[position % mImagViews.length].setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onPagerItemClickListener.onPagerItemClick(position % mImagViews.length);
                }
            });

        }
        return mImagViews[position % mImagViews.length];
    }
}
