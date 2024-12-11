<div align="center">
<h1>Tim小助手</h1>

<a href="https://github.com/suzhelan/TimTool/releases"><img alt="GitHub all releases" src="https://img.shields.io/github/downloads/suzhelan/TimTool/total?label=Downloads"></a>
<a href="https://github.com/suzhelan/TimTool/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/suzhelan/TimTool"></a>
<a href="https://github.com/suzhelan/TimTool/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/suzhelan/TimTool"></a>
<a href="https://t.me/timtool"><img alt="Telegram Channel" src="https://img.shields.io/badge/Telegram-频道-blue.svg?logo=telegram"></a>

<p>对Tim,QQ进行的扩展优化的Xposed模块</p>
<p>永久免费模块，请勿上当受骗</p>
</div>

---
### 各渠道更新频率  
* **更新频率**
    - 随缘更新
    - [Telegram](https://t.me/timtool) 在构建测试结束后会更新
    - [Releases](https://github.com/suzhelan/TimTool/releases) 每5个版本更新一次
    - [LSPosedRepo](https://github.com/Xposed-Modules-Repo/top.sacz.timtool) 每10个版本更新一次
    - 更新API (查看底部)

## 适配的版本范围 TimNT 4.x.x(目前维护到4.0.97 最新版)

![Module-Img](https://github.com/suzhelan/TimTool/blob/master/github/img/v1.5.jpg)

### 该插件仅供学习与练习

请勿使用此插件用于违法 商业行为  
插件只是提高日常方便性的工具 请勿过度依赖该插件  
此项目会因为各种可抗不可抗因素随时停止维护  
如果发生了更糟糕的情况我们会及时 **删除代码库** 以及 **发行渠道的所有版本**  
在此之前你或许可以及时保存我们的工作成品
---
### 更新API
 - 检测更新
> POST表单格式 https://timtool.sacz.top/update/hasUpdate  
> version=模块版本号(整数)   
> 返回值
```json {
    "code": 200,
    "msg": "success",
    "action": 0,
    "data": {
        "version": 11,//最新的版本号
        "versionName": "1.1",//最新的版本昵称
        "hasUpdate": true,//是否有更新
        "isForceUpdate": false//是强制更新
    }
}
```
 - 拉取更新信息  
> POST表单格式 https://timtool.sacz.top/update/getUpdateLog  
> version=11  
> 返回值  
```json
{
    "code": 200,
    "msg": "success",
    "action": 0,
    "data": [
        {
            "id": 2,
            "versionCode": 11,
            "versionName": "1.1",
            "updateLog": "# 新增 文件上传重命名*默认开启  (自动重命名base为包名或应用名 自动将.apk重命名成.APK 防止被QQ自动重命名成.apk.1 \n私聊暂未适配,等待后续适配)\n\n# 修复 复读开启后QQ原本复读图标仍然会出现",
            "fileName": "Tim小助手_1.1.apk",
            "time": "2024-12-07T10:44:48.000+00:00",
            "forceUpdate": false
        }
    ]
}
```
 - 下载指定版本的APK
> GET https://timtool.sacz.top/update/download?version=11


[LSPosed-Repo-Github](https://github.com/Xposed-Modules-Repo/top.sacz.timtool)
