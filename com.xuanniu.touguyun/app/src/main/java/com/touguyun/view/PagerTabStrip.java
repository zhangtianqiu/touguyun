package com.touguyun.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/31.
 */
public class PagerTabStrip extends LinearLayout {

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[] {
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;
    private final PageListener pageListener = new PageListener();
    public ViewPager.OnPageChangeListener delegatePageListener;
    private ViewPager pager;
    private int tabCount;
    private int currentPosition = 0;
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;
    private Paint rectPaint;
    private Paint dividerPaint;
    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;
    private boolean shouldExpand = false;
    private boolean textAllCaps = true;
    private boolean isHaveIcon = false;
    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int indicatorPaddingLeftRight=5;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int dividerWidth = 1;
    private int tabTextSize = 12;
    private int tabTextColor = 0xFF666666;
    private int selectedTabTextColor = 0xFF666666;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int tabBackgroundResId;

    private Locale locale;

    public PagerTabStrip(Context context) {
        this(context,null);
    }

    public PagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

//		tabsContainer = new LinearLayout(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
//		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
        this.setBackgroundColor(Color.WHITE);
    }

    public void setViewPager(ViewPager pager,boolean isHaveIcon) {
        this.pager = pager;
        this.isHaveIcon=isHaveIcon;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.removeAllViews();

        tabCount = pager.getAdapter().getCount();
        isHaveIcons=new HashMap<Integer, Boolean>();
        int tabWidth=getResources().getDisplayMetrics().widthPixels/tabCount;
        defaultTabLayoutParams = new LinearLayout.LayoutParams(tabWidth, LayoutParams.MATCH_PARENT);
        indicatorPaddingLeftRight=tabWidth/8;
        for (int i = 0; i < tabCount; i++) {
            isHaveIcons.put(i, false);
            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            }else if (isHaveIcon) {
                addIconTabView(i, pager.getAdapter().getPageTitle(i).toString());
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentPosition = pager.getCurrentItem();
            }
        });

    }

    private void addIconTabView(final int position, String title){
        IconTabView tab=new IconTabView(getContext());
        tab.setText(title);
        addTab(position, tab);
    }

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition!=position) {
                    pager.setCurrentItem(position);
                }
            }
        });
        this.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {
            View v = this.getChildAt(i);
            if(tabBackgroundResId!=0){
                v.setBackgroundResource(tabBackgroundResId);
            }
            if (isHaveIcon&&v instanceof IconTabView) {
                IconTabView tab=(IconTabView)v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                if (i == selectedPosition) {
                    tab.setTextColor(selectedTabTextColor);
                }else {
                    tab.setTextColor(tabTextColor);
                }
                if (textAllCaps) {
                    tab.setText(tab.getText().toString().toUpperCase(locale));
                }
                boolean isHaveNewMsgIcon=false;
                if (null!=isHaveIcons&&isHaveIcons.size()>0) {
                    isHaveNewMsgIcon=isHaveIcons.get(i);
                }
                tab.setIcon(isHaveNewMsgIcon, R.drawable.point_icon);
            }else if (v instanceof TextView) {
                TextView tab=(TextView)v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                if (i == selectedPosition) {
                    tab.setTextColor(selectedTabTextColor);
                }else {
                    tab.setTextColor(tabTextColor);
                }
                if (textAllCaps) {
                    tab.setText(tab.getText().toString().toUpperCase(locale));
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw underline
        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, this.getWidth(), height, rectPaint);

        // draw indicator line
        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = this.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft()+indicatorPaddingLeftRight;
        float lineRight = currentTab.getRight()-indicatorPaddingLeftRight;

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = this.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft()+indicatorPaddingLeftRight;
            final float nextTabRight = nextTab.getRight()-indicatorPaddingLeftRight;

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

        // draw divider

        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = this.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;
            invalidate();
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            updateTabStyles();
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    public void setIndicatorColor(int indicatorColor) {
        this.selectedTabTextColor = indicatorColor;
        this.indicatorColor = indicatorColor;
        updateTabStyles();
        invalidate();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setIndicatorColorResource(int resId) {
//		this.selectedTabTextColor = getResources().getColor(resId);
//		this.indicatorColor = getResources().getColor(resId);
//		updateTabStyles();
//		invalidate();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getIndicatorColor() {
//		return this.indicatorColor;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getIndicatorHeight() {
//		return indicatorHeight;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getIndicatorPaddingLeftRight(){
//		return indicatorPaddingLeftRight;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setUnderlineColor(int underlineColor) {
//		this.underlineColor = underlineColor;
//		invalidate();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setUnderlineColorResource(int resId) {
//		this.underlineColor = getResources().getColor(resId);
//		invalidate();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getUnderlineColor() {
//		return underlineColor;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setDividerColorResource(int resId) {
//		this.dividerColor = getResources().getColor(resId);
//		invalidate();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getDividerColor() {
//		return dividerColor;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getUnderlineHeight() {
//		return underlineHeight;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

//	public void setDividerPadding(int dividerPaddingPx) {
//		this.dividerPadding = dividerPaddingPx;
//		invalidate();
//	}
//
//	public int getDividerPadding() {
//		return dividerPadding;
//	}

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setScrollOffset(int scrollOffsetPx) {
//		this.scrollOffset = scrollOffsetPx;
//		invalidate();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getScrollOffset() {
//		return scrollOffset;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        notifyDataSetChanged();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public boolean getShouldExpand() {
//		return shouldExpand;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public boolean isTextAllCaps() {
//		return textAllCaps;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setAllCaps(boolean textAllCaps) {
//		this.textAllCaps = textAllCaps;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getTextSize() {
//		return tabTextSize;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setTextColorResource(int resId) {
//		this.tabTextColor = getResources().getColor(resId);
//		updateTabStyles();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getTextColor() {
//		return tabTextColor;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public void setTypeface(Typeface typeface, int style) {
//		this.tabTypeface = typeface;
//		this.tabTypefaceStyle = style;
//		updateTabStyles();
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        updateTabStyles();
    }

// --Commented out by Inspection START (15/7/9 上午11:45):
//	public int getTabBackground() {
//		return tabBackgroundResId;
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:45)

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private Map<Integer, Boolean> isHaveIcons;

    public void setPostionIcon(int postion,boolean isHaveIcon){
        if (null!=isHaveIcons&&postion<tabCount) {
            isHaveIcons.remove(postion);
            isHaveIcons.put(postion, isHaveIcon);
            updateTabStyles();
        }
    }

    public int getNowSelectedPostion(){
        return selectedPosition;
    }

}
