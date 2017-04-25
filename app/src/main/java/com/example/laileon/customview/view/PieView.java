package com.example.laileon.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.laileon.customview.utils.CustomView;

import java.util.ArrayList;

/**
 * Created by laileon on 2017/4/25.
 */


//        步骤	关键字	作用
//        1	构造函数	初始化(初始化画笔Paint)
//        2	onMeasure	测量View的大小(暂时不用关心)
//        3	onSizeChanged	确定View大小(记录当前View的宽高)
//        4	onLayout	确定子View布局(无子View，不关心)
//        5	onDraw	实际绘制内容(绘制饼状图)
//        6	提供接口	提供接口(提供设置数据的接口)

public class PieView extends CustomView {
    // 颜色表 (注意: 此处定义颜色使用的是ARGB，带Alpha通道的)
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    // 饼状图初始绘制角度
    private float mStartAngle = 0;
    // 数据
    private ArrayList<PieData> mData;

    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 设置起始角度
    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    // 设置数据
    public void setData(ArrayList<PieData> mData) {
        this.mData = mData;
        initData(mData);
        invalidate();   // 刷新
    }

    // 初始化数据
    private void initData(ArrayList<PieData> mData) {
        if (null == mData || mData.size() == 0)   // 数据有问题 直接返回
            return;

        float sumValue = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);

            sumValue += pie.getValue();       //计算数值和

            int j = i % mColors.length;       //设置颜色
            pie.setColor(mColors[j]);
        }

        float sumAngle = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);

            float percentage = pie.getValue() / sumValue;   // 百分比
            float angle = percentage * 360;                 // 对应的角度

            pie.setPercentage(percentage);                  // 记录百分比
            pie.setAngle(angle);                            // 记录角度大小
            sumAngle += angle;

            Log.i("angle", "" + pie.getAngle());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mData || mData.size() == 0)   // 数据有问题 直接返回
            return;
        mDeafultPaint.setStyle(Paint.Style.FILL);
        mDeafultPaint.setAntiAlias(true);

        // 当前起始角度
        float currentStartAngle = mStartAngle;
        // 将画布坐标原点移动到中心位置
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        // 饼状图半径 0.8 预留边界，为以后添加文字留的空间
        float r = (float) (Math.min(mViewWidth, mViewHeight) / 2 * 0.8);
        // 饼状图绘制区域
        RectF rectF = new RectF(-r, -r, r, r);

        for (int i = 0; i < mData.size(); i++) {
            PieData pieData = mData.get(i);
            mDeafultPaint.setColor(pieData.getColor());
            canvas.drawArc(rectF, currentStartAngle, pieData.getAngle(), true, mDeafultPaint);
            currentStartAngle += pieData.getAngle();

            // 平移画布 Math.toDegrees 弧度转化为角度 Math.toRadians 角度转化为弧度
            float mCenterX = (float) ((float) 0.6 * r * Math.cos(Math.toRadians(currentStartAngle - 0.5 * pieData.getAngle())));
            float mCenterY = (float) ((float) 0.6 * r * Math.sin(Math.toRadians(currentStartAngle - 0.5 * pieData.getAngle())));
            canvas.translate(mCenterX, mCenterY); // 文字中心坐标
            // 设置字体颜色
            mDefaultTextPaint.setColor(Color.BLACK);
            mDefaultTextPaint.setTextSize(20f);
            mDefaultTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("居中对齐" + i, 0, 0, mDefaultTextPaint);
            canvas.translate(-mCenterX, -mCenterY);//绘制文字完成回到原点
        }
    }
}
