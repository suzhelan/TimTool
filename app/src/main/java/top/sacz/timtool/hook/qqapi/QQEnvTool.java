package top.sacz.timtool.hook.qqapi;

import de.robv.android.xposed.XposedHelpers;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.MethodUtils;

public class QQEnvTool {

    public static String getCurrentUin() {
        try {
            Object runTime = getAppRuntime();
            if (runTime == null) return null;
            return MethodUtils.create(runTime)
                    .methodName("getCurrentAccountUin")
                    .returnType(String.class)
                    .callFirst(runTime);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getAppRuntime() throws Exception {
        Object application = MethodUtils.create(ClassUtils.findClass("com.tencent.common.app.BaseApplicationImpl"))
                .methodName("getApplication")
                .returnType(ClassUtils.findClass("com.tencent.common.app.BaseApplicationImpl"))
                .callFirstStatic();

        return MethodUtils.create(application)
                .methodName("getRuntime")
                .returnType(ClassUtils.findClass("mqq.app.AppRuntime"))
                .callFirst(application);
    }

    /**
     * uin转peerUid
     */
    public static String getUidFromUin(String uin) {
        Object o = getQRouteApi(ClassUtils.findClass("com.tencent.relation.common.api.IRelationNTUinAndUidApi"));
        return (String) XposedHelpers.callMethod(o, "getUidFromUin", uin);
    }

    /**
     * peerUid转uin
     */
    public static String getUinFromUid(String uid) {
        Object o = getQRouteApi(ClassUtils.findClass("com.tencent.relation.common.api.IRelationNTUinAndUidApi"));
        return (String) XposedHelpers.callMethod(o, "getUinFromUid", uid);
    }

    public static Object getQRouteApi(Class<?> clz) {
        return MethodUtils.create("com.tencent.mobileqq.qroute.QRoute")
                .methodName("api")
                .params(Class.class)
                .callFirstStatic(clz);

    }
}
