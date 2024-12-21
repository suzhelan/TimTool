package top.sacz.timtool.hook.item.experiment.retrac;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicBoolean;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.item.experiment.SnowflakesFall;
import top.sacz.timtool.util.ScreenParamUtils;


public class SnowflakesFallTool {

    private final SnowflakesFall snowflakesFall;
    private final AtomicBoolean isShowing = new AtomicBoolean(false);
    /**
     * 布局监听器
     */
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    /**
     * 窗口管理器
     */
    private WindowManager windowManager;
    /**
     * 雪花View
     */
    private FallingView fallingView;
    /**
     * 悬浮窗布局
     */
    private WindowManager.LayoutParams layoutParams;

    private Activity activity;

    public SnowflakesFallTool(SnowflakesFall snowflakesFall) {
        this.snowflakesFall = snowflakesFall;
    }

    public void hideSnowflakesFall() {
        if (isShowing.getAndSet(false)) {
            //移除监听器 防止性能消耗
            View rootView = activity.getWindow().getDecorView();
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            //移除悬浮窗
            windowManager.removeViewImmediate(fallingView);
        }
    }

    public void showSnowflakesFall(Activity activity) {
        if (this.activity == null) this.activity = activity;
        if (windowManager == null) windowManager = activity.getWindowManager();
        if (layoutParams == null) layoutParams = getWindowManagerParams();
        if (fallingView == null) {
            fallingView = new FallingView(activity);
            fallingView.setClickable(false);
            FallObject.Builder builder = new FallObject.Builder(activity.getDrawable(R.drawable.img_snowflakes));
            int size = ScreenParamUtils.dpToPx(activity, 40);
            FallObject fallObject = builder.setSpeed(8, true)
                    .setSize(size, size, true).setWind(5, true).build();
            fallingView.addFallObject(fallObject, 50);
        }
        if (!isShowing.get()) {
            windowManager.addView(fallingView, layoutParams);
            isShowing.set(true);
            View rootView = activity.getWindow().getDecorView();
            //监听键盘
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    boolean mKeyboardUp = isKeyboardShown(rootView);
                    //通知
                    notifiesKeyboardHeightChange(rootView.getContext(), snowflakesFall.keyboardHeight);
                }
            };
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    /**
     * 监听同时通知键盘高度
     */
    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        snowflakesFall.keyboardHeight = rootView.getHeight() - rect.bottom;
        int heightDiff = rootView.getBottom() - rect.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private WindowManager.LayoutParams getWindowManagerParams() {
        layoutParams = new WindowManager.LayoutParams();
        //只在应用内展示
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //不覆盖点击事件
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.format = PixelFormat.RGBA_8888;
        //布局大小
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        return layoutParams;
    }

    /**
     * 通知键盘高度已更新 防止雪花悬浮窗盖住键盘导致键盘没反应
     */
    private void notifiesKeyboardHeightChange(Context context, int keyboardHeight) {
        if (keyboardHeight != 0 && layoutParams != null) {
            layoutParams.height = ScreenParamUtils.getScreenHeight(context) - keyboardHeight;
            windowManager.updateViewLayout(fallingView, layoutParams);
        } else if (layoutParams != null) {
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            windowManager.updateViewLayout(fallingView, layoutParams);
        }
    }
}
