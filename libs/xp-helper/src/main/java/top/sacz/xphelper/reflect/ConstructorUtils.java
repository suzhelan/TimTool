package top.sacz.xphelper.reflect;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import top.sacz.xphelper.base.BaseFinder;
import top.sacz.xphelper.util.CheckClassType;

public class ConstructorUtils extends BaseFinder<Constructor<?>> {

    private int paramCount;
    private Class<?>[] paramTypes;

    public static ConstructorUtils create(Object target) {
        return create(target.getClass());
    }

    public static ConstructorUtils create(Class<?> fromClass) {
        ConstructorUtils constructorUtils = new ConstructorUtils();
        constructorUtils.setDeclaringClass(fromClass);
        return constructorUtils;
    }

    public static ConstructorUtils create(String fromClassName) {
        return create(ClassUtils.findClass(fromClassName));
    }

    public static Object newInstance(Class<?> fromClass, Class<?>[] paramTypes, Object... args) {
        return create(fromClass)
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

    public ConstructorUtils paramTypes(Class<?>... paramTypes) {
        this.paramTypes = paramTypes;
        this.paramCount = paramTypes.length;
        return this;
    }

    @Override
    public BaseFinder<Constructor<?>> find() {
        //查找缓存
        List<Constructor<?>> cache = findConstructorCache();
        if (cache != null && !cache.isEmpty()) {
            result = cache;
            return this;
        }
        Constructor<?>[] constructors = getDeclaringClass().getDeclaredConstructors();
        result.addAll(Arrays.asList(constructors));
        result.removeIf(constructor -> paramCount != 0 && constructor.getParameterCount() != paramCount);
        result.removeIf(constructor -> paramTypes != null && !paramEquals(constructor.getParameterTypes()));
        writeToConstructorCache(result);
        return null;
    }

    private boolean paramEquals(Class<?>[] methodParams) {
        for (int i = 0; i < methodParams.length; i++) {
            Class<?> type = methodParams[i];
            Class<?> findType = this.paramTypes[i];
            if (findType == Ignore.class || CheckClassType.checkType(type, findType)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public Object newFirstInstance(Object... args) {
        try {
            Constructor<?> firstConstructor = first();
            Object instance = firstConstructor.newInstance(args);
            return getDeclaringClass().cast(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String buildSign() {
        StringBuilder signBuilder = new StringBuilder()
                .append("constructor:")
                .append(fromClassName)
                .append(" ")
                .append(paramCount)
                .append(" ")
                .append(Arrays.toString(paramTypes));
        return signBuilder.toString();
    }
}
