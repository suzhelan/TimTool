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
"version": 11, //最新的版本号
"versionName": "1.1", //最新的版本昵称
"hasUpdate": true, //是否有更新
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

