package top.sacz.timtool.hook.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//声明某个类是hook项目 等待被扫描自动编译添加
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface HookItem {
    String value();

    boolean hasUI() default true;
}
