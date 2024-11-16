package top.sacz.xphelper.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import top.sacz.xphelper.base.BaseFinder;

public class FieldUtils extends BaseFinder<Field> {

    private final boolean isFindSuperClass = false;
    private Class<?> fieldType;
    private String fieldName;


    public static FieldUtils create(Object target) {
        return create(target.getClass());
    }

    public static FieldUtils create(Class<?> fromClass) {
        FieldUtils fieldUtils = new FieldUtils();
        fieldUtils.declaringClass = fromClass;
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
        Field[] declaredFields = declaringClass.getDeclaredFields();
        result.addAll(Arrays.asList(declaredFields));
        //过滤类型
        result.removeIf(field -> fieldType != null && !field.getType().equals(fieldType));
        //过滤名称
        result.removeIf(field -> fieldName != null && !field.getName().equals(fieldName));
        //写入缓存
        writeToFieldCache(result);
        findComplete();
        return this;
    }

    @Override
    public String buildSign() {
        StringBuilder signBuilder = new StringBuilder();
        //构建签名缓存
        signBuilder.append("field:")
                .append(declaringClass)
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

}
