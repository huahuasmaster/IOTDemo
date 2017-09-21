package administrator.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import administrator.application.ContextApplication;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;

/**
 * Created by zhuang_ge on 2017/8/25.
 * 处理设备sn号与数据编码code的映射
 */

public class DeviceCodeUtil {

    private static SharedPreferences scSp = ContextApplication
            .getContext()
            .getSharedPreferences("sn_code_map",
                    Context.MODE_PRIVATE);

    //当查找不到结果时，默认返回的字符串
    public static final String DEFAULT_SN = "default";

    public static String getCode(String sn) {
        String code = scSp.getString(sn, DEFAULT_SN);
        if (code.equals(DEFAULT_SN)) {
            //如果在本地找不到对应的数据，在线查询
            getMapOnline();
            code = scSp.getString(sn, DEFAULT_SN);
        }
        return code;
    }

    //提供解析好的code
    public static String[] getSplitedCode(String sn) {
        String code = getCode(sn);
        if (!code.equals(DEFAULT_SN)) {
            String[] spCode = code.split("#");
            return spCode;
        }
        return null;
    }

    /**
     * 从网络中获取device-sn map
     */
    public static void getMapOnline() {
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Map<String, String> map = new Gson().fromJson(response,
                        new TypeToken<Map<String, String>>() {
                        }.getType());
                if (map.size() > 0) {
                    //获取所有的键
                    Set<String> keySet = map.keySet();
                    for (String key : keySet) {
                        //将键值对存入sp
                        scSp.edit().putString(key, map.get(key)).apply();
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        };
        String url = UrlHandler.getSnCodeMap();
        HttpUtil.sendRequestWithCallback(url, listener);
    }
}
