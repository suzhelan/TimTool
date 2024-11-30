package top.sacz.timtool.hook.item.api;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import top.sacz.timtool.hook.base.ApiHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.timtool.hook.util.LogUtils;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.reflect.MethodUtils;


@HookItem("API-监听RKey刷新")
public class OnQQRKeyApi extends ApiHookItem {

    private static String rkey_group;
    private static String rkey_private;

    public static String getRkeyGroup() {
        return rkey_group;
    }

    public static String getRkeyPrivate() {
        return rkey_private;
    }

    private static byte[] getUnpPackage(byte[] b) {
        if (b == null) {
            return null;
        }
        if (b.length < 4) {
            return b;
        }
        if (b[0] == 0) {
            return Arrays.copyOfRange(b, 4, b.length);
        } else {
            return b;
        }
    }

    @Override
    public void loadHook(ClassLoader classLoader) throws Throwable {
        Method method = MethodUtils.create("mqq.app.msghandle.MsgRespHandler")
                .methodName("dispatchRespMsg")
                .params(
                        ClassUtils.findClass("mqq.app.MobileQQ"),
                        ClassUtils.findClass("com.tencent.mobileqq.msf.sdk.MsfMessagePair"),
                        ClassUtils.findClass("com.tencent.mobileqq.msf.sdk.MsfRespHandleUtil"),
                        ClassUtils.findClass("com.tencent.mobileqq.msf.sdk.MsfServiceSdk")
                ).returnType(void.class)
                .first();
        hookBefore(method, param -> {
            Object fromServiceMsg = FieldUtils.create(
                            param.args[1])
                    .fieldName("fromServiceMsg")
                    .fieldType(ClassUtils.findClass("com.tencent.qphone.base.remote.FromServiceMsg"))
                    .firstValue(param.args[1]);
            String serviceCmd = FieldUtils.create(fromServiceMsg)
                    .fieldName("serviceCmd")
                    .fieldType(String.class)
                    .firstValue(fromServiceMsg);
            if ("OidbSvcTrpcTcp.0x9067_202".equals(serviceCmd)) {
                FunProtoData data = new FunProtoData();
                byte[] buffer = FieldUtils.create(fromServiceMsg)
                        .fieldName("wupBuffer")
                        .fieldType(byte[].class)
                        .firstValue(fromServiceMsg);
                data.fromBytes(getUnpPackage(buffer));

                JSONObject obj = data.toJSON();
                rkey_group = obj.getJSONObject("4")
                        .getJSONObject("4")
                        .getJSONArray("1")
                        .getJSONObject(0).getString("1");
                rkey_private = obj.getJSONObject("4")
                        .getJSONObject("4")
                        .getJSONArray("1")
                        .getJSONObject(1).getString("1");
            }
        });
    }

    private class FunProtoData {
        private final HashMap<Integer, List<Object>> values = new HashMap<>();

        public void fromJSON(JSONObject json) {
            try {
                Iterator<String> key_it = json.keys();
                while (key_it.hasNext()) {
                    String key = key_it.next();
                    int k = Integer.parseInt(key);
                    Object value = json.get(key);
                    if (value instanceof JSONObject) {
                        FunProtoData newProto = new FunProtoData();
                        newProto.fromJSON((JSONObject) value);
                        putValue(k, newProto);
                    } else if (value instanceof JSONArray arr) {
                        for (int i = 0; i < arr.length(); i++) {
                            Object arr_obj = arr.get(i);
                            if (arr_obj instanceof JSONObject) {
                                FunProtoData newProto = new FunProtoData();
                                newProto.fromJSON((JSONObject) arr_obj);
                                putValue(k, newProto);
                            } else {
                                putValue(k, arr_obj);
                            }
                        }
                    } else {
                        putValue(k, value);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        private void putValue(int key, Object value) {
            List<Object> list = values.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(value);
        }

        public void fromBytes(byte[] b) throws IOException {
            CodedInputStream in = CodedInputStream.newInstance(b);
            while (in.getBytesUntilLimit() > 0) {
                int tag = in.readTag();
                int fieldNumber = tag >>> 3;
                int wireType = tag & 7;
                if (wireType == 4 || wireType == 3 || wireType > 5)
                    throw new IOException("Unexpected wireType: " + wireType);
                switch (wireType) {
                    case 0:
                        putValue(fieldNumber, in.readInt64());
                        break;
                    case 1:
                        putValue(fieldNumber, in.readRawVarint64());
                        break;
                    case 2:
                        byte[] subBytes = in.readByteArray();
                        try {
                            FunProtoData sub_data = new FunProtoData();
                            sub_data.fromBytes(subBytes);
                            putValue(fieldNumber, sub_data);
                        } catch (Exception e) {
                            putValue(fieldNumber, new String(subBytes));
                        }
                        break;
                    case 5:
                        putValue(fieldNumber, in.readFixed32());
                        break;
                    default:
                        putValue(fieldNumber, "Unknown wireType: " + wireType);
                        break;
                }
            }
        }

        public JSONObject toJSON() throws Exception {
            JSONObject obj = new JSONObject();
            for (Integer k_index : values.keySet()) {
                List<?> list = values.get(k_index);
                assert list != null;
                if (list.size() > 1) {
                    JSONArray arr = new JSONArray();
                    for (Object o : list) {
                        arr.put(valueToText(o));
                    }
                    obj.put(String.valueOf(k_index), arr);
                } else {
                    for (Object o : list) {
                        obj.put(String.valueOf(k_index), valueToText(o));
                    }
                }
            }
            return obj;
        }

        private Object valueToText(Object value) throws Exception {
            if (value instanceof FunProtoData data) {
                return data.toJSON();
            } else {
                return value;
            }
        }

        public byte[] toBytes() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            CodedOutputStream out = CodedOutputStream.newInstance(bos);
            try {
                for (Integer k_index : values.keySet()) {
                    List<?> list = values.get(k_index);
                    for (Object o : list) {
                        if (o instanceof Long) {
                            long l = (long) o;
                            out.writeInt64(k_index, l);
                        } else if (o instanceof String s) {
                            out.writeByteArray(k_index, s.getBytes());
                        } else if (o instanceof FunProtoData data) {
                            byte[] subBytes = data.toBytes();
                            out.writeByteArray(k_index, subBytes);
                        } else if (o instanceof Integer) {
                            int i = (int) o;
                            out.writeInt32(k_index, i);
                        } else {
                            LogUtils.addRunLog("FunProtoData.toBytes " + "Unknown type: " + o.getClass().getName());
                        }
                    }
                }
                out.flush();
                return bos.toByteArray();
            } catch (Exception e) {
                LogUtils.addError("FunProtoData", "toBytes", e);
                return new byte[0];
            }
        }
    }
}
