# 欢迎加入此项目的贡献

### 此项目的环境配置比较简单 你只需要先fork到自己的仓库下 然后开始clone自己仓库的源代码 最后开始完成pr到此即可

### 开始最常用的开发

如果你是Kotlin开发者，可以参考如下代码

```kotlin

@HookItem("辅助功能/表情与图片/禁用表情下载限制")//功能所在路径
class DownloadEmotion : BaseSwitchFunctionHookItem() {
    //重写此方法以在功能添加一个灰字提示
    override fun getTip(): String {
        return "新版QQ禁用了表情下载,开启了可以继续下载"
    }

    //必须重写此方法 加载hook时会调用
    override fun loadHook(loader: ClassLoader) {
        //有很多限制方法都走的这个接口
        val isSwitchOn =
            MethodUtils.create("com.tencent.mobileqq.activity.api.impl.UnitedConfigImpl")
                .methodName("isSwitchOn")
                .params(String::class.java, Boolean::class.javaObjectType)
                .returnType(Boolean::class.javaObjectType)
                .first()
        //使用hookBefore Hook此方法 会自动进行开关判断
        hookBefore(isSwitchOn) { param ->
            val cmd = param.args[0] as String
            if (cmd == "emotion_download_disable_8980_887036489") {
                param.result = false
            }
        }
    }

}
```