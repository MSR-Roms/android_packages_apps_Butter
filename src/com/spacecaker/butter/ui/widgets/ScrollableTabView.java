/*
 * Copyright (C) 2011 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spacecaker.butter.ui.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.spacecaker.butter.adapters.TabAdapter;

/**
 * I'm using a custom tab view in place of ActionBarTabs entirely for the theme
 * engine.
 */
public class ScrollableTabView extends HorizontalScrollView implements
        ViewPager.OnPageChangeListener {

    private final Context mContext;

    private ViewPager mPager;

    private TabAdapter mAdapter;

    private final LinearLayout mContainer;

    private final ArrayList<View> mTabs = new ArrayList<View>();

    private Drawable mDividerDrawable;

    private final int mDividerColor = 0xFF636363;

    private int mDividerMarginTop = 12;

    private int mDividerMarginBottom = 12;

    private int mDividerWidth = 1;

    public ScrollableTabView(Context context) {
        this(context, null);
    }

    public ScrollableTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        this.mContext = context;

        mDividerMarginTop = (int)(getResources().getDisplayMetrics().density * mDividerMarginTop);
        mDividerMarginBottom = (int)(getResources().getDisplayMetrics().density * mDividerMarginBottom);
        mDividerWidth = (int)(getResources().getDisplayMetrics().density * mDividerWidth);

        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalFadingEdgeEnabled(false);

        mContainer = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);

        this.addView(mContainer);

    }

    public void setAdapter(TabAdapter adapter) {
        this.mAdapter = adapter;

        if (mPager != null && mAdapter != null)
            initTabs();
    }

    public void setViewPager(ViewPager pager) {
        this.mPager = pager;
        mPager.setOnPageChangeListener(this);

        if (mPager != null && mAdapter != null)
            initTabs();
    }

    private void initTabs() {

        mContainer.removeAllViews();
        mTabs.clear();

        if (mAdapter == null)
            return;

        for (int i = 0; i < mPager.getAdapter().getCount(); i++) {

            final int index = i;

            View tab = mAdapter.getView(i);
            mContainer.addView(tab);

            tab.setFocusable(true);

            mTabs.add(tab);

            if (i != mPager.getAdapter().getCount() - 1) {
                mContainer.addView(getSeparator());
            }

            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPager.getCurrentItem() == index) {
                        selectTab(index);
                    } else {
                        mPager.setCurrentItem(index, true);
                    }
                }
            });

        }

        selectTab(mPager.getCurrentItem());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        selectTab(position);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed)
            selectTab(mPager.getCurrentItem());
    }

    private View getSeparator() {
        View v = new View(mContext);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDividerWidth,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, mDividerMarginTop, 0, mDividerMarginBottom);
        v.setLayoutParams(params);

        if (mDividerDrawable != null)
            v.setBackgroundDrawable(mDividerDrawable);
        else
            v.setBackgroundColor(mDividerColor);

        return v;
    }

    private void selectTab(int position) {

        for (int i = 0, pos = 0; i < mContainer.getChildCount(); i += 2, pos++) {
            View tab = mContainer.getChildAt(i);
            tab.setSelected(pos == position);
        }

        View selectedTab = mContainer.getChildAt(position * 2);

        final int w = selectedTab.getMeasuredWidth();
        final int l = selectedTab.getLeft();

        final int x = l - this.getWidth() / 2 + w / 2;

        smoothScrollTo(x, this.getScrollY());

    }

}
