package top.sacz.timtool.hook;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.base.BaseHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.xphelper.XpHelper;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.ConstructorUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.reflect.MethodUtils;

/**
 * 注入QQ设置界面入口 Tim通用
 *
 * @author suzhelan
 */
@HookItem("注入QQ设置界面")
public class QQSettingInject extends BaseHookItem {

    /**
     * 直接照搬qs
     */
    private void hookQQ8970Setting() throws Exception {
        Method onCreate = MethodUtils.create("com.tencent.mobileqq.setting.main.MainSettingConfigProvider")
                .returnType(List.class)
                .params(Context.class)
                .first();
        hookAfter(onCreate, param -> {
            Context context = (Context) param.args[0];
            XpHelper.injectResourcesToContext(context);

            //获取方法的返回结果 item组包装器List-结构和当前类的DemoItemGroupWraper类似
            Object result = param.getResult();
            List<Object> itemGroupWraperList = (List<Object>) result;
            //获取返回的集合泛类型
            Class<?> itemGroupWraperClass = itemGroupWraperList.get(0).getClass();
            //循环包装器组集合 目的是获取里面的元素
            for (Object wrapper : itemGroupWraperList) {
                try {
                    //获取包装器里实际存放的Item集合
                    List<Object> itemList = FieldUtils.create(wrapper.getClass())
                            .fieldType(List.class)
                            .firstValue(wrapper);
                    //筛选
                    if (itemList == null || itemList.isEmpty()) continue;
                    String name = itemList.get(0).getClass().getName();

                    if (!name.startsWith("com.tencent.mobileqq.setting.processor")) continue;
                    //获取itemList的首个元素并取得Class
                    Class<?> itemClass = itemList.get(0).getClass();
                    //新建自己的Item
                    Object mItem = ConstructorUtils.newInstance(itemClass, new Class[]{Context.class, int.class, CharSequence.class, int.class}, context, 0x520a, context.getString(R.string.app_name), R.mipmap.ic_launcher_round);
                    //在这个类查找所有符合 public void ?(Function0 function0)的方法 可以查找到两个 一个是点击事件 一个是item刚被初始化时的事件
                    List<Method> setOnClickMethods = MethodUtils.create(itemClass)
                            .returnType(void.class)
                            .params(ClassUtils.findClass("kotlin.jvm.functions.Function0"))
                            .getResult();
                    //动态代理设置事件
                    Object onClickListener = Proxy.newProxyInstance(HookEnv.getInstance().getHostClassLoader(),
                            new Class[]{ClassUtils.findClass("kotlin.jvm.functions.Function0")},
                            new OnClickListener(context, itemClass));

                    for (Method setOnClickMethod : setOnClickMethods) {
                        setOnClickMethod.invoke(mItem, onClickListener);
                    }

                    //新建类似包装器里的itemList的list用来存放自己的mItem
                    List<Object> mItemGroup = new ArrayList<>();
                    mItemGroup.add(mItem);
                    //按长度获取item包装器的构造器
                    Constructor<?> itemGroupWraperConstructor = ConstructorUtils.build(itemGroupWraperClass).paramCount(5).first();
                    //新建包装器实例并添加到返回结果
                    Object itemGroupWrap = itemGroupWraperConstructor.newInstance(mItemGroup, null, null, 6, null);
                    itemGroupWraperList.add(0, itemGroupWrap);
                    break;
                } catch (Exception e) {
                    /*
                     * itemClass可能是com.tencent.mobileqq.setting.processor.b 而不是我们想要的 所以需要判断过滤第一次和catch过滤第二次
                     * 通常此catch由ConstructorUtils找不到构造方法抛出异常以实现第二次过滤
                     */
                }
            }

        }, 500);

    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {
        hookQQ8970Setting();
    }


    /**
     * 创建脚本入口项
     */

    private class OnClickListener implements InvocationHandler {

        private final Context qSettingActivity;

        private final Class<?> itemClass;

        private OnClickListener(Context qqSettingActivity, Class<?> itemClass) {
            qSettingActivity = qqSettingActivity;
            this.itemClass = itemClass;

        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            boolean isEnterModuleActivity = false;
            Throwable throwable = new Throwable();
            StackTraceElement[] stackTraceElements = throwable.getStackTrace();
            // 有被自己聪明到)
            //通过调用栈来确定是不是被指定的方法调用的
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                //判断是不是以此类名开头的内部类再处理 (也可以避免栈中出现此类后loadClass找不到类抛错)
                if (!stackTraceElement.getClassName().startsWith(itemClass.getName())) continue;
                //加载此类
                Class<?> stackClass = ClassUtils.findClass(stackTraceElement.getClassName());
                //获取接口列表
                Class<?>[] interfacesList = stackClass.getInterfaces();
                //判断实现接口和方法
                if (interfacesList[0] == View.OnClickListener.class && stackTraceElement.getMethodName().equals("onClick")) {
                    isEnterModuleActivity = true;
                    break;
                }
            }
            if (isEnterModuleActivity) {
                Toast.makeText(qSettingActivity, "进入设置页", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }


    /**
     * qq设置页差不多是这样的结构展示列表
     */
    class DemoItemGroupWrapper {
        public List<DemoItem> demoItemList;
    }

    class DemoItem {

        String text;
        ImageView image;
    }
}
