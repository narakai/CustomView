package com.example.laileon.customview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by laileon on 2017/12/25.
 */

public class MusicButtonView extends AppCompatImageView {
    private ObjectAnimator objectAnimator;

    public static final int STATE_PLAYING = 1;//正在播放
    public static final int STATE_PAUSE = 2;//暂停
    public static final int STATE_STOP = 3;//停止
    public int state;

    public MusicButtonView(Context context) {
        this(context,null);
    }

    public MusicButtonView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MusicButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        state = STATE_STOP;
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    public void playMusic() {
        if (state == STATE_STOP) {
            objectAnimator.start();//动画开始
            state = STATE_PLAYING;
        } else if (state == STATE_PAUSE) {
            objectAnimator.resume();//动画重新开始
            state = STATE_PLAYING;
        } else if (state == STATE_PLAYING) {
            objectAnimator.pause();//动画暂停
            state = STATE_PAUSE;
        }
    }

    public void stopMusic() {
        objectAnimator.end();//动画结束
        state = STATE_STOP;
    }
}
