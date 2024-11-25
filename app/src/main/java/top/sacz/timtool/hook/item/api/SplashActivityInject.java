package top.sacz.timtool.hook.item.api;

import android.app.Activity;
import android.os.Bundle;

import java.lang.reflect.Method;

import top.sacz.timtool.hook.base.BaseHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.xphelper.XpHelper;
import top.sacz.xphelper.reflect.MethodUtils;

@HookItem("为聊天界面注入Res资源")
public class SplashActivityInject extends BaseHookItem {

    @Override
    public void loadHook(ClassLoader loader) throws Exception {
        Method onCreateMethod = MethodUtils.create("com.tencent.mobileqq.activity.SplashActivity")
                .methodName("doOnCreate")
                .returnType(boolean.class)
                .params(Bundle.class)
                .first();
        hookAfter(onCreateMethod, param -> {
            Activity activity = (Activity) param.thisObject;
            XpHelper.injectResourcesToContext(activity);
        }, 1000);
    }
}
