# 欢迎加入此项目的贡献

### 此项目的环境配置比较简单(JDK要求17及以上) 你只需要先fork到自己的仓库下 然后开始clone自己仓库的源代码 最后开始完成pr到此即可

此项目包含的QQ API https://github.com/suzhelan/TimTool/blob/master/.github/devhelp/api.md
### 开始最常用的开发

在此项目进行开发 需要保持良好的代码规范 保持低耦合的设计

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

如果需要使用dexkit进行反混淆查找方法 可以参考以下kotlin代码

1. 在功能类实现 IMethodFinder接口与 find() 在查找方法时会调用
2. 在类中调用 buildMethodFinder() 会生成方法查找器 传入查询条件
3. 调用 first() 得到首个结果 (调用find())会得到结果列表  
   IMethodFinder.find() 会在QQ或模块版本变动调用一次并查找数据并缓存 随后在正常情况会使用缓存
   防止过多的性能开销  
   严禁在IMethodFinder.find()外的地方调用buildMethodFinder() 这可能将会导致本次启动内存泄露

```kotlin
@HookItem("辅助功能/聊天/禁止回复自动艾特")
class DisableReplyAutoAt : BaseSwitchFunctionHookItem(), IMethodFinder {

    lateinit var method: Method

    override fun loadHook(loader: ClassLoader) {
        hookBefore(method) { param ->
            param.result = null
        }
    }

    override fun find() {
        method = buildMethodFinder()
            .searchPackages("com.tencent.mobileqq.aio.input.reply")//在指定的包下查找
            .useString("msgItem.msgRecord.senderUid")//查找方法内使用的字符串
            .first()//得到方法结果
    }
}
```

如果你是Java开发者，可以参考如下代码
```java

@HookItem("辅助功能/聊天/消息长按菜单添加复读")
public class MessageMenuAddRereading extends BaseSwitchFunctionHookItem {

   @Override
   public void loadHook(@NonNull ClassLoader classLoader) throws Throwable {
      //qq创建长按菜单的方法
      Method setMenuMethod = MethodUtils.create("com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout")
              .methodName("setMenu")
              .params(Ignore.class, View.class)
              .returnType(void.class)
              .first();
      hookBefore(setMenuMethod, param -> {
         Object itemListWrapper = param.args[0];
         //这个参数第一个为list的字段是长按的菜单列表
         List<Object> itemList = FieldUtils.create(itemListWrapper)
                 .fieldType(List.class)
                 .firstValue(itemListWrapper);
         //获取第一个菜单
         Object item = itemList.get(0);
         //解析出msgRecord
         Object aioMsgItem = FieldUtils.getFirstType(item, ClassUtils.findClass("com.tencent.mobileqq.aio.msg.AIOMsgItem"));
         Object msgRecord = XposedHelpers.callMethod(aioMsgItem, "getMsgRecord");
         //构建长按菜单选项
         Object itemButton = QQCustomMenu.createMenuItem(aioMsgItem, R.drawable.repeat, "复读",
                 () -> {
                    RereadingMessageClickListener listener = new RereadingMessageClickListener(msgRecord, ContactUtils.getCurrentContact());
                    listener.rereading();
                    return null;
                 });
         //添加选项到菜单
         itemList.add(0, itemButton);
      });
   }
}
```

