package top.sacz.timtool.hook.util

import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 通过描述符获取类
 */
fun String.toClass(): Class<*> = ClassUtils.findClass(this)

/**
 * 方法拓展
 * 通过方法签名获取方法
 */
fun String.toMethod(): Method = MethodUtils.getMethodByDesc(this)

fun String.toMethod(clsLoader: ClassLoader): Method = MethodUtils.getMethodByDesc(this, clsLoader)

/**
 * 变量拓展
 * 通过变量签名获取变量
 */
fun String.toField(): Field = FieldUtils.getFieldByDesc(this)

fun String.toField(clsLoader: ClassLoader): Field = FieldUtils.getFieldByDesc(this, clsLoader)


/**
 * 对象拓展 通过对象的方法参数调用
 * 不支持静态方法的调用
 */
fun <T> Any.call(
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
 * 对象拓展
 * 获取字段值
 * 传参 字段名 或者 字段类型
 */
fun <T> Any.getFieldValue(name: String? = null, type: Class<*>? = null): T {
    return FieldUtils.create(this)
        .fieldName(name)
        .fieldType(type)
        .firstValue<T>(this)
}

/**
 * 获取字段值 传参字段名
 */
fun <T> Any.getFieldValue(name: String): T {
    return this.getFieldValue(name, null)
}

/**
 * 获取字段值 传参字段类型
 */
fun <T> Any.getFieldValue(type: Class<*>): T {
    return this.getFieldValue(null, type)
}

/**
 * 对象拓展
 * 设置字段值
 * 传参 字段名 或者 字段类型
 */
fun <T> Any.setFieldValue(name: String? = null, type: Class<*>? = null, value: T) {
    FieldUtils.create(this)
        .fieldName(name)
        .fieldType(type)
        .setFirst(this, value)
}

/**
 * 设置字段值 传参字段名
 */
fun <T> Any.setFieldValue(name: String, value: T) {
    this.setFieldValue(name, null, value)
}

/**
 * 设置字段值 传参字段类型
 */
fun <T> Any.setFieldValue(type: Class<*>, value: T) {
    this.setFieldValue(null, type, value)
}

/**
 * 类拓展
 * 获取静态字段值
 * 传参 字段名 或者 字段类型
 */
fun <T> Class<*>.getStaticFieldValue(name: String? = null, type: Class<*>? = null): T {
    return FieldUtils.create(this)
        .fieldName(name)
        .fieldType(type)
        .firstValue<T>(null)
}

/**
 * 类拓展
 * 设置静态字段值
 * 传参 字段名 或者 字段类型
 */
fun <T> Class<*>.setStaticFieldValue(name: String? = null, type: Class<*>? = null, value: T) {
    FieldUtils.create(this)
        .fieldName(name)
        .fieldType(type)
        .setFirst(null, value)
}
