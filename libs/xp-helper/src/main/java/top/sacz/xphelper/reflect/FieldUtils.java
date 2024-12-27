package top.sacz.xphelper.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import top.sacz.xphelper.base.BaseFinder;
import top.sacz.xphelper.util.CheckClassType;
import top.sacz.xphelper.util.DexFieldDescriptor;

public class FieldUtils extends BaseFinder<Field> {

    private Class<?> fieldType;
    private String fieldName;


    public static Field getFieldByDesc(String desc) throws NoSuchMethodException {
        Field field = new DexFieldDescriptor(desc).getFieldInstance(ClassUtils.getClassLoader());
        field.setAccessible(true);
        return field;
    }

    public static Field getFieldByDesc(String desc, ClassLoader classLoader) throws NoSuchMethodException {
        Field field = new DexFieldDescriptor(desc).getFieldInstance(classLoader);
        field.setAccessible(true);
        return field;
    }
    /**
     * 获取field值
     *
     * @param targetObj 运行时对象
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     */
    public static <T> T getField(Object targetObj, String fieldName, Class<?> fieldType) {
        return create(targetObj.getClass())
                .fieldName(fieldName)
                .fieldType(fieldType)
                .firstValue(targetObj);
    }

    /**
     * 设置字段值
     */
    public static void setField(Object target, String fieldName, Object value) {
        FieldUtils.create(target)
                .fieldName(fieldName)
                .fieldType(value.getClass())
                .setFirst(target, value);
    }

    /**
     * 根据字段类型获取首个字段值
     */
    public static <T> T getFirstType(Object runtimeObj, Class<?> fieldType) {
        return FieldUtils.create(runtimeObj.getClass())
                .fieldType(fieldType)
                .firstValue(runtimeObj);
    }

    /**
     * 设置首个为此类型的字段值
     */
    public static void setFirstType(Object target, Object value) {
        FieldUtils.create(target)
                .fieldName(value.getClass().getName())
                .setFirst(target, value);
    }

    public static FieldUtils create(Object target) {
        return create(target.getClass());
    }

    public static FieldUtils create(Class<?> fromClass) {
        FieldUtils fieldUtils = new FieldUtils();
        fieldUtils.setDeclaringClass(fromClass);
        return fieldUtils;
    }

    public static FieldUtils create(String formClassName) {
        return create(ClassUtils.findClass(formClassName));
    }

    public FieldUtils fieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public FieldUtils fieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    @Override
    public FieldUtils find() {
        //查找缓存
        List<Field> cache = findFiledCache();
        //缓存不为空直接返回
        if (cache != null && !cache.isEmpty()) {
            result = cache;
            return this;
        }
        //查找类的所有字段
        Field[] declaredFields = getDeclaringClass().getDeclaredFields();
        result.addAll(Arrays.asList(declaredFields));
        //过滤类型
        result.removeIf(field -> fieldType != null && !CheckClassType.checkType(field.getType(), fieldType));
        //过滤名称
        result.removeIf(field -> fieldName != null && !field.getName().equals(fieldName));
        //写入缓存
        writeToFieldCache(result);
        return this;
    }

    @Override
    public String buildSign() {
        StringBuilder signBuilder = new StringBuilder();
        //构建签名缓存
        signBuilder.append("field:")
                .append(fromClassName)
                .append(" ")
                .append(fieldType)
                .append(" ")
                .append(fieldName);
        return signBuilder.toString();
    }

    private <T> T tryGetFieldValue(Field field, Object object) {
        try {
            return (T) field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T firstValue(Object object) {
        Field field = first();
        return tryGetFieldValue(field, object);
    }

    public <T> T lastValue(Object object) {
        Field field = last();
        return tryGetFieldValue(field, object);
    }

    public FieldUtils setFirst(Object target, Object value) {
        Field field = first();
        try {
            field.set(target, value);
            return this;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FieldUtils setLast(Object target, Object value) {
        Field field = last();
        try {
            field.set(target, value);
            return this;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
