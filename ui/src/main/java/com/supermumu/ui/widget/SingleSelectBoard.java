package com.supermumu.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermumu.R;
import com.supermumu.ui.helper.SelectBoardResHelper;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hsienhsu on 2017/9/20.
 */

public class SingleSelectBoard extends LinearLayout implements View.OnClickListener {
    private final int MIN_COUNT = 2;
    private final int MAX_COUNT = 5;
    
    public interface IButtonClickListener {
        void onClickListener(int position, View view);
    }
    
    private IButtonClickListener buttonClickListener;
    
    private SelectBoardResHelper selectBoardResHelper;
    private int visibleButtonCount;
    
    public SingleSelectBoard(Context context) {
        super(context);
        init(context, null);
    }
    
    public SingleSelectBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public SingleSelectBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        initSelectBoardThemeAttributes(context, attrs);
        
        final int margin1X = context.getResources().getDimensionPixelSize(R.dimen.margin_1x);
        for (int i=0; i<MAX_COUNT; i++) {
            TextView view = createTextView(context, margin1X, attrs);
            view.setOnClickListener(this);
            addView(view);
        }
    }
    
    private void initSelectBoardThemeAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SingleSelectBoard,
                0,
                0
        );
    
        int colorSelected = a.getColor(R.styleable.SingleSelectBoard_colorSelected, 0);
        if (colorSelected == 0) {
            colorSelected = ContextCompat.getColor(context, R.color.selected_theme_color);
        }
    
        int colorUnselected = a.getColor(R.styleable.SingleSelectBoard_colorUnselected, 0);
        if (colorUnselected == 0) {
            colorUnselected = ContextCompat.getColor(context, R.color.unselected_theme_color);
        }
        
        a.recycle();
    
        int roundRadius = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_radius);
        int strokeWidth = context.getResources().getDimensionPixelSize(R.dimen.single_select_board_stroke_width);
    
        selectBoardResHelper = new SelectBoardResHelper(colorSelected, colorUnselected, roundRadius, strokeWidth);
    }
    
    private TextView createTextView(Context context, int padding, AttributeSet attrs) {
        ColorStateList colorStateList = selectBoardResHelper.getTextColorStateList();
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1);
        TextView view = new TextView(context, attrs);
        view.setLayoutParams(lp);
        view.setPadding(0, padding, 0, padding);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(colorStateList);
        return view;
    }
    
    public void setDisplayText(@NonNull ArrayList<CharSequence> list) {
        visibleButtonCount = checkButtonCount(list.size());
        
        for (int i=0; i<MAX_COUNT; i++) {
            View view = getChildAt(i);
            if (i < visibleButtonCount) {
                if (i == 0) {
                    view.setBackground(selectBoardResHelper.getStartDrawable());
                } else if ((i + 1) == visibleButtonCount) {
                    view.setBackground(selectBoardResHelper.getEndDrawable());
                } else {
                    view.setBackground(selectBoardResHelper.getCenterDrawable());
                }
                ((TextView)view).setText(list.get(i));
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    
        View view = getChildAt(0);
        if (view.getVisibility() == View.VISIBLE) {
            view.callOnClick();
        }
    }
    
    @IntRange(from = MIN_COUNT, to = MAX_COUNT)
    private int checkButtonCount(int count) {
        if (MIN_COUNT > count || count > MAX_COUNT) {
            throw new RuntimeException(String.format(Locale.getDefault(), "The attr of buttonCount must set %d to %d", MIN_COUNT, MAX_COUNT));
        }
        return count;
    }
    
    @Override
    public void onClick(View view) {
        int rightIndex = visibleButtonCount - 1;
        for (int index = rightIndex; index >= 0; index--) {
            View childView = getChildAt(index);
            if (childView.getVisibility() != View.VISIBLE) {
                continue;
            }
            
            boolean isSelectedView = (childView == view);
            if (isSelectedView) {
                if (childView.isSelected()) {
                    continue;
                }
            } else {
                if (!childView.isSelected()) {
                    continue;
                }
            }
            
            childView.setSelected(isSelectedView);
            if (index == 0) {
                childView.setBackground(selectBoardResHelper.getStartDrawable());
            } else if (index == rightIndex) {
                rightIndex = index;
                childView.setBackground(selectBoardResHelper.getEndDrawable());
            } else {
                childView.setBackground(selectBoardResHelper.getCenterDrawable());
            }
        }
        
        if (null != buttonClickListener) {
            int position = 0;
            for (int i=0; i<getChildCount(); i++) {
                if (view == getChildAt(i)) {
                    position = i;
                    break;
                }
            }
            
            buttonClickListener.onClickListener(position, view);
        }
    }
    
    public void setClickListener(IButtonClickListener listener) {
        buttonClickListener = listener;
    }
}
