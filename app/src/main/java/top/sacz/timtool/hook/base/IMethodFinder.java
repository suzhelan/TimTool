package top.sacz.timtool.hook.base;

import top.sacz.xphelper.dexkit.MethodFinder;

/**
 * 如果方法被混淆 可以继承此接口 在接口内实现反混淆查找的逻辑
 */
public interface IMethodFinder {

    void find();

    default MethodFinder.MethodBridge buildMethodFinder() {
        return MethodFinder.build();
    }

}
