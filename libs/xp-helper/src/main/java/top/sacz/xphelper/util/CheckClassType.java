package top.sacz.xphelper.util;

import java.util.HashMap;
import java.util.Map;

public class CheckClassType {

    /*    private final static Class<?>[][] typeArray = {
                {boolean.class, Boolean.class},
                {int.class, Integer.class},
                {long.class, Long.class},
                {byte.class, Byte.class},
                {short.class, Short.class},
                {float.class, Float.class},
                {double.class, Double.class},
                {char.class, Character.class},
                {boolean[].class, Boolean[].class},
                {int[].class, Integer[].class},
                {long[].class, Long[].class},
        };*/
    private final static Map<Class<?>, Class<?>> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(boolean.class, Boolean.class);
        TYPE_MAP.put(int.class, Integer.class);
        TYPE_MAP.put(long.class, Long.class);
        TYPE_MAP.put(byte.class, Byte.class);
        TYPE_MAP.put(short.class, Short.class);
        TYPE_MAP.put(float.class, Float.class);
        TYPE_MAP.put(double.class, Double.class);
        TYPE_MAP.put(char.class, Character.class);

        TYPE_MAP.put(boolean[].class, Boolean[].class);
        TYPE_MAP.put(int[].class, Integer[].class);
        TYPE_MAP.put(long[].class, Long[].class);
        TYPE_MAP.put(char[].class, Character[].class);
        TYPE_MAP.put(short[].class, Short[].class);
        TYPE_MAP.put(byte[].class, Byte[].class);
        TYPE_MAP.put(float[].class, Float[].class);
        TYPE_MAP.put(double[].class, Double[].class);
    }

    //缩小范围匹配字节引用类型
    public static boolean checkType(Class<?> methodParamClz, Class<?> convert) {
        if (methodParamClz.equals(convert)) return true;
        if (methodParamClz.equals(hasWarpClass(convert))) return true;
        if (methodParamClz.equals(hasBaseClass(convert))) return true;
        return methodParamClz.isAssignableFrom(convert);
    }

    private static Class<?> hasWarpClass(Class<?> targetClz) {
        if (TYPE_MAP.containsKey(targetClz)) {
            return TYPE_MAP.get(targetClz);
        }
        return null;
    }

    private static Class<?> hasBaseClass(Class<?> target) {
        if (TYPE_MAP.containsValue(target)) {
            //获取指定key
            for (Map.Entry<Class<?>, Class<?>> entry : TYPE_MAP.entrySet()) {
                if (entry.getValue().equals(target)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private static Class<?> hasWarpType(Class<?> baseType) {
        if (baseType.equals(boolean.class)) return Boolean.class;
        if (baseType.equals(int.class)) return Integer.class;
        if (baseType.equals(long.class)) return Long.class;
        if (baseType.equals(byte.class)) return Byte.class;
        if (baseType.equals(short.class)) return Short.class;
        if (baseType.equals(float.class)) return Float.class;
        if (baseType.equals(double.class)) return Double.class;
        if (baseType.equals(char.class)) return Character.class;
        return null;
    }

    private static Class<?> hasType(Class<?> clz) {
        if (clz.equals(Boolean.class)) return boolean.class;
        if (clz.equals(Integer.class)) return int.class;
        if (clz.equals(Long.class)) return long.class;
        if (clz.equals(Byte.class)) return byte.class;
        if (clz.equals(Short.class)) return short.class;
        if (clz.equals(Float.class)) return float.class;
        if (clz.equals(Double.class)) return double.class;
        if (clz.equals(Character.class)) return char.class;
        return null;
    }
}
