package com.example.laileon.customview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.laileon.customview.view.CircleBarView.CircleBarView;
import com.example.laileon.customview.view.FallingView.FallingObject;
import com.example.laileon.customview.view.FallingView.FallingView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //    @Bind(R.id.round)
//    RoundIndicatorView mRoundIndicatorView;
//    @Bind(R.id.pieview)
//    PieView mPieView;
    @Bind(R.id.falling)
    FallingView mFallingView;
    @Bind(R.id.circle)
    CircleBarView mCircleBarView;

    private Paint snowPaint;
    private Canvas bitmapCanvas;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        draw();
    }

    private void draw() {
        //        final Random random = new Random();
//        mRoundIndicatorView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mRoundIndicatorView.setCurrentNumAnim(random.nextInt(500));
//                return false;
//            }
//        });
//
//        ArrayList<PieData> datas = new ArrayList<>();
//        PieData pieData = new PieData("sloop", 60);
//        PieData pieData2 = new PieData("sloop", 30);
//        PieData pieData3 = new PieData("sloop", 40);
//        PieData pieData4 = new PieData("sloop", 20);
//        PieData pieData5 = new PieData("sloop", 20);
//        datas.add(pieData);
//        datas.add(pieData2);
//        datas.add(pieData3);
//        datas.add(pieData4);
//        datas.add(pieData5);
//        mPieView.setData(datas);

        //绘制雪球bitmap
        snowPaint = new Paint();
        snowPaint.setColor(Color.WHITE);
        snowPaint.setStyle(Paint.Style.FILL);
        bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawCircle(25, 25, 25, snowPaint);

        //初始化一个雪球样式的fallObject
//        FallingObject fallingObject = new FallingObject.Builder(bitmap).setSpeed(5).build();
        FallingObject.Builder builder = new FallingObject.Builder(getResources().getDrawable(R.drawable.snow));
        FallingObject fallingObject = builder.setSpeed(5, true)
                .setSize(80, 80, true)
                .setWind(5, true, true)
                .build();
        mFallingView.addFallObject(fallingObject, 20);
        mCircleBarView.setProgressNum(100, 1000);
    }

}
