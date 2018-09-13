package com.example.laileon.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.laileon.customview.utils.utils.DensityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartView2 extends View {

    private List<Point> mPointList = new ArrayList<>();

    private List<Point> mPointList1 = new ArrayList<>();

    private int mLeftRightMargin;

    private int mDivideWidth;

    private int mMinPrice;

    private int mMaxPrice;

    private Shader mShader;

    private Path mPath = new Path();

    private Path mSharePath = new Path();

    private Path mLowerPath = new Path();

    private RectF mLowerRect = new RectF();

    private Paint mPaint = new Paint();

    private ChartData mChartData;

    private Context mContext;

    public ChartView2(Context context) {
        super(context);
        init(context);
    }

    public ChartView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mLeftRightMargin = DensityUtils.dip2px(context, 10);
        mShader = new LinearGradient(0, 0, 0, DensityUtils.dip2px(context, 100), new int[]{0x3000bedc, 0x0000bedc}, null, Shader.TileMode.REPEAT);
    }

    public void updateChartData(ChartData chartData) {
        if (chartData == null) {
            return;
        }
        mChartData = chartData;
        mPointList.clear();
        mPointList1.clear();
        for (Float price : mChartData.getPrices()) {
            mPointList.add(new Point());
            mPointList1.add(new Point());
        }
        mPointList1.add(new Point());
        mPointList1.add(new Point());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mChartData == null || mChartData.getPrices() == null || mChartData.getPrices().size() == 0) {
            return;
        }
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#00BEDC"));
        mPaint.clearShadowLayer();
        mPath.reset();
        mLowerPath.reset();
        mSharePath.reset();

        translate(canvas.getHeight(), canvas.getWidth());

        //曲线
        mPaint.setStrokeWidth(DensityUtils.dip2px(mContext, 1));
        measurePath();
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mShader);
        canvas.drawPath(mSharePath, mPaint);

    }

    private void measurePath() {
        mPointList1.get(0).set(mLeftRightMargin, mPointList.get(0).y);
        mPointList1.get(mPointList1.size() - 1).set(getWidth() - mLeftRightMargin, mPointList.get(mPointList.size() - 1).y);
        for (int i = 0; i < mPointList.size(); i++) {
            mPointList1.get(i + 1).set(mPointList.get(i).x, mPointList.get(i).y);
        }

        //保存曲线路径
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        final int lineSize = mPointList1.size();
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                Point point = mPointList1.get(valueIndex);
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    Point point = mPointList1.get(valueIndex - 1);
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    Point point = mPointList1.get(valueIndex - 2);
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < lineSize - 1) {
                Point point = mPointList1.get(valueIndex + 1);
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                mPath.moveTo(currentPointX, currentPointY);
                mSharePath.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float lineSmoothness = 0.16f;
                final float firstControlPointX = previousPointX + (lineSmoothness * firstDiffX);
                final float firstControlPointY = previousPointY + (lineSmoothness * firstDiffY);
                final float secondControlPointX = currentPointX - (lineSmoothness * secondDiffX);
                final float secondControlPointY = currentPointY - (lineSmoothness * secondDiffY);
                //画出曲线
                mPath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY, currentPointX, currentPointY);
                mSharePath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY, currentPointX, currentPointY);
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;

        }
        mSharePath.lineTo(getWidth() - mLeftRightMargin, getHeight());
        mSharePath.lineTo(mLeftRightMargin, getHeight());
        mSharePath.close();
    }

    private void translate(int height, int width) {
        List<Float> ps = mChartData.getPrices();
        float max = Collections.max(ps);
        float min = Collections.min(ps);
        float f = (max - min) / height / 0.4f;
        if (f == 0) {
            f = height / 3;
        }
        mDivideWidth = width / 4;
        for (int i = 0; i < mPointList.size(); i++) {
            int y = (int) ((ps.get(i) - min) / f + height * 0.25f);
            int x = i * mDivideWidth + mDivideWidth / 2;
            mPointList.get(i).set(x, height - y);
            float ff = ps.get(i);
            if (ff < mMinPrice && ff > 0 || mMinPrice == 0) {
                mMinPrice = (int) ff;
            }
            if (ff > mMaxPrice && ff > 0 || mMinPrice == 0) {
                mMaxPrice = (int) ff;
            }
        }
    }

    private class ChartData {
        List<Float> prices;
        List<String> labels;

        public List<Float> getPrices() {
            return prices;
        }

        public List<String> getLabels() {
            return labels;
        }
    }
}
