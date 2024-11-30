package top.sacz.timtool.hook.qqapi;

import top.sacz.timtool.hook.item.api.QQContactUpdateListener;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.ConstructorUtils;
import top.sacz.xphelper.reflect.FieldUtils;

/**
 * QQ联系人会话相关工具
 */
public class ContactUtils {

    public static Object getCurrentContact() {
        Object currentAIOContact = QQContactUpdateListener.getCurrentAIOContact();
        Object contact = getContactByAIOContact(currentAIOContact);
        return contact;
    }

    public static Object getContactByAIOContact(Object aioContact) {
        String peerUid = FieldUtils.create(aioContact)
                .fieldName("e")
                .fieldType(String.class)
                .firstValue(aioContact);
        int chatType = FieldUtils.create(aioContact)
                .fieldName("d")
                .fieldType(int.class)
                .firstValue(aioContact);
        String guild = FieldUtils.create(aioContact)
                .fieldName("f")
                .fieldType(String.class)
                .firstValue(aioContact);
        String nick = FieldUtils.create(aioContact)
                .fieldName("g")
                .fieldType(String.class)
                .firstValue(aioContact);
        return getContact(chatType, peerUid, guild);
    }


    /**
     * 获取好友聊天对象
     */
    public static Object getFriendContact(String uin) {
        return getContact(1, uin);
    }

    /**
     * 获取群聊聊天对象
     */
    public static Object getGroupContact(String troopUin) {
        return getContact(2, troopUin);
    }

    /**
     * 获取聊天对象
     *
     * @param type 联系人类型 2是群聊 1是好友
     * @param uin  正常的QQ号/群号
     */
    public static Object getContact(int type, String uin) {
        return getContact(type, uin, "");
    }

    /**
     * 获取聊天对象 包含可能是频道的情况
     *
     * @param type    type为4时创建频道聊天对象
     * @param guildId 频道id
     */
    public static Object getContact(int type, String uin, String guildId) {
        Class<?> contactClass = ClassUtils.findClass("com.tencent.qqnt.kernelpublic.nativeinterface.Contact");
        try {
            String peerUid = (type != 2 && type != 4 && isNumeric(uin)) ? QQEnvTool.getUidFromUin(uin) : uin;
            return ConstructorUtils.newInstance(contactClass, new Class[]{int.class, String.class, String.class}, type, peerUid, guildId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) return false;
        }
        return true;
    }


}
