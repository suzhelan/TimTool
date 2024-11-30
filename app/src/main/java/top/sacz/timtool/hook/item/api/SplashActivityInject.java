package top.sacz.timtool.hook.item.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

import top.sacz.timtool.hook.base.BaseHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.xphelper.XpHelper;
import top.sacz.xphelper.reflect.MethodUtils;

@HookItem("为聊天界面注入Res资源")
public class SplashActivityInject extends BaseHookItem {

    @SuppressLint("StaticFieldLeak")
    private static Activity chatActivity;

    public static Activity getChatActivity() {
        return chatActivity;
    }

    @Override
    public void loadHook(@NonNull ClassLoader loader) throws Exception {
        Method onCreateMethod = MethodUtils.create("com.tencent.mobileqq.activity.SplashActivity")
                .methodName("doOnCreate")
                .returnType(boolean.class)
                .params(Bundle.class)
                .first();
        hookAfter(onCreateMethod, param -> {
            Activity activity = (Activity) param.thisObject;
            chatActivity = activity;
            XpHelper.injectResourcesToContext(activity);
        }, 1000);
    }
}
