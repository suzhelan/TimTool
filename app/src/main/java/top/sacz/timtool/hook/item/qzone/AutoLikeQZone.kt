package top.sacz.timtool.hook.item.qzone

import android.view.View
import com.alibaba.fastjson2.TypeReference
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.OutputHookStack
import top.sacz.timtool.hook.util.callMethod
import top.sacz.timtool.hook.util.getFieldValue
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.MethodUtils
import top.sacz.xphelper.util.ActivityTools
import top.sacz.xphelper.util.KvHelper
import kotlin.concurrent.thread


@HookItem("辅助功能/QQ空间/在空间时自动点赞QQ空间")
class AutoLikeQZone : BaseSwitchFunctionHookItem() {

    private val noLikeConfig by lazy { KvHelper("AutoLikeQZone") }

    override fun getTip(): String {
        return "所见即点赞"
    }

    override fun loadHook(classLoader: ClassLoader) {
        hookBefore(MethodUtils.create("com.qzone.reborn.feedx.presenter.bs").methodName("C").params(View::class.java).first()) { param ->
            val thisObject = param.thisObject
            postLikeTask(thisObject)
        }
    }

    private fun postLikeTask(obj: Any) {
        //发布点赞任务
        thread {
            //最多执行五次 大多数情况点赞成功会直接终止
            repeat(10) {
                Thread.sleep(500)
                val feedData = obj.getFieldValue<Any>(ClassUtils.findClass("com.qzone.proxy.feedcomponent.model.BusinessFeedData"))
                OutputHookStack.OutputObjectField(feedData)
                val likeInfo = feedData.callMethod<Any>("getLikeInfo")
                val isLiked = likeInfo.getFieldValue<Boolean>("isLiked", Boolean::class.java)
                //存储已经处理过的标识
                val integerUniTimeLbsKey = feedData.getFieldValue<Int>("integerUniTimeLbsKey")
                //获取Set
                var noLikeSet = noLikeConfig.getObject("noLikeSet", object : TypeReference<MutableSet<Int>>() {})
                if (noLikeSet == null) {
                    noLikeSet = mutableSetOf()
                }
                //已经点赞则不处理
                if (isLiked) {
                    noLikeSet.add(integerUniTimeLbsKey)
                    noLikeConfig.put("noLikeSet", noLikeSet)
                    return@thread
                }
                //判断Set包含则不处理 防止人为取消赞 模块又帮他点上
                if (noLikeSet.contains(integerUniTimeLbsKey)) {
                    return@thread
                }
                ActivityTools.runOnUiThread {
                    obj.callMethod<Any>("N")
                }
            }
        }
    }
}
