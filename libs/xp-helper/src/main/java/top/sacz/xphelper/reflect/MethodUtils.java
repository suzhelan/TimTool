package top.sacz.xphelper.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import top.sacz.xphelper.base.BaseFinder;

public class MethodUtils extends BaseFinder<Method> {

    private String methodName;
    private Class<?> returnType;
    private Class<?>[] methodParams;
    private Integer paramCount;


    public static MethodUtils create(Object target) {
        return create(target.getClass());
    }

    public static MethodUtils create(Class<?> fromClass) {
        MethodUtils methodUtils = new MethodUtils();
        methodUtils.declaringClass = fromClass;
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
        Method[] methods = declaringClass.getDeclaredMethods();
        result.addAll(Arrays.asList(methods));
        result.removeIf(method -> methodName != null && !method.getName().equals(methodName));
        result.removeIf(method -> returnType != null && !method.getReturnType().equals(returnType));
        result.removeIf(method -> paramCount != null && method.getParameterCount() != paramCount);
        result.removeIf(method -> methodParams != null && !paramEquals(method.getParameterTypes()));
        writeToMethodCache(result);
        findComplete();
        return this;
    }

    private boolean paramEquals(Class<?>[] methodParams) {
        for (int i = 0; i < methodParams.length; i++) {
            Class<?> type = methodParams[i];
            Class<?> findType = this.methodParams[i];
            if (findType == Ignore.class || Objects.equals(type, findType)) {
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
                .append(declaringClass)
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
            return (T) method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T callFirst(Object object, Object... args) {
        Method method = first();
        return tryCall(method, object, args);
    }

    public <T> T callLast(Object object, Object... args) {
        Method method = last();
        return tryCall(method, object, args);
    }
}
