package top.sacz.timtool.hook.util;

import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class HookTestUtils {
    public static void hookTextView(CharSequence str) {
        try {
            XposedBridge.hookMethod(TextView.class.getMethod("setText", CharSequence.class), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    CharSequence text = (CharSequence) param.args[0];
                    if (text == null) return;
                    if (String.valueOf(text).contains(str)) {
                        LogUtils.addRunLog(str + "_TextView", LogUtils.getCallStack());
                    }
                }
            });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
