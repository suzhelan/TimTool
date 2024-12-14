package top.sacz.timtool.hook.util

import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils
import java.lang.reflect.Method

/**
 * 方法拓展
 * 通过方法签名获取方法
 *
 */
fun String.toMethod(): Method = MethodUtils.getMethodByDesc(this)

/**
 * 对象拓展 通过对象的方法参数调用
 * 不支持静态方法的调用
 */
fun <T> Any.invoke(
    methodName: String,
    vararg args: Any?
): T {
    val paramTypes = args.map {
        if (it == null) {
            return@map Ignore::class.java
        } else {
            return@map it.javaClass
        }
    }.toTypedArray()
    return MethodUtils.create(this)
        .methodName(methodName)
        .params(*paramTypes)
        .callFirst<T>(this, *args)
}

/**
 * 对象拓展 通过对象的属性名获取
 * 传参 name或者字段类型
 */
fun <T> Any.getFieldValue(name: String? = null, type: Class<out T>? = null): T {
    return FieldUtils.create(this)
        .fieldName(name)
        .fieldType(type)
        .firstValue<T>(this)
}

/**
 * 设置字段值
 * 传参 name或者字段类型
 */
fun <T> Any.setFieldValue(name: String? = null, type: Class<out T>? = null, value: T) {
    FieldUtils.create(this)
        .fieldName(name)
        .fieldType(type)
        .setFirst(this, value)
}

