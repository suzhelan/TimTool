package top.sacz.xphelper.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加密解密类
 **/
public class AESHelper {

    /**
     * 加密算法/模式/填充模式
     */
    private static final String CipherMode = "AES/ECB/PKCS5Padding";

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * 加密字节数据
     **/
    public static byte[] encrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密字符串
     *
     * @param content  内容
     * @param password 密钥
     * @return 密文
     */
    public static String encrypt(String content, String password) {
        byte[] data = null;
        try {
            data = content.getBytes(UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, password);
        String result = null;
        if (data != null) {
            result = byte2hex(data);
        }
        return result;
    }


    /**
     * 解密字节数据
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密字符串
     */
    public static String decrypt(String content, String password) {
        byte[] data = null;
        try {
            data = hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password);
        if (data == null) return null;
        String result;
        result = new String(data, UTF_8);
        return result;
    }


    private static SecretKeySpec createKey(String password) {
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(32);
        sb.append(password);
        //不足32位补满32位
        while (sb.length() < 32) {
            sb.append("0");
        }
        //超过32位删除32位之后的
        if (sb.length() > 32) {
            sb.setLength(32);
        }
        byte[] data = sb.toString().getBytes(UTF_8);
        return new SecretKeySpec(data, "AES");
    }

    private static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuilder sb = new StringBuilder(b.length * 2);
        String tmp;
        for (byte value : b) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(value & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }


    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }
}