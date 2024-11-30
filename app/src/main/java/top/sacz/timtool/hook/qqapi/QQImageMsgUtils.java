package top.sacz.timtool.hook.qqapi;

import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.hook.item.api.OnQQRKeyApi;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.reflect.MethodUtils;

public class QQImageMsgUtils {

    public static String getPicElementUrl(int chatType, Object picElement) {
        String url;
        String originImageUrl = FieldUtils.getField(picElement, "originImageUrl", String.class);
        String fileUuid = FieldUtils.getField(picElement, "fileUuid", String.class);
        String md5 = FieldUtils.getField(picElement, "md5HexStr", String.class);
        String baseUrl = "https://gchat.qpic.cn";
        if (originImageUrl != null && !originImageUrl.isEmpty()) {
            if (originImageUrl.startsWith("/download")) {
                String rkey = OnQQRKeyApi.getRkeyPrivate();
                if (originImageUrl.contains("appid=1406")) {
                    rkey = OnQQRKeyApi.getRkeyGroup();
                }
                url = baseUrl + originImageUrl + rkey;
            } else {
                url = baseUrl + originImageUrl;
            }
        } else if (fileUuid.length() >= 64) {
            String appid = "";
            if (chatType == 1) {
                appid = "1406";
            } else if (chatType == 2) {
                appid = "1407";
            }
            String rkey = OnQQRKeyApi.getRkeyPrivate();
            if (appid.equals("1406")) {
                rkey = OnQQRKeyApi.getRkeyGroup();
            }
            url = "https://gchat.qpic.cn/download?appid=" + appid + "&fileid=" + fileUuid + "&spec=0" + rkey;
        } else {
            url = "https://gchat.qpic.cn/gchatpic_new/0/0-0-" + md5.toUpperCase() + "/0?term=2&is_origin=1";
        }
        return url;
    }

    public static String getMsgRecordPicUrl(Object msgRecord) {
        ArrayList<Object> elements = FieldUtils.getField(msgRecord, "elements", ArrayList.class);
        int chatType = FieldUtils.getField(msgRecord, "chatType", int.class);
        String url = "";
        for (Object msgElement : elements) {
            Object picElement = MethodUtils.create(msgElement.getClass())
                    .methodName("getPicElement")
                    .returnType(ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.PicElement"))
                    .callFirst(msgElement);
            if (picElement == null) {
                continue;
            }
            url = getPicElementUrl(chatType, picElement);
        }
        return url;
    }

    public static List<String> getMsgRecordPicUrlList(Object msgRecord) {
        ArrayList<Object> elements = FieldUtils.getField(msgRecord, "elements", ArrayList.class);
        int chatType = FieldUtils.getField(msgRecord, "chatType", int.class);
        List<String> urlList = new ArrayList<>();
        for (Object msgElement : elements) {
            Object picElement = MethodUtils.create(msgElement.getClass())
                    .methodName("getPicElement")
                    .returnType(ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.PicElement"))
                    .callFirst(msgElement);
            if (picElement == null) {
                continue;
            }
            urlList.add(getPicElementUrl(chatType, picElement));
        }
        return urlList;
    }
}
