package top.sacz.timtool.hook.qqapi;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import top.sacz.timtool.hook.util.LogUtils;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.reflect.MethodUtils;

public class QQTroopTool {
    public static Object getMemberInfo(String group, String uin) {
        try {
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            Object iTroopMemberListRepoApi = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.troopmemberlist.ITroopMemberListRepoApi"));
            Method fetchTroopMemberInfo = MethodUtils.create(iTroopMemberListRepoApi.getClass())
                    .methodName("fetchTroopMemberInfo")
                    .returnType(void.class)
                    .params(String.class,
                            String.class,
                            boolean.class,
                            ClassUtils.findClass("androidx.lifecycle.LifecycleOwner"),
                            String.class,
                            Object.class)
                    .first();
            Class<?>[] parameterTypes = fetchTroopMemberInfo.getParameterTypes();
            Class<?> repoClass = parameterTypes[parameterTypes.length - 1];
            fetchTroopMemberInfo.invoke(iTroopMemberListRepoApi,
                    group, uin, true, null, "TroopMemberListActivity", Proxy.newProxyInstance(ClassUtils.getClassLoader(), new Class[]{repoClass},
                            ((proxy, method, args) -> {
                                if (method.getReturnType() == void.class && method.getParameterTypes().length == 1) {
                                    completableFuture.complete(args[0]);
                                    return null;
                                } else {
                                    return method.invoke(iTroopMemberListRepoApi, args);
                                }

                            })));
            return completableFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LogUtils.addError("plugin api", e);
            return null;
        }
    }

    public static String getMemberName(String group, String uin) {
        try {
            Object memberInfo = getMemberInfo(group, uin);
            Object nickInfo = FieldUtils.create(memberInfo)
                    .fieldName("nickInfo")
                    .firstValue(memberInfo);
            return MethodUtils.create(nickInfo)
                    .methodName("getShowName")
                    .returnType(String.class)
                    .callFirst(nickInfo);
        } catch (Exception e) {
            LogUtils.addError("plugin api", e);
            return null;
        }
    }
}
