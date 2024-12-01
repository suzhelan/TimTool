package top.sacz.timtool.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.kongzue.dialogx.interfaces.ScrollController;

public class FollowRecycleViewLinearLayout extends LinearLayout implements ScrollController {


    private CustomRecycleView followRecycleView;

    public FollowRecycleViewLinearLayout(Context context) {
        super(context);
    }


    public FollowRecycleViewLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FollowRecycleViewLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FollowRecycleViewLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean isLockScroll() {
        return followRecycleView.isLockScroll();
    }

    @Override
    public void lockScroll(boolean lockScroll) {
        this.followRecycleView.lockScroll(lockScroll);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return followRecycleView.onTouchEvent(event);
    }

    public void setFollowRecycleView(CustomRecycleView customRecycleView) {
        this.followRecycleView = customRecycleView;
    }

    @Override
    public int getScrollDistance() {
        return followRecycleView.getScrollDistance();
    }

    @Override
    public boolean isCanScroll() {
        return followRecycleView.isCanScroll();
    }


}
