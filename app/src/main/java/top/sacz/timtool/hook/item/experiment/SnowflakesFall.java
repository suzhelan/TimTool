package top.sacz.timtool.hook.item.experiment;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.timtool.hook.core.factory.ExceptionFactory;
import top.sacz.timtool.hook.item.experiment.retrac.SnowflakesFallTool;
import top.sacz.xphelper.XpHelper;


@HookItem("辅助功能/季节专属/雪花飘落")
public class SnowflakesFall extends BaseSwitchFunctionHookItem {

    /**
     * 复用池 以便activity即使不在焦点也能保存之前view防止重新下雪
     */
    private static final HashMap<Activity, SnowflakesFallTool> showList = new HashMap<>();
    /**
     * 键盘高度
     */
    public int keyboardHeight = 0;

    @Override
    public String getTip() {
        return "想要留住雪花，可在掌心里，只会融化的更快";
    }

    private void updateStates(Activity activity, boolean isShow) {
        try {
            SnowflakesFallTool show = showList.get(activity);
            if (show == null) {
                show = new SnowflakesFallTool(this);
                XpHelper.injectResourcesToContext(activity);
                showList.put(activity, show);
            }
            if (isShow) {
                show.showSnowflakesFall(activity);
            } else {
                show.hideSnowflakesFall();
            }
        } catch (Exception e) {
            ExceptionFactory.add(this, e);
        }
    }

    @Override
    public boolean isLoadedByDefault() {
        return true;
    }

    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        //窗口焦点可见时
        hookAfter(Activity.class.getDeclaredMethod("onWindowFocusChanged", boolean.class), param -> {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean hasFocus = (boolean) param.args[0];
                    if (hasFocus) {
                        Activity activity = (Activity) param.thisObject;
                        updateStates(activity, true);
                    }
                }
            }, 50);
        });
        //通知关闭
        hookBefore(Activity.class.getDeclaredMethod("onPause"), param -> {
            Activity activity = (Activity) param.thisObject;
            updateStates(activity, false);
        });
        //act 销毁 从复用池移出
        hookBefore(Activity.class.getDeclaredMethod("onDestroy"), param -> {
            Activity activity = (Activity) param.thisObject;
            showList.remove(activity);
            //触发gc防止资源不回收
            System.gc();
        });
    }
}
