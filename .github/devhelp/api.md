## 在此项目中的QQAPI 你可以在项目中快速复用

### QQ环境相关 QQEnvTool
```java
//获取当前登录QQ号
String myUin = QQEnvTool.getCurrentUin();

//获取QQ的指定QRoute实例
Object iTroopMemberListRepoApi = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.troopmemberlist.ITroopMemberListRepoApi"));

//uin转peerUid
QQEnvTool.getUidFromUin(String uin)
//peerUid转uin
QQEnvTool.getUinFromUid(String uid)
```

---


### 获取当前聊天对象 Contact
 * 获取好友/群组聊天对象 包含可能是频道的情况
 * @param type 联系人类型 2是群聊 1是好友 为4时创建频道聊天对象
 * @param uin  正常的QQ号/群号
 * @param guildId 频道id
```java
//获取当前聊天对象
Object contact = ContactUtils.getCurrentContact();

//获取好友聊天对象
Object friendContact = ContactUtils.getFriendContact(String uin);

//获取群聊聊天对象
Object groupContact = ContactUtils.getGroupContact(String groupUin);

//使用chatType获取聊天对象
Object targetContact = ContactUtils.getContact(int type, String uin)

//包含频道情况的获取聊天对象
Object targetContact = ContactUtils.getContact(int type, String uin, String guildId)
```
--- 

### 监听消息View更新
1.调用QQMessageViewListener.addMessageViewUpdateListener(BaseSwitchFunctionHookItem,QQMessageViewListener.OnChatViewUpdateListener) 参数一为当前功能类,参数二为实现的监听器方法
2.实现QQMessageViewListener.OnChatViewUpdateListener接口 完成onViewUpdateAfter重写 参数一为MsgView 参数二懂的都懂
```java
        QQMessageViewListener.addMessageViewUpdateListener(this, new QQMessageViewListener.OnChatViewUpdateListener() {
            @Override
            public void onViewUpdateAfter(@NonNull View msgItemView, @NonNull Object msgRecord) {
                
            }
        });
```
