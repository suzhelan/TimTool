package top.sacz.timtool.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.kongzue.dialogx.interfaces.ScrollController;

public class CustomLinearLayout extends LinearLayout implements ScrollController {
    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public int getScrollDistance() {
        return 0;
    }

    @Override
    public boolean isCanScroll() {
        return false;
    }

    @Override
    public void lockScroll(boolean lockScroll) {

    }

    @Override
    public boolean isLockScroll() {
        return false;
    }
}
