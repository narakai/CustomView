package com.example.laileon.customview.view.FallingView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laileon on 2017/12/22.
 */

public class FallingView extends View {
    private Context mContext;
    private AttributeSet mAttrs;

    private int viewWidth;
    private int viewHeight;

    private static final int defaultWidth = 600;//默认宽度
    private static final int defaultHeight = 1000;//默认高度
    private static final int intervalTime = 5;//重绘间隔时间

    //    private Paint testPaint;
    private int snowY;

    private List<FallingObject> fallingObjects;

    private void init() {
        fallingObjects = new ArrayList<>();
    }

    public FallingView(Context context) {
        this(context, null);
    }

    public FallingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttrs = attrs;
        init();
    }

//    private void init() {
//        testPaint = new Paint();
//        testPaint.setColor(Color.WHITE);
//        testPaint.setStyle(Paint.Style.FILL);
//        snowY = 0;
//    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(defaultHeight, heightMeasureSpec);
        int width = measureSize(defaultWidth, widthMeasureSpec);
        setMeasuredDimension(width, height);

        viewWidth = width;
        viewHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawCircle(100, snowY, 25, testPaint);
//        getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                snowY += 15;
//                if (snowY > viewHeight) {//超出屏幕则重置雪球位置
//                    snowY = 0;
//                }
//                invalidate();
//            }
//        }, intervalTime);
        if (fallingObjects.size() > 0) {
            for (int i = 0; i < fallingObjects.size(); i++) {
                //然后进行绘制
                fallingObjects.get(i).drawObject(canvas);
            }
            // 隔一段时间重绘一次, 动画效果
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, intervalTime);
        }
    }

    /**
     * 向View添加下落物体对象
     *
     * @param fallObject 下落物体对象
     * @param num
     */
    public void addFallObject(final FallingObject fallObject, final int num) {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < num; i++) {
                    FallingObject newFallObject = new FallingObject(fallObject.builder, viewWidth, viewHeight);
                    fallingObjects.add(newFallObject);
                }
                invalidate();
                return true;
            }
        });
    }
}
