package top.sacz.xphelper.reflect;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import top.sacz.xphelper.base.BaseFinder;
import top.sacz.xphelper.util.CheckClassType;
import top.sacz.xphelper.util.DexMethodDescriptor;

public class MethodUtils extends BaseFinder<Method> {

    private String methodName;
    private Class<?> returnType;
    private Class<?>[] methodParams;
    private Integer paramCount;

    /**
     * 通过方法签名获取方法对象
     */
    public static Method getMethodByDesc(String desc) throws NoSuchMethodException {
        Method method = new DexMethodDescriptor(desc).getMethodInstance(ClassUtils.getClassLoader());
        method.setAccessible(true);
        return method;
    }

    public static Method getMethodByDesc(String desc, ClassLoader classLoader) throws NoSuchMethodException {
        Method method = new DexMethodDescriptor(desc).getMethodInstance(classLoader);
        method.setAccessible(true);
        return method;
    }

    public static MethodUtils create(Object target) {
        return create(target.getClass());
    }

    public static MethodUtils create(Class<?> fromClass) {
        MethodUtils methodUtils = new MethodUtils();
        methodUtils.setDeclaringClass(fromClass);
        return methodUtils;
    }

    public static MethodUtils create(String formClassName) {
        return create(ClassUtils.findClass(formClassName));
    }

    public MethodUtils returnType(Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }

    public MethodUtils methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodUtils params(Class<?>... methodParams) {
        this.methodParams = methodParams;
        this.paramCount = methodParams.length;
        return this;
    }

    public MethodUtils paramCount(int paramCount) {
        this.paramCount = paramCount;
        return this;
    }

    @Override
    public BaseFinder<Method> find() {
        List<Method> cache = findMethodCache();
        if (cache != null && !cache.isEmpty()) {
            result = cache;
            return this;
        }
        Method[] methods = getDeclaringClass().getDeclaredMethods();
        result.addAll(Arrays.asList(methods));
        result.removeIf(method -> methodName != null && !method.getName().equals(methodName));
        result.removeIf(method -> returnType != null && !CheckClassType.checkType(method.getReturnType(), returnType));
        result.removeIf(method -> paramCount != null && method.getParameterCount() != paramCount);
        result.removeIf(method -> methodParams != null && !paramEquals(method.getParameterTypes()));
        writeToMethodCache(result);
        return this;
    }

    private boolean paramEquals(Class<?>[] methodParams) {
        for (int i = 0; i < methodParams.length; i++) {
            Class<?> type = methodParams[i];
            Class<?> findType = this.methodParams[i];
            if (findType == Ignore.class || CheckClassType.checkType(type, findType)) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public String buildSign() {
        StringBuilder build = new StringBuilder();
        build.append("method:")
                .append(fromClassName)
                .append(" ")
                .append(returnType)
                .append(" ")
                .append(methodName)
                .append("(")
                .append(paramCount)
                .append(Arrays.toString(methodParams))
                .append(")");
        return build.toString();
    }

    private <T> T tryCall(Method method, Object object, Object... args) {
        try {
            Log.d("MethodTool", "tryCall: " + method + " obj=" + object + " args " + Arrays.toString(args));
            return (T) method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T callFirstStatic(Object... args) {
        Method method = first();
        return tryCall(method, null, args);
    }

    public <T> T callFirst(Object runTimeObj, Object... args) {
        Method method = first();
        return tryCall(method, runTimeObj, args);
    }

    public <T> T callLast(Object object, Object... args) {
        Method method = last();
        return tryCall(method, object, args);
    }
}
