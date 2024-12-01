package top.sacz.timtool.hook.qqapi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import top.sacz.timtool.hook.util.LogUtils;
import top.sacz.timtool.hook.util.ToastTool;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.MethodUtils;

public class QQSendMsgTool {
    /**
     * 发送一条消息
     *
     * @param contact     发送联系人 通过 {@link ContactUtils} 类创建
     * @param elementList 元素列表 通过 {@link CreateElement}创建元素
     */
    public static void sendMsg(Object contact, ArrayList<Object> elementList) {
        if (contact == null) {
            ToastTool.show("contact==null");
            return;
        }
        if (elementList == null) {
            ToastTool.show("elementList==null");
            return;
        }
        Class<?> IMsgServiceClass = ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgService");
        Object msgServer = QQEnvTool.getQRouteApi(IMsgServiceClass);
        MethodUtils.create(msgServer.getClass())
                .params(
                        ClassUtils.findClass("com.tencent.qqnt.kernelpublic.nativeinterface.Contact"),
                        ArrayList.class,
                        ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback")
                )
                .returnType(void.class)
                .methodName("sendMsg")
                .callFirst(msgServer, contact, elementList, Proxy.newProxyInstance(ClassUtils.getClassLoader(), new Class[]{ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback")}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // void onResult(int i2, String str);

                        return null;
                    }
                }));
    }

    public static void forwardMsg(ArrayList<Long> msgIdList, Object contact, ArrayList<Object> targetContactList) {
        try {
            Class<?> IMsgServiceClass = ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgService");
            Object msgServer = QQEnvTool.getQRouteApi(IMsgServiceClass);
            Class<?> callbackClass = ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.IForwardOperateCallback");
            Method forwardMsgMethod = MethodUtils.create(msgServer.getClass())
                    .params(ArrayList.class,
                            ClassUtils.findClass("com.tencent.qqnt.kernelpublic.nativeinterface.Contact"),
                            ArrayList.class,
                            ArrayList.class,
                            callbackClass)
                    .returnType(void.class)
                    .methodName("forwardMsg")
                    .first();
            forwardMsgMethod.invoke(msgServer, msgIdList, contact, targetContactList, null, Proxy.newProxyInstance(ClassUtils.getClassLoader(), new Class[]{callbackClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // void onResult(int i2, String str);
                    return null;
                }
            }));
        } catch (Exception e) {
            LogUtils.addError(e);
        }
    }
}
