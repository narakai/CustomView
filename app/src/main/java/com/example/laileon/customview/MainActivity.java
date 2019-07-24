package com.example.laileon.customview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;

import com.example.laileon.customview.view.ChartView2;
import com.example.laileon.customview.view.CircleBarView.CircleBarView;
import com.example.laileon.customview.view.FallingView.FallingObject;
import com.example.laileon.customview.view.FallingView.FallingView;
import com.example.laileon.customview.view.MusicButtonView;
import com.example.laileon.customview.view.RippleBackground;
import com.example.laileon.customview.view.furigara.FuriganaView;
import com.example.laileon.customview.view.misports.MISportsConnectView;
import com.example.laileon.customview.view.misports.SportsData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    //    @BindView(R.id.round)
//    RoundIndicatorView mRoundIndicatorView;
//    @BindView(R.id.pieview)
//    PieView mPieView;
//    @BindView(R.id.falling)
//    FallingView mFallingView;
//    @BindView(R.id.circle)
//    CircleBarView mCircleBarView;
//    @BindView(R.id.music_btn)
//    MusicButtonView mImageView;
//    @BindView(R.id.chart2)
//    ChartView2 mChartView2;
    @BindView(R.id.mi_sports_loading_view)
    MISportsConnectView mMISportsConnectView;
    @BindView(R.id.connect_button)
    Button mButton;
    @BindView(R.id.fu_tv)
    FuriganaView mFuriganaView;
    @BindView(R.id.ripple)
    RippleBackground rp;
    @BindView(R.id.rp_fl)
    FrameLayout rpFl;

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
        rpFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rp.startRippleAnimation();
            }
        });
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

//        mChartView2.updateChartData(mChart);

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

        TextPaint tp = new TextPaint();
        tp.setColor(getResources().getColor(R.color.colorAccent));
        tp.setTextSize(60);
        String text = "{彼女;かのじょ}は{寒気;さむけ}を{防;ふせ}ぐために{厚;あつ}いコートを{着;き}ていた。";
        int mark_s = 11; // highlight 厚い in text (characters 11-13)
        int mark_e = 13;
        mFuriganaView.text_set(tp, text, mark_s, mark_e);
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
