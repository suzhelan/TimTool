package top.sacz.timtool.hook.item.api;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.base.ApiHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.Ignore;
import top.sacz.xphelper.reflect.MethodUtils;

@HookItem("QQ长按菜单接口")
public class QQCustomMenu extends ApiHookItem {

    /**
     * 抽象类
     */
    private static Class<?> baseMenuItemClass;

    public static Object createMenuItem(Object aioMsgItem, int id, String text, Callable<?> callable) {
        File generatedDir = HookEnv.getHostAppContext().getDir("generated", Context.MODE_PRIVATE);
        try (DynamicType.Unloaded<?> make = new ByteBuddy().subclass(baseMenuItemClass)
                //text
                .method(ElementMatchers.named("f")).intercept(FixedValue.value(text))
                //新方法 不知道是啥
                .method(ElementMatchers.named("e")).intercept(FixedValue.value(text))
                //id
                .method(ElementMatchers.named("b")).intercept(FixedValue.value(id))
                //被点击回调
                .method(ElementMatchers.returns(void.class)).intercept(MethodCall.call(callable))
                //res id 也就是图标
                .method(ElementMatchers.named("c")).intercept(FixedValue.value(id))
                .make()) {
            Class<?> generatedClass = make.load(baseMenuItemClass.getClassLoader(), new AndroidClassLoadingStrategy.Wrapping(generatedDir)).getLoaded();
            return generatedClass.getDeclaredConstructor(ClassUtils.findClass("com.tencent.mobileqq.aio.msg.AIOMsgItem"))
                    .newInstance(aioMsgItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadHook(@NonNull ClassLoader loader) throws Throwable {
        Method findMethod = MethodUtils.create("com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout")
                .returnType(View.class)
                .params(int.class, Ignore.class, boolean.class, float[].class)
                .first();
        baseMenuItemClass = findMethod.getParameters()[1].getType();
    }
}
