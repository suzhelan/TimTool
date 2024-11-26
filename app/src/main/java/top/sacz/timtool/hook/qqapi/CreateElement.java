package top.sacz.timtool.hook.qqapi;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.robv.android.xposed.XposedHelpers;
import top.sacz.timtool.hook.util.PathTool;
import top.sacz.timtool.hook.util.ToastTool;
import top.sacz.timtool.net.DownloadManager;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.ConstructorUtils;
import top.sacz.xphelper.reflect.FieldUtils;


public class CreateElement {


    public static Object createTextElement(String text) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createTextElement", new Class[]{String.class}, text);
    }

    public static Object createEmojiElement(String url) {
        String path = cachePicPath(url);
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createPicElement", new Class[]{String.class, boolean.class, int.class}, path, true, 1);
    }

    public static Object createPicElement(String url) {
        String path = cachePicPath(url);
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createPicElement", new Class[]{String.class, boolean.class, int.class}, path, true, 0);
    }

    public static Object createAtTextElement(String text, String peerUid, int atType) {//0不艾特1全体2个人
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createAtTextElement", new Class[]{String.class, String.class, int.class}, text, peerUid, atType);
    }

    /**
     * 创建艾特元素 并自动获取对方在群内名称
     */
    public static Object createAtTextElement(String groupUin, String peerUid) {
        int atType = 2;
        String atText = "@";
        if (TextUtils.isEmpty(peerUid) || peerUid.equals("0")) {
            atText += "全体成员";
            atType = 1;
        } else {
            atText += QQTroopTool.getMemberName(groupUin, peerUid);
        }
        return createAtTextElement(atText, peerUid, atType);
    }

    public static Object createReplyElement(long msgId) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createReplyElement", new Class[]{long.class}, msgId);
    }

    public static Object createFileElement(String path) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createFileElement", new Class[]{String.class}, path);
    }


    public static Object createVideoElement(String path) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createVideoElement", new Class[]{String.class}, path);
    }

    public static Object createJsonGrayTipElement(String text, String url) {
        JSONObject jsonObject = new JSONObject();
        boolean empty = !(url.contains("http://") || url.contains("https://"));
        try {
            jsonObject.put("align", "center");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("col", 3);
            jsonObject1.put("jp", url);
            jsonObject1.put("txt", text);
            jsonObject1.put("type", empty ? "nor" : "url");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject1);
            jsonObject.put("items", jsonArray);
            Object jsonGrayElement = ConstructorUtils.newInstance(ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.JsonGrayElement"), new Class[]{long.class, String.class, String.class, boolean.class, ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.XmlToJsonParam")}, empty ? 1014 : 1015, jsonObject.toString(), "", false, null);

            Object grayTipElement = ConstructorUtils.newInstance(ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.GrayTipElement"));

            FieldUtils.create(grayTipElement)
                    .fieldName("jsonGrayTipElement")
                    .setFirst(grayTipElement, 17);

            FieldUtils.create(grayTipElement)
                    .fieldName("jsonGrayTipElement")
                    .setLast(grayTipElement, jsonGrayElement);

            Object msgElement = ConstructorUtils.newInstance(ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.MsgElement"));
            XposedHelpers.callMethod(msgElement, "setElementType", new Class[]{int.class}, 8);
            XposedHelpers.callMethod(msgElement, "setGrayTipElement", new Class[]{ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.GrayTipElement")}, grayTipElement);
            return msgElement;
        } catch (Exception e) {
            Log.d("报错:createJsonGrayTipElement", String.valueOf(e));
            return null;
        }
    }

    public static Object createArkElement(String card) {
        try {
            Class<?> card_data = ClassUtils.findClass("com.tencent.qqnt.msg.a.b");
            Object card_data_object = card_data.newInstance();
            boolean o1 = (boolean) XposedHelpers.callMethod(card_data_object, "o", new Class[]{String.class}, card);
            if (!o1) {
                ToastTool.show("卡片格式有问题:" + card);
                return null;
            }
            Object o = QQEnvTool.getQRouteApi(ClassUtils.findClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
            return XposedHelpers.callMethod(o, "createArkElement", new Class[]{card_data}, card_data_object);
        } catch (IllegalAccessException | InstantiationException e) {
            //throw new RuntimeException(e);
            return null;
        }
    }


    public static String cachePicPath(String Path) {
        String mPath = Path.toLowerCase();
        if (mPath.startsWith("http:") || mPath.startsWith("https:")) {
            String mRandomPathName = (String.valueOf(Math.random())).substring(2);
            String mRandomPath = PathTool.getModuleCachePath("img") + "/";
            DownloadManager.download(Path, mRandomPath + mRandomPathName);
            return mRandomPath + mRandomPathName;
        } else {
            return Path;
        }
    }

    /**
     * 获取 视频 或 音频 时长
     *
     * @param path 视频 或 音频 文件路径
     * @return 时长 毫秒值
     */
    public static long getDuration(String path) {
        long duration = 0;
        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
            if (path != null) {
                mmr.setDataSource(path);
            }
            String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (time != null) {
                duration = Long.parseLong(time);
            }
        } catch (Exception ignored) {
        }
        return duration;
    }

}
