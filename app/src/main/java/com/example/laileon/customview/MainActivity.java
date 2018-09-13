package com.example.laileon.customview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.laileon.customview.view.ChartView2;
import com.example.laileon.customview.view.CircleBarView.CircleBarView;
import com.example.laileon.customview.view.FallingView.FallingObject;
import com.example.laileon.customview.view.FallingView.FallingView;
import com.example.laileon.customview.view.MusicButtonView;
import com.example.laileon.customview.view.misports.MISportsConnectView;
import com.example.laileon.customview.view.misports.SportsData;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //    @Bind(R.id.round)
//    RoundIndicatorView mRoundIndicatorView;
//    @Bind(R.id.pieview)
//    PieView mPieView;
//    @Bind(R.id.falling)
//    FallingView mFallingView;
//    @Bind(R.id.circle)
//    CircleBarView mCircleBarView;
//    @Bind(R.id.music_btn)
//    MusicButtonView mImageView;
    @Bind(R.id.chart2)
    ChartView2 mChartView2;
    @Bind(R.id.mi_sports_loading_view)
    MISportsConnectView mMISportsConnectView;
    @Bind(R.id.connect_button)
    Button mButton;

    private Paint snowPaint;
    private Canvas bitmapCanvas;
    private Bitmap bitmap;
    private boolean connect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        draw();
        setData();
//        mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mImageView.playMusic();
//            }
//        });
    }

    private void setData() {
        ChartView2.ChartData mChart = new ChartView2.ChartData();
        List<Float> prices = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        prices.add(1234.1f);
        prices.add(1784.1f);
        prices.add(1904.1f);
        prices.add(884.1f);

        labels.add("Mon");
        labels.add("Tue");
        labels.add("Thu");
        labels.add("Wen");

        mChart.setPrices(prices);
        mChart.setLabels(labels);

        mChartView2.updateChartData(mChart);

        SportsData sportsData = new SportsData();
        sportsData.step = 2714;
        sportsData.distance = 1700;
        sportsData.calories = 34;
        sportsData.progress = 75;
        mMISportsConnectView.setSportsData(sportsData);

        final Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        connect = !connect;
                        mMISportsConnectView.setConnected(connect);
                        connectButton.setText(connect ? getString(R.string.disconnect) : getString(R.string.connect));
                    }
                }, 500);
            }
        });
    }

//    private void draw() {
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
//        snowPaint = new Paint();
//        snowPaint.setColor(Color.WHITE);
//        snowPaint.setStyle(Paint.Style.FILL);
//        bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
//        bitmapCanvas = new Canvas(bitmap);
//        bitmapCanvas.drawCircle(25, 25, 25, snowPaint);

    //初始化一个雪球样式的fallObject
//        FallingObject fallingObject = new FallingObject.Builder(bitmap).setSpeed(5).build();
//        FallingObject.Builder builder = new FallingObject.Builder(getResources().getDrawable(R.drawable.snow));
//        FallingObject fallingObject = builder.setSpeed(5, true)
//                .setSize(80, 80, true)
//                .setWind(5, true, true)
//                .build();
//        mFallingView.addFallObject(fallingObject, 20);
//        mCircleBarView.setProgressNum(100, 1000);
//    }

}
