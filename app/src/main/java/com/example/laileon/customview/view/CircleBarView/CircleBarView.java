package com.example.laileon.customview.view.CircleBarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.laileon.customview.R;
import com.example.laileon.customview.utils.utils.DensityUtils;
import com.example.laileon.customview.utils.utils.ViewUtils;

/**
 * Created by laileon on 2017/12/22.
 */

public class CircleBarView extends View {
    private Paint rPaint;//绘制矩形的画笔
    private Paint progressPaint;//绘制圆弧的画笔
    private CircleBarAnim anim;

    private Paint bgPaint;//绘制背景圆弧的画笔

    private float progressNum;//可以更新的进度条数值
    private float maxNum;//进度条最大值

    private float progressSweepAngle;//进度条圆弧扫过的角度
    private float startAngle;//背景圆弧的起始角度
    private float sweepAngle;//背景圆弧扫过的角度

    private int defaultSize;//自定义View默认的宽高
    private int progressColor;//进度条圆弧颜色
    private int bgColor;//背景圆弧颜色
    private float barWidth;//圆弧进度条宽度
    private RectF mRectF;//绘制圆弧的矩形区域

    public CircleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        anim = new CircleBarAnim();
        rPaint = new Paint();
        rPaint.setStyle(Paint.Style.STROKE);//只描边，不填充
        rPaint.setColor(Color.RED);

        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);//只描边，不填充
        progressPaint.setAntiAlias(true);//设置抗锯齿
        progressPaint.setStrokeWidth(15);//随便设置一个画笔宽度，看看效果就好，之后会通过attr自定义属性进行设置

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.STROKE);//只描边，不填充
        bgPaint.setAntiAlias(true);//设置抗锯齿
        bgPaint.setStrokeWidth(15);

        progressNum = 0;
        maxNum = 100;//也是随便设的

        startAngle = 0;
        sweepAngle = 360;

        defaultSize = DensityUtils.dip2px(context, 100);
        barWidth = DensityUtils.dip2px(context, 10);
        mRectF = new RectF();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleBarView);
        progressColor = typedArray.getColor(R.styleable.CircleBarView_progress_color,Color.GREEN);//默认为绿色
        bgColor = typedArray.getColor(R.styleable.CircleBarView_bg_color,Color.GRAY);//默认为灰色
        startAngle = typedArray.getFloat(R.styleable.CircleBarView_start_angle,0);//默认为0
        sweepAngle = typedArray.getFloat(R.styleable.CircleBarView_sweep_angle,360);//默认为360
        barWidth = typedArray.getDimension(R.styleable.CircleBarView_bar_width,DensityUtils.dip2px(context,10));//默认为10dp
        typedArray.recycle();//typedArray用完之后需要回收，防止内存泄漏

        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(barWidth);

        bgPaint.setColor(bgColor);
        bgPaint.setStrokeWidth(barWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = ViewUtils.measureSize(defaultSize, heightMeasureSpec);
        int width = ViewUtils.measureSize(defaultSize, widthMeasureSpec);

        int min = Math.min(width, height);// 获取View最短边的长度
        setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形

        if (min >= barWidth * 2) {//这里简单限制了圆弧的最大宽度
            mRectF.set(barWidth / 2, barWidth / 2, min - barWidth / 2, min - barWidth / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        float x = 50;
//        float y = 50;
//        RectF rectF = new RectF(x, y, x + 300, y + 300);//建一个大小为300 * 300的正方形区域
        //这里角度0对应的是三点钟方向，顺时针方向递增
//        canvas.drawRect(rectF, rPaint);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, bgPaint);
        canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, progressPaint);
    }

    public class CircleBarAnim extends Animation {

        public CircleBarAnim() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            progressSweepAngle = interpolatedTime * sweepAngle * (progressNum / maxNum);//这里计算进度条的比例
        }
    }

    //写个方法给外部调用，用来设置动画时间
    public void setProgressNum(float progressNum, int time) {
        this.progressNum = progressNum;
        anim.setDuration(time);
        this.startAnimation(anim);
    }
}
