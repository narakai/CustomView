package com.example.laileon.customview.view.CornerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.laileon.customview.R;
import com.example.laileon.customview.utils.utils.DensityUtils;
import com.example.laileon.customview.view.BubbleDrawable;


/**
 * Created by zengxianzi on 2016/8/24.
 */
public class CornerLayout2 extends RelativeLayout {

    float radius = 0.0f;
    float top_left_radius = 0.0f;
    float top_right_radius = 0.0f;
    float bottom_right_radius = 0.0f;
    float bottom_left_radius = 0.0f;
    float border = 0.0f;
    int borderColor = 0;
    int bg = 0;

    public CornerLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public CornerLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CornerLayout2(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CornerView, defStyleAttr, defStyleAttr);
        radius = a.getDimension(R.styleable.CornerView_radius, 0);
        top_left_radius = a.getDimension(R.styleable.CornerView_top_left_radius, 0);
        top_right_radius = a.getDimension(R.styleable.CornerView_top_right_radius, 0);
        bottom_right_radius = a.getDimension(R.styleable.CornerView_bottom_right_radius, 0);
        bottom_left_radius = a.getDimension(R.styleable.CornerView_bottom_left_radius, 0);
        border = a.getDimension(R.styleable.CornerView_border, 0);
        bg = a.getColor(R.styleable.CornerView_backgroundColor, 0);
        borderColor = a.getColor(R.styleable.CornerView_borderColor,bg);
        renderView();
        a.recycle();
    }

    public void setTop_left_radius(float top_left_radius) {
        this.top_left_radius = top_left_radius;
        renderView();
    }

    public void setTop_right_radius(float top_right_radius) {
        this.top_right_radius = top_right_radius;
        renderView();
    }

    public void setBottom_right_radius(float bottom_right_radius) {
        this.bottom_right_radius = bottom_right_radius;
        renderView();
    }

    public void setBottom_left_radius(float bottom_left_radius) {
        this.bottom_left_radius = bottom_left_radius;
        renderView();
    }

    public void setBg(int bg) {
        this.bg = bg;
        renderView();
    }

    @Override
    public void setBackgroundColor(int color) {
        bg = color;
        renderView();
    }

    public void setRadius(float radius){
        this.radius = radius;
        renderView();
    }

    public void setBorder(float border){
        this.border = border;
        renderView();
    }

    public void setBorderColor(int borderColor){
        this.borderColor = borderColor;
        renderView();
    }

    private void renderView(){

//        BubbleDrawable bubbleDrawable = new BubbleDrawable();
//        bubbleDrawable.setBgColor(Color.BLUE);
//        bubbleDrawable.setCornerRadius(DensityUtils.dip2px(getContext(),5));
//        bubbleDrawable.setBubbleLength(DensityUtils.dip2px(getContext(),8));
//        bubbleDrawable.setArrowDirection(BubbleDrawable.ArrowDirection.BOTTOM);
//        setBackgroundDrawable(bubbleDrawable);
    }

}
