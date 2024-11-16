package top.sacz.xphelper.reflect;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import top.sacz.xphelper.base.BaseFinder;

public class ConstructorUtils extends BaseFinder<Constructor<?>> {


    private int paramCount;
    private Class<?>[] paramTypes;

    public static ConstructorUtils build(Object target) {
        return build(target.getClass());
    }

    public static ConstructorUtils build(Class<?> fromClass) {
        ConstructorUtils constructorUtils = new ConstructorUtils();
        constructorUtils.declaringClass = fromClass;
        return constructorUtils;
    }

    public static ConstructorUtils build(String fromClassName) {
        return build(ClassUtils.findClass(fromClassName));
    }

    public static Object newInstance(Class<?> fromClass, Class<?>[] paramTypes, Object... args) {
        return build(fromClass)
                .paramTypes(paramTypes)
                .newFirstInstance(args);
    }

    public static Object newInstance(Class<?> fromClass, Object... args) {
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        return newInstance(fromClass, paramTypes, args);
    }

    public ConstructorUtils paramCount(int paramCount) {
        this.paramCount = paramCount;
        return this;
    }

    public ConstructorUtils paramTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
        return this;
    }

    @Override
    public BaseFinder<Constructor<?>> find() {
        List<Constructor<?>> cache = findConstructorCache();
        if (cache != null && !cache.isEmpty()) {
            result = cache;
            return this;
        }
        Constructor<?>[] constructors = declaringClass.getDeclaredConstructors();
        result.addAll(Arrays.asList(constructors));
        result.removeIf(constructor -> paramCount != 0 && constructor.getParameterCount() != paramCount);
        result.removeIf(constructor -> paramTypes != null && !Arrays.equals(constructor.getParameterTypes(), paramTypes));
        writeToConstructorCache(result);
        findComplete();
        return null;
    }

    public Object newFirstInstance(Object... args) {
        try {
            Constructor<?> firstConstructor = first();
            Object instance = firstConstructor.newInstance(args);
            return declaringClass.cast(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String buildSign() {
        StringBuilder signBuilder = new StringBuilder()
                .append("constructor:")
                .append(declaringClass)
                .append(" ")
                .append(paramCount)
                .append(" ")
                .append(Arrays.toString(paramTypes));
        return signBuilder.toString();
    }
}
