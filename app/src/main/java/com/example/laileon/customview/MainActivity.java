package com.example.laileon.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;


import com.example.laileon.customview.view.PieData;
import com.example.laileon.customview.view.PieView;
import com.example.laileon.customview.view.RoundIndicatorView;

import java.util.ArrayList;
import java.util.Random;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.round)
    RoundIndicatorView mRoundIndicatorView;
    @Bind(R.id.pieview)
    PieView mPieView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Random random = new Random();
        mRoundIndicatorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRoundIndicatorView.setCurrentNumAnim(random.nextInt(500));
                return false;
            }
        });

        ArrayList<PieData> datas = new ArrayList<>();
        PieData pieData = new PieData("sloop", 60);
        PieData pieData2 = new PieData("sloop", 30);
        PieData pieData3 = new PieData("sloop", 40);
        PieData pieData4 = new PieData("sloop", 20);
        PieData pieData5 = new PieData("sloop", 20);
        datas.add(pieData);
        datas.add(pieData2);
        datas.add(pieData3);
        datas.add(pieData4);
        datas.add(pieData5);
        mPieView.setData(datas);
    }

}
