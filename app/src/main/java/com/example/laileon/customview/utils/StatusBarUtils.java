package com.example.laileon.customview.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.ColorInt;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by schuman on 2018/12/26.
 */

public class StatusBarUtils {

    public static final int DEFAULT_STATUS_BAR_ALPHA = 112;

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, @ColorInt int color) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */

    public static void setColor(Activity activity, @ColorInt int color, int statusBarAlpha) {
        setColor(activity,color,statusBarAlpha,true);
    }

    public static void setColor(Activity activity, @ColorInt int color, int statusBarAlpha,boolean darkModeStatus) {
        boolean result = setStatusBarDarkMode(darkModeStatus,activity);
        if(result&&darkModeStatus){
            statusBarAlpha = 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Log.e("status", "higher than lollipop");
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Log.e("status", "lower than lollipop");
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int count = decorView.getChildCount();
            if (count > 0 && decorView.getChildAt(count - 1) instanceof StatusBarView) {
                decorView.getChildAt(count - 1).setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                StatusBarView statusView = createStatusBarView(activity, color, statusBarAlpha);
                decorView.addView(statusView);
            }
            setRootView(activity);
        }
    }


    public static boolean setStatusBarDarkMode(boolean darkmode, Activity activity) {


        boolean result = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkmode) {
            activity.getWindow().getDecorView().setSystemUiVisibility( activity.getWindow().getDecorView().getSystemUiVisibility()| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            result = true;

        }


        try {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            result = true;
        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("miui","error:"+e.getMessage());
        }

        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
//            Log.e("meizu","before:"+value+","+bit);
            if (darkmode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
//            Log.e("meizu", "after:" + value + "," + bit);
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            result = true;
        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("meizu", "error:"+e.getMessage());

        }
//        Log.e("setdarkmode",result+"");
        return result;



    }


    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColorForSwipeBack(Activity activity, int color) {
        setColorForSwipeBack(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColorForSwipeBack(Activity activity, @ColorInt int color, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup contentView = ((ViewGroup) activity.findViewById(android.R.id.content));
            contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
            contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            setTransparentForWindow(activity);
        }
    }

    /**
     * 设置状态栏纯色 不加半透明效果
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColorNoTranslucent(Activity activity, @ColorInt int color) {
        setColor(activity, color, 0);
    }

    /**
     * 设置状态栏颜色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    @Deprecated
    public static void setColorDiff(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        // 移除半透明矩形,以免叠加
        if (contentView.getChildCount() > 1) {
            contentView.getChildAt(1).setBackgroundColor(color);
        } else {
            contentView.addView(createStatusBarView(activity, color));
        }
        setRootView(activity);
    }

    /**
     * 使状态栏半透明
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    public static void setTranslucent(Activity activity) {
        setTranslucent(activity, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 使状态栏半透明
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucent(Activity activity, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparent(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 针对根布局是 CoordinatorLayout, 使状态栏半透明
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucentForCoordinatorLayout(Activity activity, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    /**
     * 使状态栏透明(5.0以上半透明效果,不建议使用)
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    @Deprecated
    public static void setTranslucentDiff(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setRootView(activity);
        }
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为DrawerLayout 布局设置状态栏颜色,纯色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    public static void setColorNoTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, 0);
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity       需要设置的activity
     * @param drawerLayout   DrawerLayout
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color,
                                               int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
//            Log.e("statusbar_drawer", "hight than lollipop");
        } else {
//            Log.e("statusbar_drawer", "lower than lollipop");
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 生成一个状态栏大小的矩形
        // 添加 statusBarView 到布局中
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        if (contentLayout.getChildCount() > 0 && contentLayout.getChildAt(0) instanceof StatusBarView) {
            contentLayout.getChildAt(0).setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
        } else {
            StatusBarView statusBarView = createStatusBarView(activity, color);
            contentLayout.addView(statusBarView, 0);
        }
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1)
                    .setPadding(contentLayout.getPaddingLeft(), getStatusBarHeight(activity) + contentLayout.getPaddingTop(),
                            contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());
        }
        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 设置 DrawerLayout 属性
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout DrawerLayout 的内容布局
     */
    private static void setDrawerLayoutProperty(DrawerLayout drawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    @Deprecated
    public static void setColorForDrawerLayoutDiff(Activity activity, DrawerLayout drawerLayout, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            if (contentLayout.getChildCount() > 0 && contentLayout.getChildAt(0) instanceof StatusBarView) {
                contentLayout.getChildAt(0).setBackgroundColor(calculateStatusColor(color, DEFAULT_STATUS_BAR_ALPHA));
            } else {
                // 添加 statusBarView 到布局中
                StatusBarView statusBarView = createStatusBarView(activity, color);
                contentLayout.addView(statusBarView, 0);
            }
            // 内容布局不是 LinearLayout 时,设置padding top
            if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
                contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
            }
            // 设置属性
            setDrawerLayoutProperty(drawerLayout, contentLayout);
        }
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        setTranslucentForDrawerLayout(activity, drawerLayout, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForDrawerLayout(activity, drawerLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        }

        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明(5.0以上半透明效果,不建议使用)
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    @Deprecated
    public static void setTranslucentForDrawerLayoutDiff(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setClipToPadding(true);
            // 设置抽屉布局属性
            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
            vg.setFitsSystemWindows(false);
            // 设置 DrawerLayout 属性
            drawerLayout.setFitsSystemWindows(false);
        }
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏全透明
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTransparentForImageView(Activity activity, View needOffsetView) {
        setTranslucentForImageView(activity, 0, needOffsetView);
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageView(Activity activity, View needOffsetView) {
        setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageView(final Activity activity, int statusBarAlpha, final View needOffsetView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForWindow(activity);
        addTranslucentView(activity, statusBarAlpha);
        if (needOffsetView != null) {
            try {
//                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
//                layoutParams.setMargins(0, getStatusBarHeight(activity), 0, 0);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
                int top;
                if(needOffsetView.getTag() != null && needOffsetView.getTag() instanceof Integer){
                    top = (int) needOffsetView.getTag();
                }else{
                    top = layoutParams.topMargin + getStatusBarHeight(activity);
                }
                layoutParams.setMargins(layoutParams.leftMargin,top, layoutParams.rightMargin, layoutParams.bottomMargin);
                needOffsetView.setTag(top);
            }catch (Exception e){
//                Log.e("error",e.getMessage());
                needOffsetView.post(new Runnable() {
                    @Override
                    public void run() {
//                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
//                        layoutParams.setMargins(0, getStatusBarHeight(activity), 0, 0);
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
                        int top;
                        if(needOffsetView.getTag() != null && needOffsetView.getTag() instanceof Integer){
                            top = (int) needOffsetView.getTag();
                        }else{
                            top = layoutParams.topMargin + getStatusBarHeight(activity);
                        }
                        layoutParams.setMargins(layoutParams.leftMargin,top, layoutParams.rightMargin, layoutParams.bottomMargin);
                        needOffsetView.setTag(top);
                    }
                });
            }
        }
    }

    public static void setTranslucentForImageViewWithViews(final Activity activity, int statusBarAlpha, View[] needOffsetViews) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForWindow(activity);
        addTranslucentView(activity, statusBarAlpha);

        if (needOffsetViews != null && needOffsetViews.length > 0) {
            for(int i = 0; i < needOffsetViews.length; i++) {
                final View needOffsetView  = needOffsetViews[i];
                if(needOffsetView == null){
                    continue;
                }
                try {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
                    int top;
                    if(needOffsetView.getTag() != null && needOffsetView.getTag() instanceof Integer){
                        top = (int) needOffsetView.getTag();
                    }else{
                        top = layoutParams.topMargin + getStatusBarHeight(activity);
                    }
                    layoutParams.setMargins(layoutParams.leftMargin,top, layoutParams.rightMargin, layoutParams.bottomMargin);
                    needOffsetView.setTag(top);
                } catch (Exception e) {
//                    Log.e("error", e.getMessage());
                    needOffsetView.post(new Runnable() {
                        @Override
                        public void run() {
                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
                            int top;
                            if(needOffsetView.getTag() != null && needOffsetView.getTag() instanceof Integer){
                                top = (int) needOffsetView.getTag();
                            }else{
                                top = layoutParams.topMargin + getStatusBarHeight(activity);
                            }
                            layoutParams.setMargins(layoutParams.leftMargin,top, layoutParams.rightMargin, layoutParams.bottomMargin);
                            needOffsetView.setTag(top);
                        }
                    });
                }
            }
        }
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageViewInFragment(Activity activity, View needOffsetView) {
        setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTransparentForImageViewInFragment(Activity activity, View needOffsetView) {
//        setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
        setTransparentForImageViewInFragment(activity,needOffsetView,true);
    }

    public static void setTransparentForImageViewInFragmentWithViews(Activity activity, View[] needOffsetView) {
//        setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
        setTransparentForImageViewInFragmentWithViews(activity,needOffsetView,true);
    }

    public static void setTransparentForImageViewInFragment(Dialog dialog, View needOffsetView) {
//        setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
        if(dialog.getOwnerActivity() != null) {
            setTransparentForImageViewInFragment(dialog.getOwnerActivity(), needOffsetView, true);
        }
        setTransparentForWindow(dialog);
    }

    public static void setTransparentForImageViewInFragment(Activity activity, View needOffsetView,boolean darkModeStauts) {
        setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
        setStatusBarDarkMode(darkModeStauts,activity);
    }

    public static void setTransparentForImageViewInFragmentWithViews(Activity activity, View[] needOffsetView, boolean darkModeStauts) {
        setTranslucentForImageViewInFragmentWithViews(activity, 0, needOffsetView);
        setStatusBarDarkMode(darkModeStauts,activity);
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageViewInFragment(Activity activity, int statusBarAlpha, View needOffsetView) {
        setTranslucentForImageView(activity, statusBarAlpha, needOffsetView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            clearPreviousSetting(activity);
        }
    }

    public static void setTranslucentForImageViewInFragmentWithViews(Activity activity, int statusBarAlpha, View[] needOffsetView) {
        setTranslucentForImageViewWithViews(activity, statusBarAlpha, needOffsetView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            clearPreviousSetting(activity);
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void clearPreviousSetting(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        int count = decorView.getChildCount();
        if (count > 0 && decorView.getChildAt(count - 1) instanceof StatusBarView) {
            decorView.removeViewAt(count - 1);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private static void addTranslucentView(Activity activity, int statusBarAlpha) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        if (contentView.getChildCount() > 1) {
            contentView.getChildAt(1).setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
        }
        isPageChange = true;
    }

    public static void setStatusbarColorDynamic(Activity activity, @ColorInt int Color) {
        if(activity == null){
            return;
        }
        isPageChange = false;
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        if (contentView.getChildCount() > 1) {
            contentView.getChildAt(1).setBackgroundColor(Color);
        } else {
            contentView.addView(createStatusBarView(activity,Color));
        }

    }
    public static boolean isPageChange = false;
    public static void setStatusbarColorDynamic(final Activity activity, @ColorInt final int startColor, @ColorInt final int endColor, View scrollView, final View measuredView) {
        if(activity == null || scrollView == null){
            return;
        }
        if(scrollView.getTag() != null && scrollView.getTag() instanceof ViewTreeObserver.OnScrollChangedListener) {
            scrollView.getViewTreeObserver().removeOnScrollChangedListener((ViewTreeObserver.OnScrollChangedListener) scrollView.getTag());
        }
        ViewTreeObserver.OnScrollChangedListener scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(measuredView == null || isPageChange){
                    return;
                }
                int[] loc = new int[2];
                measuredView.getLocationInWindow(loc);
                float rate = Math.abs(loc[1])/(float)measuredView.getHeight();
                if(rate < 0){
                    rate = 0;
                }else if(rate > 1){
                    rate = 1;
                }

                int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * rate);
                int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * rate);
                int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * rate);
                int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * rate);
                StatusBarUtils.setStatusbarColorDynamic(activity, Color.argb(a,r,g,b));
            }
        };
        scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);
        scrollView.setTag(scrollChangedListener);

        if(measuredView == null){
            return;
        }
        int[] loc = new int[2];
        measuredView.getLocationInWindow(loc);
        float rate = Math.abs(loc[1])/(float)measuredView.getHeight();
        if(rate < 0){
            rate = 0;
        }else if(rate > 1){
            rate = 1;
        }


        int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * rate);
        int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * rate);
        int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * rate);
        int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * rate);

        StatusBarUtils.setStatusbarColorDynamic(activity, Color.argb(a,r,g,b));

    }

    public static void setStatusbarColorDynamic(final Activity activity, @ColorInt final int startColor, @ColorInt final int endColor, final View scrollView, final View measuredView, final boolean measureTopOffset, final View extraOffsetView, final RateCallback callback, final boolean ignoreStatus) {
        if(activity == null || scrollView == null){
            return;
        }
        if(scrollView.getTag() != null && scrollView.getTag() instanceof ViewTreeObserver.OnScrollChangedListener) {
            scrollView.getViewTreeObserver().removeOnScrollChangedListener((ViewTreeObserver.OnScrollChangedListener) scrollView.getTag());
        }
        ViewTreeObserver.OnScrollChangedListener scrollChangedListener = new StatusBarScrollListener(activity,startColor,endColor,scrollView,measuredView,measureTopOffset,extraOffsetView,callback,ignoreStatus);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);
        scrollView.setTag(scrollChangedListener);

        if(measuredView == null){
            return;
        }
        int[] loc = new int[2];
        measuredView.getLocationInWindow(loc);
        float rate = Math.abs(loc[1])/(float)measuredView.getHeight();
        if(rate < 0){
            rate = 0;
        }else if(rate > 1){
            rate = 1;
        }


        int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * rate);
        int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * rate);
        int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * rate);
        int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * rate);

        StatusBarUtils.setStatusbarColorDynamic(activity, Color.argb(a,r,g,b));

    }

    public static class StatusBarScrollListener implements ViewTreeObserver.OnScrollChangedListener{

        private Activity activity;
        private  @ColorInt int startColor;
        private  @ColorInt int endColor;
        private View scrollView;
        private View measuredView;
        private boolean measureTopOffset;
        private View extraOffsetView;
        private RateCallback callback;
        private boolean ignoreStatus;

        public StatusBarScrollListener(Activity activity, int startColor, int endColor, View scrollView, View measuredView, boolean measureTopOffset, View extraOffsetView, RateCallback callback, boolean ignoreStatus) {
            this.activity = activity;
            this.startColor = startColor;
            this.endColor = endColor;
            this.scrollView = scrollView;
            this.measuredView = measuredView;
            this.measureTopOffset = measureTopOffset;
            this.extraOffsetView = extraOffsetView;
            this.callback = callback;
            this.ignoreStatus = ignoreStatus;
        }

        @Override
        public void onScrollChanged() {
            if(measuredView == null || isPageChange){
                return;
            }
            int[] loc = new int[2];
            measuredView.getLocationInWindow(loc);
            float rate;
            int extraOffset = extraOffsetView == null ? 0 : extraOffsetView.getHeight();
            if(measureTopOffset){
                rate = scrollView.getScrollY()/(float)(loc[1] + scrollView.getScrollY() - extraOffset);
            }else{
                rate = Math.abs(loc[1])/(float)measuredView.getHeight();
            }


            if(rate < 0){
                rate = 0;
            }else if(rate > 1){
                rate = 1;
            }

            int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * rate);
            int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * rate);
            int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * rate);
            int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * rate);
            StatusBarUtils.setStatusbarColorDynamic(activity, Color.argb(ignoreStatus ? 0 : a,r,g,b));
            if(callback != null){
                callback.onRateUpdate(rate,Color.argb(a,r,g,b),measureTopOffset ? scrollView.getScrollY() < 0 : loc[1] > 0);
            }
        }
    }

    public static void removeDynamicColor(View scrollView){
        if(scrollView != null && scrollView.getTag() != null && scrollView.getTag() instanceof ViewTreeObserver.OnScrollChangedListener) {
            scrollView.getViewTreeObserver().removeOnScrollChangedListener((ViewTreeObserver.OnScrollChangedListener) scrollView.getTag());
        }
    }

    public interface RateCallback{
        void onRateUpdate(float rate, @ColorInt int color, boolean isOverScroll);
    }

    /**
     * 生成一个和状态栏大小相同的彩色矩形条
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static StatusBarView createStatusBarView(Activity activity, @ColorInt int color) {
        // 绘制一个和状态栏一样高的矩形
        StatusBarView statusBarView = new StatusBarView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private static StatusBarView createStatusBarView(Activity activity, @ColorInt int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        StatusBarView statusBarView = new StatusBarView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        return statusBarView;
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup)childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 设置透明
     */
    private static void setTransparentForWindow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private static void setTransparentForWindow(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            dialog.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private static StatusBarView createTranslucentStatusBarView(Activity activity, int alpha) {
        // 绘制一个和状态栏一样高的矩形
//        Log.e("createStatusbarview",alpha+","+activity.getClass().getSimpleName());
        StatusBarView statusBarView = new StatusBarView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    public static class StatusBarView  extends View {
        public StatusBarView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public StatusBarView(Context context) {
            super(context);
        }
    }
}